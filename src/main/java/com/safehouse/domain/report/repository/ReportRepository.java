package com.safehouse.domain.report.repository;

import com.safehouse.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByArea(String area);

    // 예약되지 않은 신고 조회
    @Query("SELECT r FROM Report r WHERE r.reserved = false")
    List<Report> findNonReservedReports();

    // 예약되지 않은 신고 조회 (특정 구역)
    @Query("SELECT r FROM Report r WHERE r.reserved = false AND r.area = :area")
    List<Report> findNonReservedReportsByArea(@Param("area") String area);

    // 지역 + 예약 여부 필터링
    List<Report> findByAreaAndReserved(String area, boolean reserved);

}

