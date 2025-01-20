package com.safehouse.domain.inspection.repository;

import com.safehouse.domain.inspection.entity.RegularSchedule;
import com.safehouse.domain.user.entity.Inspector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegularScheduleRepository extends JpaRepository<RegularSchedule, Long> {
    List<RegularSchedule> findByInspector_InspectorId(Long inspectorId);
    boolean existsByInspectorAndScheduleDate(Inspector inspector, LocalDate scheduleDate);
}