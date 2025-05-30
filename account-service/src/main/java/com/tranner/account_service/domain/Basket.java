package com.tranner.account_service.domain;

import com.tranner.account_service.type.CountryCode;
import com.tranner.account_service.type.PlaceType;
import com.tranner.account_service.type.RegionCode;
import com.tranner.account_service.util.CountryCodeConverter;
import com.tranner.account_service.util.RegionCodeConverter;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "basket")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //장바구니 식별자

    @Column(name = "member_id", nullable = false, length = 20)
    private String memberId; //멤버 아이디

    @Column(name = "place_id", nullable = false)
    private String placeId; //장소 id

    @Column(name = "place_name", nullable = false)
    private String placeName; //장소 이름

    @Enumerated(EnumType.STRING)
    @Column(name = "place_type", nullable = false)
    private PlaceType placeType; //장소 타입

    @Column(name = "address", nullable = false)
    private String address; //주소

    @Column(name = "latitude", nullable = false)
    private Double latitude; //위도

    @Column(name = "longitude", nullable = false)
    private Double longitude; //경도

    @Convert(converter = CountryCodeConverter.class)
    @Column(name = "country_code", nullable = false)
    private CountryCode countryCode; //국가번호

    @Convert(converter = RegionCodeConverter.class)
    @Column(name = "region_code", nullable = false)
    private RegionCode regionCode; //지역번호
}
