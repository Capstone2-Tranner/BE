package com.tranner.external_api_proxy.common.util;

import com.tranner.external_api_proxy.common.type.PlaceType;

import java.util.List;
import java.util.Set;

public class PlaceTypeMappingUtil {

    private final static Set<String> ATTRACTION_TYPES = Set.of("tourist_attraction", "museum", "amusement_park", "zoo", "park", "landmark", "point_of_interest");
    private final static Set<String> RESTAURANT_TYPES = Set.of("restaurant", "cafe", "bakery", "bar", "food");
    private final static Set<String> HOTEL_TYPES = Set.of("lodging", "hotel", "guest_house", "motel", "resort");

    public static PlaceType classify(List<String> googleTypes) {
        if (googleTypes == null || googleTypes.isEmpty()) {
            return PlaceType.ETC;
        }

        if (googleTypes.stream().anyMatch(HOTEL_TYPES::contains)) {
            return PlaceType.HOTEL;
        }
        if (googleTypes.stream().anyMatch(RESTAURANT_TYPES::contains)) {
            return PlaceType.RESTAURANT;
        }
        if (googleTypes.stream().anyMatch(ATTRACTION_TYPES::contains)) {
            return PlaceType.ATTRACTION;
        }

        return PlaceType.ETC;
    }

    private PlaceTypeMappingUtil() {} // static 유틸 방지용
}
