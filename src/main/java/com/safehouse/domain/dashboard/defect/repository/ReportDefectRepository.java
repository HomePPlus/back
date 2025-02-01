package com.safehouse.domain.dashboard.defect.repository;
import com.safehouse.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReportDefectRepository extends JpaRepository<Report, Long> {

    @Query("SELECT r.defectType as defectType, COUNT(r) as count " +
            "FROM Report r " +
            "WHERE (:area = '부산시' OR r.area = :area) " +
            "GROUP BY r.defectType")
    List<DefectStatProjection> countDefectsByArea(@Param("area") String area);
}