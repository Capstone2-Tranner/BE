package com.tranner.external_api_proxy.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearbySearchRequest {
    private LocationRestriction locationRestriction;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocationRestriction {
        private Circle circle;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Circle {
        private LatLng center;
        private double radius; // in meters
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LatLng {
        private double latitude;
        private double longitude;
    }
}

