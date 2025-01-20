package com.safehouse.domain.inspection.repository;

import com.safehouse.domain.inspection.entity.ReportSchedule;
import com.safehouse.domain.user.entity.Inspector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportScheduleRepository extends JpaRepository<ReportSchedule, Long> {
    List<ReportSchedule> findByInspector_InspectorId(Long inspectorId);
    boolean existsByReportReportId(Long reportId);  // 중복예약방지
    boolean existsByInspectorAndScheduleDate(Inspector inspector, LocalDateTime scheduleDate);
}