package com.tranner.external_api_proxy.api.service;

import com.tranner.external_api_proxy.api.dto.response.V1KeywordSearchResponse;
import com.tranner.external_api_proxy.api.dto.response.V1KeywordSearchResult;
import com.tranner.external_api_proxy.common.type.PhotoSize;
import com.tranner.external_api_proxy.common.type.PlaceType;
import com.tranner.external_api_proxy.api.exception.ApiErrorCode;
import com.tranner.external_api_proxy.common.exception.custom.InternalServerException;
import com.tranner.external_api_proxy.common.util.PlaceTypeMappingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NearbySearchService {

    private final WebClient webClient;
    private final PhotoService photoService;

    @Value("${google.maps.api.key}")
    private String GOOGLE_PLACES_KEY;

    public Mono<V1KeywordSearchResponse> searchNearbyLegacy(Double latitude, Double longitude, @Nullable String pageToken) {
        Integer radius = 500;

        return webClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .scheme("https")
                            .host("maps.googleapis.com")
                            .path("/maps/api/place/nearbysearch/json")
                            .queryParam("location", latitude + "," + longitude)
                            .queryParam("radius", radius)
                            .queryParam("key", GOOGLE_PLACES_KEY)
                            .queryParam("language", "ko");

                    if (pageToken != null && !pageToken.isBlank()) {
                        builder.queryParam("pagetoken", pageToken);
                    }

                    return builder.build();
                })
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    String message = String.format("Google V1 Text API Error: %s\nResponse body: %s",
                                            response.statusCode(), body);
                                    return Mono.error(new com.tranner.external_api_proxy.common.exception.custom.InternalServerException(ApiErrorCode.GOOGLE_HTTP_ERROR, message));
                                })
                )
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(responseMap -> {
                    List<Map<String, Object>> places = (List<Map<String, Object>>) responseMap.getOrDefault("results", List.of());

                    List<V1KeywordSearchResult> results = places.stream().map(place -> {
                        V1KeywordSearchResult result = new V1KeywordSearchResult();

                        result.setId((String) place.get("place_id"));
                        result.setPlaceName((String) place.get("name"));
                        result.setAddress((String) place.get("vicinity"));

                        Map<String, Object> location = (Map<String, Object>) ((Map<String, Object>) place.get("geometry")).get("location");
                        if (location != null) {
                            result.setLatitude(((Number) location.get("lat")).doubleValue());
                            result.setLongitude(((Number) location.get("lng")).doubleValue());
                        }

                        List<String> types = (List<String>) place.get("types");
                        result.setPlaceType(PlaceTypeMappingUtil.classify(types));

                        List<Map<String, Object>> photos = (List<Map<String, Object>>) place.get("photos");
                        if (photos != null && !photos.isEmpty()) {
                            String photoReference = (String) photos.get(0).get("photo_reference");
                            if (photoReference != null) {
                                String photoUrl = photoService.getPhotoUrl(photoReference, PhotoSize.MIDDLE);
                                result.setPhotoUrl(photoUrl);
                            }
                        }

                        return result;
                    }).toList();

                    String nextPageToken = (String) responseMap.get("next_page_token");
                    return new V1KeywordSearchResponse(results, nextPageToken);
                })
                .onErrorResume(ex -> Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_API_REQUEST_ERROR, ex.getMessage())));
    }
}
