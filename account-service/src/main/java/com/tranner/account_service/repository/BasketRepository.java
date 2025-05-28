package com.tranner.account_service.repository;

import com.tranner.account_service.domain.Basket;
import com.tranner.account_service.type.CountryCode;
import com.tranner.account_service.type.RegionCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BasketRepository extends JpaRepository<Basket, Long> {

    List<Basket> findByMemberIdAndCountryCodeAndRegionCode(String memberId, CountryCode countryCode, RegionCode regionCode);


    List<Basket> findAllByMemberIdAndCountryCodeAndRegionCodeAndPlaceIdIn(
            String memberId,
            CountryCode countryCode,
            RegionCode regionCode,
            List<String> placeIds
    );


}
