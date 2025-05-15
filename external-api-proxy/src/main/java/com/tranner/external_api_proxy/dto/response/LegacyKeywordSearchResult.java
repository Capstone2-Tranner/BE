package com.tranner.external_api_proxy.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tranner.external_api_proxy.type.PlaceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegacyKeywordSearchResult {

    @JsonProperty("place_id")
    private String id;

    private String name;  // 구버전은 그냥 "name"

    private List<String> types;

    @JsonProperty("formatted_address")
    private String formattedAddress;

    private Geometry geometry;

    private List<Photo> photos;

    private Double rating;

    private PlaceType placeType;  // 내부 분류 타입

    @Data
    public static class Geometry {
        private Location location;
    }

    @Data
    public static class Location {
        private double lat;
        private double lng;
    }

    @Data
    public static class Photo {
        @JsonProperty("photo_reference")
        private String photoReference;
    }
}
