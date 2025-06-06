package com.tranner.account_service.service;


import com.tranner.account_service.domain.Schedule;
import com.tranner.account_service.domain.ScheduleDetail;
import com.tranner.account_service.dto.request.PlanRequestDTO;
import com.tranner.account_service.dto.response.PlanDetailResponseDTO;
import com.tranner.account_service.dto.response.PlanListResponseDTO;
import com.tranner.account_service.repository.ScheduleDetailRepository;
import com.tranner.account_service.repository.ScheduleRepository;
import com.tranner.account_service.type.CountryCode;
import com.tranner.account_service.type.PlaceType;
import com.tranner.account_service.type.RegionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleDetailRepository scheduleDetailRepository;

    public PlanListResponseDTO readPlanList(String memberId) {
        List<Schedule> schedules = scheduleRepository.findByMemberId(memberId);

        List<PlanListResponseDTO.PlanDTO> planDTOs = schedules.stream()
                .map(s -> new PlanListResponseDTO.PlanDTO(
                        s.getId(),
                        s.getScheduleName(),
                        s.getStartDate(),
                        s.getEndDate(),
                        s.getHowManyPeople(),
                        s.getCountryCode().name(),   // or getDisplayName() if you want 한글
                        s.getRegionCode().name()
                ))
                .toList();

        return new PlanListResponseDTO(planDTOs);
    }

    public PlanDetailResponseDTO readPlanDetail(Long scheduleId) {
        // 1. Schedule 조회
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스케줄이 존재하지 않습니다: " + scheduleId));

        // 2. ScheduleDetail 전체 조회
        List<ScheduleDetail> details = scheduleDetailRepository.findBySchedule_Id(scheduleId);

        // 3. day_seq 기준으로 그룹핑
        Map<Integer, List<ScheduleDetail>> grouped = details.stream()
                .collect(Collectors.groupingBy(ScheduleDetail::getDaySeq));

        // 4. DTO 변환
        List<PlanDetailResponseDTO.DayScheduleDTO> daySchedules = grouped.entrySet().stream()
                .map(entry -> {
                    int daySeq = entry.getKey();
                    List<PlanDetailResponseDTO.LocationDTO> locations = entry.getValue().stream()
                            .sorted(Comparator.comparingInt(ScheduleDetail::getLocationSeq))
                            .map(detail -> new PlanDetailResponseDTO.LocationDTO(
                                    detail.getLocationSeq(),
                                    detail.getStartTime(),
                                    detail.getEndTime(),
                                    detail.getPlaceName(),
                                    detail.getPlaceType().name()
                            ))
                            .toList();
                    return new PlanDetailResponseDTO.DayScheduleDTO(daySeq, locations);
                })
                .sorted(Comparator.comparingInt(PlanDetailResponseDTO.DayScheduleDTO::getDaySeq))
                .toList();

        // 5. 최종 응답 DTO 생성
        return new PlanDetailResponseDTO(
                schedule.getScheduleName(),
                schedule.getStartDate(),
                schedule.getEndDate(),
                schedule.getHowManyPeople(),
                schedule.getCountryCode().name(),
                schedule.getRegionCode().name(),
                daySchedules
        );
    }

    public void savePlan(String memberId, PlanRequestDTO planRequestDTO){

        // 1. 스케줄 엔티티 생성 및 저장
        Schedule schedule = Schedule.builder()
                .memberId(memberId) // 실제 운영에서는 토큰에서 추출
                .scheduleName(planRequestDTO.scheduleName())
                .startDate(planRequestDTO.startDate())
                .endDate(planRequestDTO.endDate())
                .howManyPeople(planRequestDTO.howManyPeople())
                .countryCode(CountryCode.fromName(planRequestDTO.countryName()))
                .regionCode(RegionCode.fromName(planRequestDTO.regionName()))
                .build();

        scheduleRepository.save(schedule); // 먼저 저장하여 ID 확보

        // 2. 디테일 스케줄 생성 후 저장
        List<ScheduleDetail> scheduleDetails = new ArrayList<>();

        for (PlanRequestDTO.DetailScheduleDTO daySchedule : planRequestDTO.detailSchedule()) {
            int daySeq = daySchedule.daySeq();

            for (PlanRequestDTO.DetailByDayDTO item : daySchedule.scheduleByDay()) {
                ScheduleDetail detail = ScheduleDetail.builder()
                        .schedule(schedule)
                        .daySeq(daySeq)
                        .locationSeq(item.locationSeq())
                        .startTime(LocalTime.parse(item.startTime()))
                        .endTime(LocalTime.parse(item.endTime()))
                        .placeId(item.placeId())
                        .placeName(item.placeName())
                        .placeType(PlaceType.fromString(item.placeType())) // 커스텀 변환 메서드 필요
                        .address(item.address())
                        .latitude(item.latitude())
                        .longitude(item.longitude())
                        .build();

                scheduleDetails.add(detail);
            }
        }

        scheduleDetailRepository.saveAll(scheduleDetails);
    }

    public void deletePlan(Long id){
        //삭제 시 detail 먼저 삭제 후 스케줄 삭제(외래키 설정)

        // 1. 해당 scheduleId에 매핑된 detail 먼저 삭제
        List<ScheduleDetail> details = scheduleDetailRepository.findBySchedule_Id(id);
        scheduleDetailRepository.deleteAll(details);

        // 2. 그 다음 schedule 자체 삭제
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 스케줄이 존재하지 않습니다: " + id));
        scheduleRepository.delete(schedule);
    }


}
