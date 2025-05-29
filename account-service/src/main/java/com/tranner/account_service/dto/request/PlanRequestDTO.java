package com.tranner.account_service.dto.request;

import java.time.LocalDate;
import java.util.List;

public record PlanRequestDTO(
        Long scheduleId,
        String scheduleName,
        LocalDate startDate,
        LocalDate endDate,
        int howManyPeople,
        String countryName,
        String regionName,
        List<DetailScheduleDTO> detailSchedule
) {
    public record DetailScheduleDTO(
            int daySeq,
            List<DetailByDayDTO> scheduleByDay
    ) {}

    public record DetailByDayDTO(
            int locationSeq,
            String startTime,
            String endTime,
            String placeId,
            String placeName,
            String placeType,
            String address,
            Double latitude,
            Double longitude
    ) {}
}
