package com.tranner.external_api_proxy.search.service;

import com.tranner.external_api_proxy.api.dto.response.DetailSearchResult;
import com.tranner.external_api_proxy.api.dto.response.V1KeywordSearchResponse;
import com.tranner.external_api_proxy.api.service.PlaceDetailService;
import com.tranner.external_api_proxy.api.service.V1TextSearchService;
import com.tranner.external_api_proxy.common.type.RegionCode;
import com.tranner.external_api_proxy.discovery.dto.response.DetailResponseDTO;
import com.tranner.external_api_proxy.discovery.dto.response.PlaceListResponseDTO;
import com.tranner.external_api_proxy.search.dto.response.PlaceDTO;
import com.tranner.external_api_proxy.search.dto.response.SearchResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final V1TextSearchService v1TextSearchService;
    private final PlaceDetailService placeDetailService;

    public SearchResponseDTO searchByText(String text, Double latitude, Double longitude, @Nullable String pageToken) {

        // 3. Google Places Text Search 호출 (동기 처리)
        System.out.println("3. Google Places API 호출 시작 (text: " + text + ", pageToken: " + pageToken + ")");

        // 5. 최종 응답 객체 생성
        SearchResponseDTO result = search(text, latitude, longitude, pageToken);

        return result;
    }

    public SearchResponseDTO searchByType (String type, Double latitude, Double longitude, @Nullable String pageToken) {

        // 3. Google Places Text Search 호출 (동기 처리)
        System.out.println("3. Google Places API 호출 시작 (text: " + type + ", pageToken: " + pageToken + ")");

        SearchResponseDTO result = search(type, latitude, longitude, pageToken);

        return result;
    }

    private SearchResponseDTO search (String text, Double latitude, Double longitude, @Nullable String pageToken) {

        V1KeywordSearchResponse response = v1TextSearchService.searInPlan(
                text,
                latitude,
                longitude,
                pageToken
        ).block(); // 동기 처리

        if (response == null) {
            throw new RuntimeException("Google Places API 응답이 null입니다.");
        }
        System.out.println("3. Google Places API 응답 수신 완료");

        // 4. 응답 매핑
        List<PlaceDTO> dtoList = response.getPlaces().stream()
                .map(place -> PlaceDTO.builder()
                        .placeId(place.getId())
                        .placeName(place.getPlaceName())
                        .placeType(place.getPlaceType().toString())
                        .photoUrl(place.getPhotoUrl())
                        .address(place.getAddress())
                        .latitude(place.getLatitude())
                        .longitude(place.getLongitude())
                        .build())
                .toList();
        System.out.println("4. DTO 변환 완료, 총 개수: " + dtoList.size());

        // 5. 최종 응답 객체 생성
        SearchResponseDTO result = SearchResponseDTO.builder()
                .places(dtoList)
                .pageToken(response.getNextPageToken())
                .build();
        System.out.println("5. 응답 객체 생성 완료, pageToken: " + response.getNextPageToken());

        return result;
    }



    public Mono<DetailResponseDTO> getDetails(String placeId) {

        // 3. Google Places Text Search 호출 (동기 처리)
        System.out.println("3. Google Places API 호출 시작 (placeId: " + placeId + ")");

        return placeDetailService.getPlaceDetailAsync(placeId)
                .map(response -> {
                    if (response == null) {
                        throw new RuntimeException("Google Places API 응답이 null입니다.");
                    }

                    System.out.println("3. Google Places API 응답 수신 완료");

                    // 4. 응답 매핑
                    DetailResponseDTO result = DetailResponseDTO.builder()
                            .placeId(response.getPlaceId())
                            .name(response.getName())
                            .address(response.getAddress())
                            .latitude(response.getLatitude())
                            .longitude(response.getLongitude())
                            .summary(response.getSummary())
                            .phoneNumber(response.getPhoneNumber())
                            .url(response.getUrl())
                            .placeType(response.getPlaceType())
                            .photoUrl(response.getPhotoUrl())
                            .openingHours(
                                    response.getOpeningHours() != null ?
                                            new DetailResponseDTO.OpeningHours(
                                                    response.getOpeningHours().getWeekdayText()
                                            ) : null
                            )
                            .build();

                    return result;
                });
    }



}
