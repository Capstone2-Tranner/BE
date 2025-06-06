package com.tranner.account_service.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanModifyResponseDTO {

    private String scheduleName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int howManyPeople;
    private String countryName;
    private String regionName;
    private List<DayScheduleDTO> detailSchedule;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayScheduleDTO {
        private int daySeq;
        private List<LocationDTO> scheduleByDay;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDTO {
        private int locationSeq;
        private LocalTime startTime;
        private LocalTime endTime;
        private String placeId;     // 추가
        private String placeName;
        private String placeType;
        private String address;     // 추가
        private Double latitude;    // 추가
        private Double longitude;   // 추가
        private String memo;
    }
}
