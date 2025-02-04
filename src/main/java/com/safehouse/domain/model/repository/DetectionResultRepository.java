package com.safehouse.domain.model.repository;

import com.safehouse.domain.model.entity.DetectionResult;
import com.safehouse.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetectionResultRepository extends JpaRepository<DetectionResult, Long> {
    // 특정 Report와 연관된 가장 최근의 DetectionResult를 가져오는 쿼리 메서드
    DetectionResult findTopByReportOrderByDetectionIdDesc(Report report);
}