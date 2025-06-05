package com.tranner.external_api_proxy.discovery.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceSearchRequestDTO {
    private String countryName;
    private String regionName;
    private String pageToken;
}