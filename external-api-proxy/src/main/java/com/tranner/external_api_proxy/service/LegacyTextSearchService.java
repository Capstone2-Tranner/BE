package com.tranner.external_api_proxy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranner.external_api_proxy.dto.response.LegacyKeywordSearchResult;
import com.tranner.external_api_proxy.dto.response.V1KeywordSearchResponse;
import com.tranner.external_api_proxy.dto.response.V1KeywordSearchResult;
import com.tranner.external_api_proxy.exception.custom.InternalServerException;
import com.tranner.external_api_proxy.exception.ApiErrorCode;
import com.tranner.external_api_proxy.util.PlaceTypeMappingUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class LegacyTextSearchService {

    private final WebClient webClient;

    private final String GOOGLE_PLACES_KEY;

    private final int RADIUS_METERS = 5;

    public LegacyTextSearchService(WebClient webClient,
                                   @Value("${google.maps.api.key}") String googlePlacesKey) {
        this.webClient = webClient;
        this.GOOGLE_PLACES_KEY = googlePlacesKey;
    }

    /**
     * param: '지역', '검색어'
     * return: List<KeywordSearchResult>
     * text search (blocking) 공통 - 구버전
     */
    public List<LegacyKeywordSearchResult> search(String region, String keyword) {
        return searchAsync(region, keyword).block();
    }

    /**
     * param: '지역', '검색어'
     * return: Mono<List<KeywordSearchResult>>
     * text search (non-blocking) 공통 - 구버전
     */
    public Mono<List<LegacyKeywordSearchResult>> searchAsync(String region, String keyword) {
        String query = region + " " + keyword;

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("maps.googleapis.com")
                        .path("/maps/api/place/textsearch/json")
                        .queryParam("key", GOOGLE_PLACES_KEY)
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    String message = String.format("Google Legacy Text API Error: %s\nResponse body: %s",
                                            response.statusCode(), body);
                                    return Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_HTTP_ERROR, message));
                                })
                )
                .bodyToMono(new ParameterizedTypeReference<Map<String, List<LegacyKeywordSearchResult>>>() {})
                .map(resultMap -> resultMap.getOrDefault("results", List.of()))
                .onErrorResume(ex ->
                        Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_API_REQUEST_ERROR, ex.getMessage()))
                );
    }

    /**
     * param: '지역', '검색어', 'location', 'radius'
     * return: Mono<V1KeywordSearchResponse>
     * text search (blocking) 공통 - 구버전
     */

    /**
     * param: '지역', '검색어', 'location', 'radius'
     * return: Mono<V1KeywordSearchResponse>
     * text search (non-blocking) 공통 - 구버전
     */
    public Mono<V1KeywordSearchResponse> legacyTextSearchAsync(String query, double lat, double lng, @Nullable String pageToken) {
        return webClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .scheme("https")
                            .host("maps.googleapis.com")
                            .path("/maps/api/place/textsearch/json")
                            .queryParam("query", query)
                            .queryParam("location", lat + "," + lng)
                            .queryParam("radius", RADIUS_METERS)
                            .queryParam("key", GOOGLE_PLACES_KEY);
                    if (pageToken != null) {
                        builder.queryParam("pagetoken", pageToken);
                    }
                    return builder.build();
                })
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    String message = String.format("Google Legacy Text API Error: %s\nResponse body: %s",
                                            response.statusCode(), body);
                                    return Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_HTTP_ERROR, message));
                                })
                )
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(map -> {
                    ObjectMapper mapper = new ObjectMapper();

                    List<V1KeywordSearchResult> results = mapper.convertValue(
                            map.getOrDefault("results", List.of()),
                            new TypeReference<>() {}
                    );
                    for (V1KeywordSearchResult r : results) {
                        r.setPlaceType(PlaceTypeMappingUtil.classify(r.getTypes()));
                    }

                    return new V1KeywordSearchResponse(results, (String) map.get("next_page_token"));
                })
                .onErrorResume(ex -> Mono.error(new InternalServerException(ApiErrorCode.GOOGLE_API_REQUEST_ERROR, ex.getMessage())));
    }


}
