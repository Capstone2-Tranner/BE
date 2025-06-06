package com.tranner.external_api_proxy.discovery.domain;

import com.tranner.external_api_proxy.common.type.CountryCode;
import com.tranner.external_api_proxy.common.type.PlaceType;
import com.tranner.external_api_proxy.common.type.RegionCode;
import com.tranner.external_api_proxy.common.util.CountryCodeConverter;
import com.tranner.external_api_proxy.common.util.RegionCodeConverter;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "popular_place")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class PopularPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 인기 장소 식별자

    @Column(name = "place_id", nullable = false)
    private String placeId; // 장소 ID

    @Column(name = "place_name", nullable = false)
    private String placeName; // 장소 이름

    @Enumerated(EnumType.STRING)
    @Column(name = "place_type", nullable = false)
    private PlaceType placeType; // 장소 타입

    @Column(name = "photo_url", nullable = true, length = 1000)
    private String photoUrl; // 사진 URL

    @Convert(converter = CountryCodeConverter.class)
    @Column(name = "country_code", nullable = false)
    private CountryCode countryCode; // 국가 코드

    @Convert(converter = RegionCodeConverter.class)
    @Column(name = "region_code", nullable = false)
    private RegionCode regionCode; // 지역 코드
}
