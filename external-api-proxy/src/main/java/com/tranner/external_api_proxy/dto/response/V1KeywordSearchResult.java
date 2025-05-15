package com.tranner.external_api_proxy.dto.response;

import com.tranner.external_api_proxy.type.PlaceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class V1KeywordSearchResult {

    private String id;
    private DisplayName displayName;
    private List<String> types;
    private String formattedAddress;
    private Location location;
    private List<Photo> photos;
    private Double rating;
    private PlaceType placeType;

    @Data
    public static class DisplayName {
        private String text;
    }

    @Data
    public static class Location {
        private double latitude;
        private double longitude;
    }

    @Data
    public static class Photo {
        private String name; // photo reference
    }
}

