package com.tranner.external_api_proxy.discovery.dto.response;

import com.tranner.external_api_proxy.common.type.PlaceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailResponseDTO {
    private String placeId;                         // "place_id"
    private String name;                            // "name" (한국어 이름 포함)
    private String address;                          // 주소
    private Double latitude;                        // 위도
    private Double longitude;                       // 경도
    private String summary;      // 장소 개요
    private OpeningHours openingHours;              // 운영시간
    private String phoneNumber;            // 전화번호
    //    private Double rating;                          // 평점
    private String url;                             // 지도 URL
    private PlaceType placeType;                    // 내부 타입 (매핑 후 추가)
    private String photoUrl;                        // 사진 URL

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class OpeningHours {
        private List<String> weekdayText;
    }

}
