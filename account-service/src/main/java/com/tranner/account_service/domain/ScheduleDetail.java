package com.tranner.account_service.domain;

import com.tranner.account_service.type.PlaceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "schedule_detail")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ScheduleDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // detail_schedule_id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule; // 외래키로 연결된 스케줄

    @Column(name = "day_seq", nullable = false)
    private int daySeq; // 며칠째 일정

    @Column(name = "location_seq", nullable = false)
    private int locationSeq; // 하루 내 순서

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "place_id", nullable = false)
    private String placeId;

    @Column(name = "place_name", nullable = false)
    private String placeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "place_type", nullable = false)
    private PlaceType placeType;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo; // 선택적 메모
}
