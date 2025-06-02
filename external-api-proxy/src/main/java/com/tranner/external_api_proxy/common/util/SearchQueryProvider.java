package com.tranner.external_api_proxy.common.util;

import com.tranner.external_api_proxy.common.type.PlaceType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class SearchQueryProvider {

    private final Map<PlaceType, List<String>> queryMap = Map.of(
            PlaceType.RESTAURANT, List.of(
                    "best restaurants", "popular restaurants", "food",
                    "local food restaurants", "crab restaurant", "ramen restaurant"
            ),
            PlaceType.ATTRACTION, List.of(
                    "tourist attractions", "sightseeing spots", "landmarks",
                    "places to visit", "things to do"
            ),
            PlaceType.HOTEL, List.of(
                    "hotels", "accommodation", "guesthouse",
                    "best hotels", "budget hotels", "luxury hotels"
            )
    );

    public String getRandomQuery(PlaceType type, String region) {
        List<String> candidates = queryMap.getOrDefault(type, List.of());
        if (candidates.isEmpty()) return region;
        String keyword = candidates.get(new Random().nextInt(candidates.size()));
        return region + " " + keyword;
    }

}
