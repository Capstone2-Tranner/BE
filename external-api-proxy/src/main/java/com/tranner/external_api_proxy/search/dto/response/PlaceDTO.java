package com.tranner.external_api_proxy.search.dto.response;


import com.tranner.external_api_proxy.common.type.PlaceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceDTO {

    private String placeId;
    private String placeName;
    private String placeType;
    private String photoUrl;
    private String address;
    private double latitude;
    private double longitude;



}
