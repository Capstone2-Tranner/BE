package com.tranner.account_service.dto.request;

public record ScheduleByDayDTO(
        int locationSeq,
        String startTime,
        String endTime,
        String placeId,
        String placeName,
        String placeType,
        String address,
        double latitude,
        double longitude
) {}
