package com.tranner.external_api_proxy.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranner.external_api_proxy.common.type.PhotoSize;
import com.tranner.external_api_proxy.common.type.PlaceType;
import com.tranner.external_api_proxy.api.dto.response.V1KeywordSearchResponse;
import com.tranner.external_api_proxy.api.dto.response.V1KeywordSearchResult;
import com.tranner.external_api_proxy.api.exception.ApiErrorCode;
import com.tranner.external_api_proxy.common.exception.custom.InternalServerException;
import com.tranner.external_api_proxy.common.util.PlaceTypeMappingUtil;
import com.tranner.external_api_proxy.common.util.SearchQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class V1TextSearchService{

    private final WebClient webClient;
    private final SearchQueryProvider queryProvider;
    private final String GOOGLE_PLACES_KEY;
    private final PhotoService photoService;
    private final RedisService redisService;

    private final String V1_TEXT_FIELDS = String.join(",",
            "places.id",
            "places.displayName.text",      // ✅ 언어코드는 생략 (안 써도 들어옴)
            "places.types",
            "places.formattedAddress",
            "places.location",
            "places.photos"
            );

    public V1TextSearchService(WebClient webClient,
                              SearchQueryProvider queryProvider,
                              PhotoService photoService,
                              RedisService redisService,
                              @Value("${google.maps.api.key}") String googlePlacesKey) {
        this.webClient = webClient;
        this.queryProvider = queryProvider;
        this.photoService = photoService;
        this.redisService = redisService;
        this.GOOGLE_PLACES_KEY = googlePlacesKey;
    }

    /**
     * param: '지역', '검색어'
     * return:
     */
    // 1. text search (blocking) 여행 계획 페이지 - 신버전
    public V1KeywordSearchResponse search(String region, String keyword, @Nullable String pageToken) {
        return searchAsync(region, keyword, pageToken).block(); // 비동기 메서드를 wrapping
    }

    /**
     * param: '지역', '검색어'
     * return:
     */
    // 2. text search (non-blocking) 여행 계획 페이지 - 신버전
    public Mono<V1KeywordSearchResponse> searchAsync(String region, String keyword, @Nullable String pageToken) {
        String textQuery = region + " " + keyword;

        return redisService.getCachedTextSearchMono(textQuery, pageToken)
                .filter(cached -> cached != null)
                .switchIfEmpty(Mono.defer(() -> {
                    Map<String, Object> bodyMap = new HashMap<>();
                    bodyMap.put("textQuery", textQuery);
                    bodyMap.put("languageCode", "ko");
                    bodyMap.put("pageSize", 20);
                    if (pageToken != null) {
                        bodyMap.put("pageToken", pageToken);
                    }

                    return webClient.post()
                            .uri(uriBuilder -> uriBuilder
                                    .scheme("https")
                                    .host("places.googleapis.com")
                                    .path("/v1/places:searchText")
                                    .queryParam("key", GOOGLE_PLACES_KEY)
                                    .queryParam("fields", "*")
                                    .build())
                            .bodyValue(bodyMap)
                            .retrieve()
                            // ✅ HTTP 상태 코드 기반 에러 처리
                            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                                    response.bodyToMono(String.class)
                                            .flatMap(body -> {
                                                String message = String.format("Google V1 Text API Error: %s\nResponse body: %s",
                                                        response.statusCode(), body);
                                                return Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_HTTP_ERROR, message));
                                            })
                            )
                            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                            })
                            .map(responseMap -> {
                                ObjectMapper mapper = new ObjectMapper();

                                // 1. places 리스트 파싱 (직접 수동 매핑)
                                List<Map<String, Object>> places = (List<Map<String, Object>>) responseMap.getOrDefault("places", List.of());

                                List<V1KeywordSearchResult> results = places.stream().map(place -> {
                                    V1KeywordSearchResult result = new V1KeywordSearchResult();

                                    // 1-1. place Id SET
                                    result.setId((String) place.get("id"));

                                    // 1-2. place Name SET
                                    Map<String, Object> displayName = (Map<String, Object>) place.get("displayName");
                                    if (displayName != null) {
                                        result.setPlaceName((String) displayName.get("text"));
                                    }

                                    // 1-3. address SET
                                    result.setAddress((String) place.get("formattedAddress"));

                                    // 1-4. location SET
                                    Map<String, Object> locationMap = (Map<String, Object>) place.get("location");
                                    if (locationMap != null) {
                                        Double lat = locationMap.get("latitude") instanceof Number ? ((Number) locationMap.get("latitude")).doubleValue() : null;
                                        Double lng = locationMap.get("longitude") instanceof Number ? ((Number) locationMap.get("longitude")).doubleValue() : null;

                                        result.setLatitude(lat);
                                        result.setLongitude(lng);
                                    }

                                    // 1-5. type SET
                                    // 타입 매핑
                                    List<String> types = (List<String>) place.get("types");
                                    PlaceType mappedType = PlaceTypeMappingUtil.classify(types);
                                    result.setPlaceType(mappedType);

                                    // 1-6. photoUrl SET
                                    List<Map<String, Object>> photos = (List<Map<String, Object>>) place.get("photos");
                                    if (photos != null && !photos.isEmpty()) {
                                        String fullName = (String) photos.get(0).get("name");
                                        String photoReference = null;

                                        if (fullName != null && fullName.contains("photos/")) {
                                            photoReference = fullName.substring(fullName.indexOf("photos/") + "photos/".length());
                                        }
                                        // photoService 호출
                                        String photoUrl = null;
                                        if (photoReference != null) {
                                            photoUrl = photoService.getPhotoUrl(photoReference, PhotoSize.MIDDLE);
                                        }
                                        result.setPhotoUrl(photoUrl);
                                    }

                                    return result;
                                }).toList();

                                // 2. nextPageToken 추출
                                String nextPageToken = (String) responseMap.getOrDefault("nextPageToken", null);
                                System.out.println("🔵 nextPageToken = " + nextPageToken);

                                V1KeywordSearchResponse response = new V1KeywordSearchResponse(results, nextPageToken);
                                return response;
                            })
                            .doOnNext(response -> redisService.cacheTextSearch(textQuery, pageToken, response))
                            // ✅ 네트워크/파싱 등 예외 잡아서 로깅 후 fallback
                            .onErrorResume(ex -> {
                                return Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_API_REQUEST_ERROR, ex.getMessage()));
                            });

                }));
    }

//    public Mono<V1KeywordSearchResponse> searchAsync(String region, String keyword, @Nullable String pageToken) {
//        String textQuery = region + " " + keyword;
//
//        return redisService.getCachedTextSearchMono(textQuery, pageToken)
//                .filter(cached -> cached != null)
//                .switchIfEmpty(Mono.defer(() -> {
//                    Map<String, Object> bodyMap = new HashMap<>();
//                    bodyMap.put("textQuery", textQuery);
//                    bodyMap.put("languageCode", "ko");
//                    bodyMap.put("pageSize", 20);
//                    if (pageToken != null) {
//                        bodyMap.put("pageToken", pageToken);
//                    }
//
//                    return webClient.post()
//                            .uri(uriBuilder -> uriBuilder
//                                    .scheme("https")
//                                    .host("places.googleapis.com")
//                                    .path("/v1/places:searchText")
//                                    .queryParam("key", GOOGLE_PLACES_KEY)
//                                    .queryParam("fields", "*")
//                                    .build())
//                            .bodyValue(bodyMap)
//                            .retrieve()
//                            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
//                                    response.bodyToMono(String.class).flatMap(body -> {
//                                        String message = String.format("Google V1 Text API Error: %s\nResponse body: %s",
//                                                response.statusCode(), body);
//                                        return Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_HTTP_ERROR, message));
//                                    })
//                            )
//                            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
//                            .map(responseMap -> {
//                                List<Map<String, Object>> places = (List<Map<String, Object>>) responseMap.getOrDefault("places", List.of());
//
//                                List<V1KeywordSearchResult> results = places.stream().map(place -> {
//                                    V1KeywordSearchResult result = new V1KeywordSearchResult();
//                                    result.setId((String) place.get("id"));
//
//                                    Map<String, Object> displayName = (Map<String, Object>) place.get("displayName");
//                                    if (displayName != null) result.setPlaceName((String) displayName.get("text"));
//
//                                    result.setAddress((String) place.get("formattedAddress"));
//
//                                    Map<String, Object> locationMap = (Map<String, Object>) place.get("location");
//                                    if (locationMap != null) {
//                                        Double lat = locationMap.get("latitude") instanceof Number ? ((Number) locationMap.get("latitude")).doubleValue() : null;
//                                        Double lng = locationMap.get("longitude") instanceof Number ? ((Number) locationMap.get("longitude")).doubleValue() : null;
//                                        result.setLatitude(lat);
//                                        result.setLongitude(lng);
//                                    }
//
//                                    List<String> types = (List<String>) place.get("types");
//                                    PlaceType mappedType = PlaceTypeMappingUtil.classify(types);
//                                    result.setPlaceType(mappedType);
//
//                                    List<Map<String, Object>> photos = (List<Map<String, Object>>) place.get("photos");
//                                    if (photos != null && !photos.isEmpty()) {
//                                        String fullName = (String) photos.get(0).get("name");
//                                        String photoReference = null;
//                                        if (fullName != null && fullName.contains("photos/")) {
//                                            photoReference = fullName.substring(fullName.indexOf("photos/") + "photos/".length());
//                                        }
//                                        String photoUrl = photoReference != null ? photoService.getPhotoUrl(photoReference, PhotoSize.MIDDLE) : null;
//                                        result.setPhotoUrl(photoUrl);
//                                    }
//
//                                    return result;
//                                }).toList();
//
//                                String nextPageToken = (String) responseMap.getOrDefault("nextPageToken", null);
//                                V1KeywordSearchResponse response = new V1KeywordSearchResponse(results, nextPageToken);
//
//                                redisService.cacheTextSearch(textQuery, pageToken, response);
//                                return response;
//                            })
//                            .onErrorResume(ex ->
//                                    Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_API_REQUEST_ERROR, ex.getMessage()))
//                            );
//                }));
//    }


    /**
     * param: '지역', 타입(명소,맛집,숙소,기타)
     * return:
     */
    // 3. text search (blocking) 여행 계획 페이지 - 신버전
    public V1KeywordSearchResponse searchByType(String region, PlaceType type, @Nullable String pageToken) {
        return searchByTypeAsync(region, type, pageToken).block(); // 비동기 메서드를 wrapping
    }

    /**
     * param: '지역', 타입(명소,맛집,숙소,기타)
     * return:
     */
    // 4. text search (non-blocking) 여행 계획 페이지 - 신버전
    public Mono<V1KeywordSearchResponse> searchByTypeAsync(String region, PlaceType type, @Nullable String pageToken) {
        String textQuery = queryProvider.getRandomQuery(type, region);

        return redisService.getCachedTextSearchMono(textQuery, pageToken)
                .filter(cached -> cached != null)
                .switchIfEmpty(Mono.defer(() -> {
                    Map<String, Object> bodyMap = new HashMap<>();
                    bodyMap.put("textQuery", textQuery);
                    bodyMap.put("languageCode", "ko");
                    bodyMap.put("pageSize", 20);
                    if (pageToken != null) {
                        bodyMap.put("pageToken", pageToken);
                    }

                    return webClient.post()
                            .uri(uriBuilder -> uriBuilder
                                    .scheme("https")
                                    .host("places.googleapis.com")
                                    .path("/v1/places:searchText")
                                    .queryParam("key", GOOGLE_PLACES_KEY)
                                    .queryParam("fields", V1_TEXT_FIELDS)
                                    .build())
                            .bodyValue(bodyMap)
                            .retrieve()
                            // ✅ HTTP 상태 코드 기반 에러 처리
                            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                                    response.bodyToMono(String.class)
                                            .flatMap(body -> {
                                                String message = String.format("Google V1 Text API Error: %s\nResponse body: %s",
                                                        response.statusCode(), body);
                                                return Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_HTTP_ERROR, message));
                                            })
                            )
                            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                            })
                            .map(responseMap -> {
                                ObjectMapper mapper = new ObjectMapper();

                                // 1. places 리스트 파싱 (직접 수동 매핑)
                                List<Map<String, Object>> places = (List<Map<String, Object>>) responseMap.getOrDefault("places", List.of());

                                List<V1KeywordSearchResult> results = places.stream().map(place -> {
                                    V1KeywordSearchResult result = new V1KeywordSearchResult();

                                    // 1-1. place Id SET
                                    result.setId((String) place.get("id"));

                                    // 1-2. place Name SET
                                    Map<String, Object> displayName = (Map<String, Object>) place.get("displayName");
                                    if (displayName != null) {
                                        result.setPlaceName((String) displayName.get("text"));
                                    }

                                    // 1-3. address SET
                                    result.setAddress((String) place.get("formattedAddress"));

                                    // 1-4. location SET
                                    Map<String, Object> locationMap = (Map<String, Object>) place.get("location");
                                    if (locationMap != null) {
                                        Double lat = locationMap.get("latitude") instanceof Number ? ((Number) locationMap.get("latitude")).doubleValue() : null;
                                        Double lng = locationMap.get("longitude") instanceof Number ? ((Number) locationMap.get("longitude")).doubleValue() : null;

                                        result.setLatitude(lat);
                                        result.setLongitude(lng);
                                    }

                                    // 1-5. type SET
                                    // 타입 매핑
                                    List<String> types = (List<String>) place.get("types");
                                    PlaceType mappedType = PlaceTypeMappingUtil.classify(types);
                                    result.setPlaceType(mappedType);

                                    // 1-6. photoUrl SET
                                    List<Map<String, Object>> photos = (List<Map<String, Object>>) place.get("photos");
                                    if (photos != null && !photos.isEmpty()) {
                                        String photoReference = (String) photos.get(0).get("name");

                                        // photoService 호출
                                        String photoUrl = photoService.getPhotoUrl(photoReference, PhotoSize.MIDDLE);

                                        result.setPhotoUrl(photoUrl);
                                    }

                                    return result;
                                }).toList();

                                // 2. nextPageToken 추출
                                String nextPageToken = (String) responseMap.getOrDefault("nextPageToken", null);
                                System.out.println("🔵 nextPageToken = " + nextPageToken);

                                V1KeywordSearchResponse response = new V1KeywordSearchResponse(results, nextPageToken);
                                return response;
                            })
                            .doOnNext(response -> redisService.cacheTextSearch(textQuery, pageToken, response))
                            // ✅ 네트워크/파싱 등 예외 잡아서 로깅 후 fallback
                            .onErrorResume(ex -> {
                                return Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_API_REQUEST_ERROR, ex.getMessage()));
                            });
                }));
    }

    /**
     * @param textQuery
     * @param latitude
     * @param longitude
     * @param pageToken
     * @return
     */
    // 4. text search (non-blocking) 여행 계획 페이지 - 신버전
    public Mono<V1KeywordSearchResponse> searInPlan(String textQuery, Double latitude, Double longitude, @Nullable String pageToken) {
        //location 생성

        return redisService.getCachedTextSearchMono(textQuery, pageToken)
                .filter(cached -> cached != null)
                .switchIfEmpty(Mono.defer(() -> {
                    Map<String, Object> bodyMap = new HashMap<>();
                    bodyMap.put("textQuery", textQuery);
                    bodyMap.put("languageCode", "ko");
                    bodyMap.put("pageSize", 20);
                    if (pageToken != null) {
                        bodyMap.put("pageToken", pageToken);
                    }
                    if (latitude != null && longitude != null) {
                        double formattedLat = Double.parseDouble(String.format("%.6f", latitude));
                        double formattedLng = Double.parseDouble(String.format("%.6f", longitude));

                        Map<String, Object> center = new HashMap<>();
                        center.put("latitude", latitude);
                        center.put("longitude", longitude);

                        Map<String, Object> circle = new HashMap<>();
                        circle.put("center", center);
                        circle.put("radius", 2000.0); // 반경 2km

                        Map<String, Object> locationBias = new HashMap<>();
                        locationBias.put("circle", circle);

                        bodyMap.put("locationBias", locationBias);
                    }

                    return webClient.post()
                            .uri(uriBuilder -> uriBuilder
                                    .scheme("https")
                                    .host("places.googleapis.com")
                                    .path("/v1/places:searchText")
                                    .queryParam("key", GOOGLE_PLACES_KEY)
                                    .queryParam("fields", V1_TEXT_FIELDS)
                                    .build())
                            .bodyValue(bodyMap)
                            .retrieve()
                            // ✅ HTTP 상태 코드 기반 에러 처리
                            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                                    response.bodyToMono(String.class)
                                            .flatMap(body -> {
                                                String message = String.format("Google V1 Text API Error: %s\nResponse body: %s",
                                                        response.statusCode(), body);
                                                return Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_HTTP_ERROR, message));
                                            })
                            )
                            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                            })
                            .map(responseMap -> {
                                ObjectMapper mapper = new ObjectMapper();

                                // 1. places 리스트 파싱 (직접 수동 매핑)
                                List<Map<String, Object>> places = (List<Map<String, Object>>) responseMap.getOrDefault("places", List.of());

                                List<V1KeywordSearchResult> results = places.stream().map(place -> {
                                    V1KeywordSearchResult result = new V1KeywordSearchResult();

                                    // 1-1. place Id SET
                                    result.setId((String) place.get("id"));

                                    // 1-2. place Name SET
                                    Map<String, Object> displayName = (Map<String, Object>) place.get("displayName");
                                    if (displayName != null) {
                                        result.setPlaceName((String) displayName.get("text"));
                                    }

                                    // 1-3. address SET
                                    result.setAddress((String) place.get("formattedAddress"));

                                    // 1-4. location SET
                                    Map<String, Object> locationMap = (Map<String, Object>) place.get("location");
                                    if (locationMap != null) {
                                        Double lat = locationMap.get("latitude") instanceof Number ? ((Number) locationMap.get("latitude")).doubleValue() : null;
                                        Double lng = locationMap.get("longitude") instanceof Number ? ((Number) locationMap.get("longitude")).doubleValue() : null;

                                        result.setLatitude(lat);
                                        result.setLongitude(lng);
                                    }

                                    // 1-5. type SET
                                    // 타입 매핑
                                    List<String> types = (List<String>) place.get("types");
                                    PlaceType mappedType = PlaceTypeMappingUtil.classify(types);
                                    result.setPlaceType(mappedType);

                                    // 1-6. photoUrl SET
                                    List<Map<String, Object>> photos = (List<Map<String, Object>>) place.get("photos");
                                    if (photos != null && !photos.isEmpty()) {
                                        String photoReference = (String) photos.get(0).get("name");

                                        // photoService 호출
                                        String photoUrl = photoService.getPhotoUrl(photoReference, PhotoSize.MIDDLE);

                                        result.setPhotoUrl(photoUrl);
                                    }

                                    return result;
                                }).toList();

                                // 2. nextPageToken 추출
                                String nextPageToken = (String) responseMap.getOrDefault("nextPageToken", null);
                                System.out.println("🔵 nextPageToken = " + nextPageToken);

                                V1KeywordSearchResponse response = new V1KeywordSearchResponse(results, nextPageToken);
                                return response;
                            })
                            .doOnNext(response -> redisService.cacheTextSearch(textQuery, pageToken, response))
                            // ✅ 네트워크/파싱 등 예외 잡아서 로깅 후 fallback
                            .onErrorResume(ex -> {
                                return Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_API_REQUEST_ERROR, ex.getMessage()));
                            });
                }));
    }
}
