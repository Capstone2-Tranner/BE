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
public class PlanDetailResponseDTO {

    private String schedule_name;
    private LocalDate start_date;
    private LocalDate end_date;
    private int how_many_people;
    private String country_name;
    private String region_name;
    private List<DayScheduleDTO> detailSchedule;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayScheduleDTO {
        private int day_seq;
        private List<LocationDTO> scheduleByDay;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDTO {
        private int location_seq;
        private LocalTime start_time;
        private LocalTime end_time;
        private String place_name;
        private String place_type;
    }
}
