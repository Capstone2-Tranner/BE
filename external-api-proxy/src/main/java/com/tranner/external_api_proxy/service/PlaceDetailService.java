package com.tranner.external_api_proxy.service;

import com.tranner.external_api_proxy.dto.response.DetailSearchResult;
import com.tranner.external_api_proxy.exception.ApiErrorCode;
import com.tranner.external_api_proxy.exception.custom.InternalServerException;
import com.tranner.external_api_proxy.util.SearchQueryProvider;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class PlaceDetailService {

    private final WebClient webClient;
    private final SearchQueryProvider queryProvider;
    private final String GOOGLE_PLACES_KEY;

    private static final String LEGACY_DETAIL_FIELDS = String.join(",",
            "place_id", "name", "types", "formatted_address", "geometry/location",
            "editorial_summary", "opening_hours", "formatted_phone_number",
            "rating", "url", "photos"
    );

    public PlaceDetailService(WebClient webClient,
                              SearchQueryProvider queryProvider,
                              @Value("${google.maps.api.key}") String googlePlacesKey) {
        this.webClient = webClient;
        this.queryProvider = queryProvider;
        this.GOOGLE_PLACES_KEY = googlePlacesKey;
    }


    //detail search (blocking) 장소 탐색 페이지 - 구버전
    public DetailSearchResult getPlaceDetail(String placeId) {
        return getPlaceDetailAsync(placeId).block();
    }


    //detail search (non-blocking) 여행 계획 페이지 - 구버전
    public Mono<DetailSearchResult> getPlaceDetailAsync(String placeId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("maps.googleapis.com")
                        .path("/maps/api/place/details/json")
                        .queryParam("key", GOOGLE_PLACES_KEY)
                        .queryParam("place_id", placeId)
                        .queryParam("fields", LEGACY_DETAIL_FIELDS)
                        .build())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    String message = String.format("Google Legacy Detail API Error: %s\nBody: %s",
                                            response.statusCode(), body);
                                    return Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_HTTP_ERROR, message));
                                })
                )
                .bodyToMono(new ParameterizedTypeReference<Map<String, DetailSearchResult>>() {})
                .map(resultMap -> resultMap.get("result"))
                .onErrorResume(ex ->
                        Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_API_REQUEST_ERROR, ex.getMessage()))
                );
    }


}
