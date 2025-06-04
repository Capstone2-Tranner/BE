package com.tranner.account_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record InsertBasketRequestDTO(

        @NotBlank(message = "국가명은 비어있을 수 없습니다")
        String countryName,

        @NotBlank(message = "지역명은 비어있을 수 없습니다")
        String regionName,

        @NotEmpty(message = "장소 목록은 비어있을 수 없습니다")
        List<@NotNull PlaceDTO> places

) {

    public record PlaceDTO(
            @NotBlank(message = "placeId를 입력해주세요")
            String placeId,

            @NotBlank(message = "장소 이름을 입력해주세요")
            String placeName,

            @NotBlank(message = "장소 타입을 입력해주세요")
            String placeType,

            @NotBlank(message = "주소를 입력해주세요")
            String address,

            @NotNull(message = "위도를 입력해주세요")
            Double latitude,

            @NotNull(message = "경도를 입력해주세요")
            Double longitude,

            String photoUrl
    ) {}
}
