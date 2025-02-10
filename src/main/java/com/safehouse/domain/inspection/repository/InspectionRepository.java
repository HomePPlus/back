package com.safehouse.domain.inspection.repository;

import com.safehouse.domain.inspection.entity.Inspection;
import com.safehouse.domain.inspection.entity.InspectionStatus;
import com.safehouse.domain.inspection.entity.InspectionType;
import com.safehouse.domain.report.entity.Report;
import com.safehouse.domain.user.entity.Inspector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    // 점검 유형과 점검자로 점검 목록 조회 (점검자별 특정 유형의 점검 목록 조회에 사용)
    List<Inspection> findByTypeAndInspector(InspectionType type, Inspector inspector);

    // 특정 지역의 점검 상태별 개수 조회 (대시보드 통계에 사용: 부산시 전체 or 특정 구)
    @Query("SELECT " +
            "CASE WHEN i.type = 'REGULAR' THEN i.inspector.area ELSE FUNCTION('SUBSTRING_INDEX', i.report.reportDetailAddress, ' ', 2) END AS district, " +
            "i.status AS status, COUNT(i) AS count " +
            "FROM Inspection i " +
            "LEFT JOIN i.report r " +
            "GROUP BY district, i.status")
    List<Object[]> getRawInspectionData();



    // 점검자의 점검 상태별 개수 조회 (대시보드 통계에 사용)
    @Query("SELECT i.status, COUNT(i) FROM Inspection i WHERE i.inspector = :inspector GROUP BY i.status")
    List<Object[]> countByInspectorAndStatus(@Param("inspector") Inspector inspector);

    // 특정 신고에 대한 특정 상태의 점검 존재 여부 확인 (신고에 대한 점검 상태 확인에 사용)
    boolean existsByReportAndStatusIn(Report report, List<InspectionStatus> statuses);

    // 특정 점검자, 날짜, 신고ID에 대한 점검 존재 여부 확인 (동일 신고에 대한 중복 점검 방지에 사용)
    boolean existsByInspectorAndScheduleDateAndReport_ReportId(Inspector inspector, LocalDate scheduleDate, Long reportId);

    // 오늘 예약 현황 조회
    List<Inspection> findByInspectorAndScheduleDate(Inspector inspector, LocalDate scheduleDate);
}
