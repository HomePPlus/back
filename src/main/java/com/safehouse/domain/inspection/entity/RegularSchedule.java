package com.safehouse.domain.inspection.entity;
import com.safehouse.domain.user.entity.Inspector;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "regular_schedules")
@Getter @Setter
public class RegularSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id")
    private Inspector inspector;

    @Column(name = "regular_schedule_date")
    private LocalDate scheduleDate;  // LocalDateTime -> LocalDate

    @Column(name = "regular_schedule_next_date")
    private LocalDate nextDate;      // LocalDateTime -> LocalDate

    @Column(name = "regular_schedule_status", nullable = false, length = 20)
    private String status;
    private String description;  // 점검 내용 설명
}

