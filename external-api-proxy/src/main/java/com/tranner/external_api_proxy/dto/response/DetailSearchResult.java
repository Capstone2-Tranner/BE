package com.tranner.external_api_proxy.dto.response;

import com.tranner.external_api_proxy.type.PlaceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailSearchResult {
    private String placeId;                         // "place_id"
    private String name;                            // "name" (한국어 이름 포함)
    private List<String> types;                     // 장소 타입들
    private String formattedAddress;                // 주소
    private Geometry geometry;                      // 위치 정보
    private EditorialSummary editorialSummary;      // 장소 개요
    private OpeningHours openingHours;              // 운영시간
    private String formattedPhoneNumber;            // 전화번호
    private Double rating;                          // 평점
    private String url;                             // 지도 URL
    private List<Photo> photos;                     // 사진 리스트
    private PlaceType placeType;                    // 내부 타입 (매핑 후 추가)

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
    public static class EditorialSummary {
        private String overview;
    }

    @Data
    public static class OpeningHours {
        private List<String> weekdayText;
    }

    @Data
    public static class Photo {
        private String photoReference;  // "photo_reference"
        private int width;              // 너비 등 필요한 필드만 선택
        // 필요시 height, html_attributions 등 추가 가능
    }
}
