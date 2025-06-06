package com.tranner.external_api_proxy.discovery.service;

import com.tranner.external_api_proxy.api.dto.response.DetailSearchResult;
import com.tranner.external_api_proxy.api.dto.response.V1KeywordSearchResponse;
import com.tranner.external_api_proxy.api.service.PlaceDetailService;
import com.tranner.external_api_proxy.api.service.V1TextSearchService;
import com.tranner.external_api_proxy.common.type.CountryCode;
import com.tranner.external_api_proxy.common.type.PlaceType;
import com.tranner.external_api_proxy.common.type.RegionCode;
import com.tranner.external_api_proxy.discovery.domain.PopularPlace;
import com.tranner.external_api_proxy.discovery.dto.response.DetailResponseDTO;
import com.tranner.external_api_proxy.discovery.dto.response.PlaceListResponseDTO;
import com.tranner.external_api_proxy.discovery.dto.response.PlacesDTO;
import com.tranner.external_api_proxy.discovery.repository.DiscoveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscoveryService {

    //private final PlaceRepository placeRepository;
    private final V1TextSearchService v1TextSearchService;
    private final PlaceDetailService placeDetailService;
    private final DiscoveryRepository discoveryRepository;

//    public PlaceListResponseDTO getPlaces(String countryName, String regionName, @Nullable String pageToken) {
//
//        // 3. Google Places Text Search 호출 (동기 처리)
//        System.out.println("3. Google Places API 호출 시작 (region: " + regionName + ", pageToken: " + pageToken + ")");
//        String regionName_eng = RegionCode.getEnglishNameFromKorean(regionName);
//        V1KeywordSearchResponse response = v1TextSearchService.searchAsync(
//                regionName_eng,
//                "attraction",  // 또는 "맛집" 등 키워드
//                pageToken
//        ).block(); // 동기 처리
//
//        if (response == null) {
//            throw new RuntimeException("Google Places API 응답이 null입니다.");
//        }
//        System.out.println("3. Google Places API 응답 수신 완료");
//
//        // 4. 응답 매핑
//        List<PlacesDTO> dtoList = response.getPlaces().stream()
//                .map(place -> PlacesDTO.builder()
//                        .placeId(place.getId())
//                        .placeName(place.getPlaceName())
//                        .placeType(place.getPlaceType().toString())
//                        .photo(place.getPhotoUrl())
//                        .build())
//                .toList();
//        System.out.println("4. DTO 변환 완료, 총 개수: " + dtoList.size());
//
//        // 5. 최종 응답 객체 생성
//        PlaceListResponseDTO result = PlaceListResponseDTO.builder()
//                .places(dtoList)
//                .pageToken(response.getNextPageToken())
//                .build();
//        System.out.println("5. 응답 객체 생성 완료, pageToken: " + response.getNextPageToken());
//
//        return result;
//    }

    public Mono<PlaceListResponseDTO> getPlaces(String countryName, String regionName, @Nullable String pageToken) {
        /**
         * 임시 코드
         */
//        CountryCode countryCode = CountryCode.fromName(countryName);
//        RegionCode regionCode = RegionCode.fromName(regionName);

        // 3. Google Places Text Search 호출 (동기 처리)
        System.out.println("3. Google Places API 호출 시작 (region: " + regionName + ", pageToken: " + pageToken + ")");
        String regionName_eng = RegionCode.getEnglishNameFromKorean(regionName);

        return v1TextSearchService.searchAsync(regionName_eng, "attraction", pageToken)
                .map(response -> {
                    if (response == null) {
                        throw new RuntimeException("Google Places API 응답이 null입니다.");
                    }

                    /**
                     * 잠깐 popular table에 추가하는 코드
                     */
//                    List<PopularPlace> entities = response.getPlaces().stream()
//                            .map(result -> PopularPlace.builder()
//                                    .placeId(result.getId())
//                                    .placeName(result.getPlaceName())
//                                    .placeType(result.getPlaceType())
//                                    .photoUrl(result.getPhotoUrl())
//                                    .countryCode(countryCode)
//                                    .regionCode(regionCode)
//                                    .build())
//                            .toList();
//                    discoveryRepository.saveAll(entities);
                    /**
                     * 여기까지 임시 코드
                     */

                    // 4. 응답 매핑
                    List<PlacesDTO> dtoList = response.getPlaces().stream()
                            .map(place -> PlacesDTO.builder()
                                    .placeId(place.getId())
                                    .placeName(place.getPlaceName())
                                    .placeType(place.getPlaceType().toString())
                                    .photo(place.getPhotoUrl())
                                    .build())
                            .toList();
                    System.out.println("4. DTO 변환 완료, 총 개수: " + dtoList.size());

                    // 5. 최종 응답 객체 생성
                    PlaceListResponseDTO result = PlaceListResponseDTO.builder()
                            .places(dtoList)
                            .pageToken(response.getNextPageToken())
                            .build();
                    System.out.println("5. 응답 객체 생성 완료, pageToken: " + response.getNextPageToken());

                    return result;

                });
    }


//    public DetailResponseDTO getDetails (String placeId) {
//
//        // 3. Google Places Text Search 호출 (동기 처리)
//        System.out.println("3. Google Places API 호출 시작 (placeId: " + placeId +")");
//
//        DetailSearchResult response = placeDetailService.getPlaceDetailAsync(placeId).block(); // 동기 처리
//
//        if (response == null) {
//            throw new RuntimeException("Google Places API 응답이 null입니다.");
//        }
//        System.out.println("3. Google Places API 응답 수신 완료");
//
//        // 4. 응답 매핑
//        DetailResponseDTO result = DetailResponseDTO.builder()
//                .placeId(response.getPlaceId())
//                .name(response.getName())
//                .address(response.getAddress())
//                .latitude(response.getLatitude())
//                .longitude(response.getLongitude())
//                .summary(response.getSummary())
//                .phoneNumber(response.getPhoneNumber())
//                .url(response.getUrl())
//                .placeType(response.getPlaceType())
//                .photoUrl(response.getPhotoUrl())
//                .openingHours(
//                        response.getOpeningHours() != null ?
//                                new DetailResponseDTO.OpeningHours(
//                                        response.getOpeningHours().getWeekdayText()
//                                ) : null
//                )
//                .build();
//
//
//        return result;
//    }
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

    public List<PlacesDTO> getPopularPlaces(String countryName, String regionName){
        CountryCode countryCode = CountryCode.fromName(countryName);
        System.out.println("✔ [popularPlaces: Service] | countryCode: "+countryCode);
        RegionCode regionCode = RegionCode.fromName(regionName);
        System.out.println("✔ [popularPlaces: Service] | regionCode: "+regionCode);

        List<PopularPlace> popularPlaces = discoveryRepository.findByCountryCodeAndRegionCode(countryCode, regionCode);

        List<PlacesDTO> places = popularPlaces.stream()
                .map(place -> PlacesDTO.builder()
                        .placeId(place.getPlaceId())
                        .placeName(place.getPlaceName())
                        .placeType(place.getPlaceType().name()) // enum → String
                        .photo(place.getPhotoUrl())
                        .build())
                .toList();

        return places;
    }



}
