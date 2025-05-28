package com.tranner.account_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record DeleteBasketRequestDTO(

        @NotBlank(message = "국가명은 비어있을 수 없습니다")
        String countryName,

        @NotBlank(message = "지역명은 비어있을 수 없습니다")
        String regionName,

        @NotEmpty(message = "삭제할 placeId 목록이 비어있을 수 없습니다")
        List<@NotBlank(message = "placeId는 비어있을 수 없습니다") String> placeId

) {}
