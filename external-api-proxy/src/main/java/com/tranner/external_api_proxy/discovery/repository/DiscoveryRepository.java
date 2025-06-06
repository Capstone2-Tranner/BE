package com.tranner.external_api_proxy.discovery.repository;

import com.tranner.external_api_proxy.common.type.CountryCode;
import com.tranner.external_api_proxy.common.type.RegionCode;
import com.tranner.external_api_proxy.discovery.domain.PopularPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscoveryRepository extends JpaRepository<PopularPlace, Long> {
    List<PopularPlace> findByCountryCodeAndRegionCode(CountryCode countryCode, RegionCode regionCode);
}
