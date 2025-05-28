package com.tranner.account_service.type;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PlaceType {
    ATTRACTION, RESTAURANT, HOTEL, ETC;

    public static PlaceType fromString(String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid placeType: " + value));
    }
}