package com.tranner.account_service.dto.response;

import com.tranner.account_service.domain.Basket;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceDTO {
    private String placeId;
    private String placeName;
    private String placeType;
    private String address;
    private Double latitude;
    private Double longitude;
    private String photoUrl;

    public PlaceDTO(String placeId, String placeName, String placeType,
                    String address, Double latitude, Double longitude, String photoUrl) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeType = placeType;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photoUrl = photoUrl;
    }

    public static PlaceDTO fromBasketEntity(Basket entity) {
        return new PlaceDTO(
                entity.getPlaceId(),
                entity.getPlaceName(),
                entity.getPlaceType().toString(),
                entity.getAddress(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getPhotoUrl()
        );
    }

}

