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
        private Long schedule_id;
        private String schedule_name;
        private LocalDate start_date;
        private LocalDate end_date;
        private int how_many_people;
        private String country_name;
        private String region_name;
    }
}
