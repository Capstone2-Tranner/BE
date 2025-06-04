package com.tranner.external_api_proxy.api.service;

import com.tranner.external_api_proxy.api.dto.response.DetailSearchResult;
import com.tranner.external_api_proxy.api.exception.ApiErrorCode;
import com.tranner.external_api_proxy.common.exception.custom.InternalServerException;
import com.tranner.external_api_proxy.common.type.PhotoSize;
import com.tranner.external_api_proxy.common.type.PlaceType;
import com.tranner.external_api_proxy.common.util.PlaceTypeMappingUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class PlaceDetailService {

    private final PhotoService photoService;
    private final WebClient webClient;
    private final RedisService redisService;

    private static final String MODERN_DETAIL_FIELDS = String.join(",",
            "id", "displayName", "location", "formattedAddress", "types",
            "rating", "userRatingCount", "regularOpeningHours", "internationalPhoneNumber",
            "editorialSummary", "photos", "googleMapsUri"
    );

    public PlaceDetailService(PhotoService photoService,
                               RedisService redisService,
                               WebClient.Builder webClientBuilder,
                               @Value("${google.maps.api.key}") String googlePlacesKey) {
        this.photoService = photoService;
        this.redisService = redisService;
        this.webClient = webClientBuilder
                .baseUrl("https://places.googleapis.com")
                .defaultHeader("X-Goog-Api-Key", googlePlacesKey)
                .defaultHeader("X-Goog-FieldMask", MODERN_DETAIL_FIELDS)
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public DetailSearchResult getPlaceDetail(String placeId) {
        return getPlaceDetailAsync(placeId).block();
    }

    public Mono<DetailSearchResult> getPlaceDetailAsync(String placeId) {

        return redisService.getCachedDetailMono(placeId)
                .filter(cached -> cached != null)
                .switchIfEmpty(Mono.defer(() -> {

                return webClient.method(HttpMethod.GET)
                        .uri(uriBuilder -> uriBuilder
                                .path("/v1/places/" + placeId)
                                .queryParam("languageCode", "ko")
                                .build())
                        .retrieve()
                        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                                response.bodyToMono(String.class).flatMap(body -> {
                                    String message = String.format("Google Modern Detail API Error: %s\nBody: %s",
                                            response.statusCode(), body);
                                    return Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_HTTP_ERROR, message));
                                })
                        )
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                        })
                        .map(placeMap -> {
                            DetailSearchResult result = new DetailSearchResult();

                            // 1-1. id SET
                            result.setPlaceId((String) placeMap.get("id"));

                            // 1-2. name SET
                            Map<String, Object> displayName = (Map<String, Object>) placeMap.get("displayName");
                            if (displayName != null) {
                                result.setName((String) displayName.get("text"));
                            }

                            // 1-3. address SET
                            result.setAddress((String) placeMap.get("formattedAddress"));

                            // 1-4. latitude, longitude SET
                            Map<String, Object> location = (Map<String, Object>) placeMap.get("location");
                            if (location != null) {
                                result.setLatitude(((Number) location.get("latitude")).doubleValue());
                                result.setLongitude(((Number) location.get("longitude")).doubleValue());
                            }

                            // 1-5. summary SET
                            Map<String, Object> editorial = (Map<String, Object>) placeMap.get("editorialSummary");
                            if (editorial != null) {
                                result.setSummary((String) editorial.get("text"));
                            }

                            // 1-6. openingHours SET
                            Map<String, Object> hours = (Map<String, Object>) placeMap.get("regularOpeningHours");
                            if (hours != null) {
                                List<String> texts = (List<String>) hours.get("weekdayDescriptions");
                                DetailSearchResult.OpeningHours openingHours = new DetailSearchResult.OpeningHours();
                                openingHours.setWeekdayText(texts);
                                result.setOpeningHours(openingHours);
                            }

                            // 1-7. phone Number SET
                            result.setPhoneNumber((String) placeMap.get("internationalPhoneNumber"));

                            // 1-8. map URI SET
                            result.setUrl((String) placeMap.get("googleMapsUri"));

                            // 1-9. type SET
                            List<String> types = (List<String>) placeMap.get("types");
                            PlaceType mappedType = PlaceTypeMappingUtil.classify(types);
                            result.setPlaceType(mappedType);


                            // 1-10. photoUrl SET (photos 리스트 → photoReference 추출)
                            List<Map<String, Object>> photos = (List<Map<String, Object>>) placeMap.get("photos");
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
                        })
                        .doOnNext(result -> redisService.cacheDetailAsync(placeId, result))

                        .onErrorResume(ex ->
                                Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_API_REQUEST_ERROR, ex.getMessage()))
                        );
                }));
    }

}
