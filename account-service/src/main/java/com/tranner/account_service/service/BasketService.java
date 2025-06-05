package com.tranner.account_service.service;

import com.tranner.account_service.domain.Basket;
import com.tranner.account_service.dto.request.DeleteBasketRequestDTO;
import com.tranner.account_service.dto.request.InsertBasketRequestDTO;
import com.tranner.account_service.dto.response.BasketResponseDTO;
import com.tranner.account_service.dto.response.PlaceDTO;
import com.tranner.account_service.repository.BasketRepository;
import com.tranner.account_service.repository.MemberRepository;
import com.tranner.account_service.type.CountryCode;
import com.tranner.account_service.type.PlaceType;
import com.tranner.account_service.type.RegionCode;
import com.tranner.account_service.util.PlaceTypeMappingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BasketService {

    private final BasketRepository basketRepository;

    /*
        1. 장바구니 조회
        2. 장바구니 아이템 삽입
        3. 장바구니 아이템 삭제
     */

    // 1. 장바구니 조회
    public BasketResponseDTO readBasket(String memberId, String countryName, String regionName){
        System.out.println("✔ [readBasket: Service] | memberId: "+memberId+", countryName: "+countryName+", regionName: "+regionName);
        // 1-1. 문자열로 온 지역/국가명을 enum으로 변환
        CountryCode countryCode = CountryCode.fromName(countryName);
        System.out.println("✔ [readBasket: Service] | countryCode: "+countryCode);
        RegionCode regionCode = RegionCode.fromName(regionName);
        System.out.println("✔ [readBasket: Service] | regionCode: "+regionCode);

        //1-2. db에서 조회
        List<Basket> basketPlaces = basketRepository.findByMemberIdAndCountryCodeAndRegionCode(memberId, countryCode, regionCode);
        System.out.println("✔ [readBasket: Service] | basketPlaces: "+basketPlaces);
        
        //1-3. PlaceDTO로 매핑
        List<PlaceDTO> places = basketPlaces.stream()
                .map(PlaceDTO::fromBasketEntity)
                .toList();
        System.out.println("✔ [readBasket: Service] | places: "+places);
        //1-4. responseDTO에 담아 전달
        BasketResponseDTO basketResponseDTO = new BasketResponseDTO(countryName, regionName, places);
        return basketResponseDTO;
    }

    // 2. 장바구니 아이템 삽입
    public void insertBasket(String memberId, InsertBasketRequestDTO insertBasketRequestDTO){

        // 2-1. 문자열로 온 지역/국가명을 enum으로 변환
        CountryCode countryCode = CountryCode.fromName(insertBasketRequestDTO.countryName());
        RegionCode regionCode = RegionCode.fromName(insertBasketRequestDTO.regionName());

        //2-2. dto -> entity 매핑
        List<Basket> basketPlaces = insertBasketRequestDTO.places().stream()
                .map(place -> Basket.builder()
                        .memberId(memberId)
                        .placeId(place.placeId())
                        .placeName(place.placeName())
                        .placeType(PlaceType.fromString(place.placeType()))
                        .address(place.address())
                        .latitude(place.latitude())
                        .longitude(place.longitude())
                        .photoUrl(place.photoUrl())
                        .countryCode(countryCode)
                        .regionCode(regionCode)
                        .build()
                ).toList();

        //2-3. db에 insert
        basketRepository.saveAll(basketPlaces);
    }

    // 3. 장바구니 아이템 삭제
    public void deleteBasket(String memberId, DeleteBasketRequestDTO deleteBasketRequestDTO){

        // 3-1. 문자열로 온 지역/국가명을 enum으로 변환
        CountryCode countryCode = CountryCode.fromName(deleteBasketRequestDTO.countryName());
        RegionCode regionCode = RegionCode.fromName(deleteBasketRequestDTO.regionName());

        // 3-2. placeId 리스트에 해당하는 장바구니 항목 조회
        List<Basket> toDelete = basketRepository.findAllByMemberIdAndCountryCodeAndRegionCodeAndPlaceIdIn(
                memberId,
                countryCode,
                regionCode,
                deleteBasketRequestDTO.placeId()
        );

        //3-3. db에서 delete
        basketRepository.deleteAll(toDelete);
    }
}
