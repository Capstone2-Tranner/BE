package com.tranner.account_service.domain;

import com.tranner.account_service.type.CountryCode;
import com.tranner.account_service.type.RegionCode;
import com.tranner.account_service.util.CountryCodeConverter;
import com.tranner.account_service.util.RegionCodeConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "schedule")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // 스케줄 식별자

    @Column(name = "member_id", nullable = false, length = 20)
    private String memberId; // 회원 ID

    @Column(name = "schedule_name", nullable = false)
    private String scheduleName; // 여행 이름

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate; // 여행 시작일

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate; // 여행 종료일

    @Column(name = "how_many_people", nullable = false)
    private int howManyPeople; // 여행 인원수

    @Convert(converter = CountryCodeConverter.class)
    @Column(name = "country_code", nullable = false)
    private CountryCode countryCode; // 국가 코드

    @Convert(converter = RegionCodeConverter.class)
    @Column(name = "region_code", nullable = false)
    private RegionCode regionCode; // 지역 코드
}
