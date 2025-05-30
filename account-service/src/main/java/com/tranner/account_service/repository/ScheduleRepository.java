package com.tranner.account_service.repository;

import com.tranner.account_service.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByMemberId(String memberId);

}
