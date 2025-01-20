package com.safehouse.domain.inspection.entity;
import com.safehouse.domain.report.entity.Report;
import com.safehouse.domain.user.entity.Inspector;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "report_schedules")
@Getter
@Setter
public class ReportSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;  // 신고와 직접 연관관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id")
    private Inspector inspector;

    @Column(name = "report_schedule_date")
    private LocalDate scheduleDate;  // LocalDateTime -> LocalDate

    @Column(name = "report_schedule_next_date")
    private LocalDate nextDate;      // LocalDateTime -> LocalDate

    @Column(name = "report_schedule_status", nullable = false, length = 20)
    private String status; // SCHEDULED, COMPLETED, CANCELLED 등

}
