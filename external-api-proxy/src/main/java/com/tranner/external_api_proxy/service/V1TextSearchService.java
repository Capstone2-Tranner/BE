package com.tranner.external_api_proxy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranner.external_api_proxy.type.PlaceType;
import com.tranner.external_api_proxy.dto.response.V1KeywordSearchResponse;
import com.tranner.external_api_proxy.dto.response.V1KeywordSearchResult;
import com.tranner.external_api_proxy.exception.ApiErrorCode;
import com.tranner.external_api_proxy.exception.custom.InternalServerException;
import com.tranner.external_api_proxy.util.PlaceTypeMappingUtil;
import com.tranner.external_api_proxy.util.SearchQueryProvider;
import lombok.RequiredArgsConstructor;
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

    private final String V1_TEXT_FIELDS = String.join(",",
            "places.id",
            "places.displayName",
            "places.types",
            "places.formattedAddress",
            "places.location",
            "places.photos",
            "places.rating"
            );

    public V1TextSearchService(WebClient webClient,
                              SearchQueryProvider queryProvider,
                              @Value("${google.maps.api.key}") String googlePlacesKey) {
        this.webClient = webClient;
        this.queryProvider = queryProvider;
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

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("textQuery", textQuery);
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
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(responseMap  -> {
                    ObjectMapper mapper = new ObjectMapper();

                    // 1. places 리스트 파싱
                    List<V1KeywordSearchResult> results = mapper.convertValue(
                            responseMap.getOrDefault("places", List.of()),
                            new TypeReference<>() {}
                    );

                    // 2. nextPageToken 추출
                    String nextPageToken = (String) responseMap.getOrDefault("nextPageToken", null);

                    // 3. 타입 매핑
                    for (V1KeywordSearchResult result : results) {
                        PlaceType mappedType = PlaceTypeMappingUtil.classify(result.getTypes());
                        result.setPlaceType(mappedType);
                    }

                    return new V1KeywordSearchResponse(results, nextPageToken);
                })
                // ✅ 네트워크/파싱 등 예외 잡아서 로깅 후 fallback
                .onErrorResume(ex -> {
                    return Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_API_REQUEST_ERROR, ex.getMessage()));
                });
    }

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

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("textQuery", textQuery);
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
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(responseMap  -> {
                    ObjectMapper mapper = new ObjectMapper();

                    // 1. places 리스트 파싱
                    List<V1KeywordSearchResult> results = mapper.convertValue(
                            responseMap.getOrDefault("places", List.of()),
                            new TypeReference<>() {}
                    );

                    // 2. nextPageToken 추출
                    String nextPageToken = (String) responseMap.getOrDefault("nextPageToken", null);

                    // 3. 타입 매핑
                    for (V1KeywordSearchResult result : results) {
                        PlaceType mappedType = PlaceTypeMappingUtil.classify(result.getTypes());
                        result.setPlaceType(mappedType);
                    }

                    return new V1KeywordSearchResponse(results, nextPageToken);
                })
                // ✅ 네트워크/파싱 등 예외 잡아서 로깅 후 fallback
                .onErrorResume(ex -> {
                    return Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_API_REQUEST_ERROR, ex.getMessage()));
                });
    }
}
