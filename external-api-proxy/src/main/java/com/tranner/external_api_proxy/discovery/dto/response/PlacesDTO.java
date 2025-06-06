package com.tranner.external_api_proxy.discovery.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlacesDTO {

    private String placeId;
    private String placeName;
    private String placeType;
    private String photoUrl; // URL

}
