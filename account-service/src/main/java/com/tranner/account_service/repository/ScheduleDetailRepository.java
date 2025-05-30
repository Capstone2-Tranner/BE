package com.tranner.account_service.repository;

import com.tranner.account_service.domain.ScheduleDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleDetailRepository extends JpaRepository<ScheduleDetail, Long> {

    List<ScheduleDetail> findBySchedule_Id(Long scheduleId);

}
