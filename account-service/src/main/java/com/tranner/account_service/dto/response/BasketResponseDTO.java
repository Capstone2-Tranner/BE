package com.tranner.account_service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BasketResponseDTO{

    private String countryName;
    private String regionName;
    private List<PlaceDTO> places;

    public BasketResponseDTO(String countryName, String regionName, List<PlaceDTO> places) {
        this.countryName = countryName;
        this.regionName = regionName;
        this.places = places;
    }
}
