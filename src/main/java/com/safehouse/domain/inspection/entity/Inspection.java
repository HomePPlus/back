package com.safehouse.domain.inspection.entity;
import com.safehouse.domain.report.entity.Report;
import com.safehouse.domain.user.entity.Inspector;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "inspections")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inspectionId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private InspectionType type;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private InspectionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id")
    private Inspector inspector;

    @Column(name = "schedule_date")
    private LocalDate scheduleDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    private String description;

    @Builder
    public Inspection(InspectionType type, Report report, Inspector inspector,
                      LocalDate scheduleDate, String description) {
        this.type = type;
        this.report = report;
        this.inspector = inspector;
        this.scheduleDate = scheduleDate;
        this.description = description;
        this.status = InspectionStatus.SCHEDULED;
    }

    public void updateStatus(InspectionStatus newStatus) {
        this.status = newStatus;
        if (newStatus == InspectionStatus.COMPLETED) {
            this.endDate = LocalDate.now();
        }
    }
    @PrePersist
    protected void onCreate() {
        if (scheduleDate == null) {
            scheduleDate = LocalDate.now(); // LocalDate 사용
        }
    }
}