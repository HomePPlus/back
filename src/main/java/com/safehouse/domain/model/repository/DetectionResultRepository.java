package com.safehouse.domain.model.repository;

import com.safehouse.domain.model.entity.DetectionResult;
import com.safehouse.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetectionResultRepository extends JpaRepository<DetectionResult, Long> {
    // 특정 Report와 연관된 가장 최근의 DetectionResult를 가져오는 쿼리 메서드
    DetectionResult findTopByReportOrderByDetectionIdDesc(Report report);

    // 지역별 결함 통계를 위한 쿼리 메서드
    @Query("SELECT r.report.area as area, r.detectionJson as detectionJson " +
            "FROM DetectionResult r " +
            "WHERE (:area = '부산시' OR r.report.area = :area)")
    List<Object[]> getDetectionResultsByArea(@Param("area") String area);
}