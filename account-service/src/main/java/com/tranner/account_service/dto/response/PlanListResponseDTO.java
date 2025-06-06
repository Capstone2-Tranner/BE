package com.tranner.account_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanListResponseDTO {

    private List<PlanDTO> plans;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanDTO {
        private Long scheduleId;
        private String scheduleName;
        private LocalDate startDate;
        private LocalDate endDate;
        private int howManyPeople;
        private String countryName;
        private String regionName;
    }
}
