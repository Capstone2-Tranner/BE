package com.tranner.external_api_proxy.api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tranner.external_api_proxy.common.type.PlaceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class V1KeywordSearchResult {

    private String id;
    private String placeName;
    private String address;
    private double latitude;
    private double longitude;
    private PlaceType placeType;
    private String photoUrl;

}

