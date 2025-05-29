package com.tranner.account_service.dto.request;

import java.util.List;

public record DetailScheduleDTO(
        int daySeq,
        List<ScheduleByDayDTO> scheduleByDay
) {}
