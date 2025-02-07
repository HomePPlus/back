package com.safehouse.domain.model.entity;

import com.safehouse.domain.report.entity.Report;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detection_results")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetectionResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detectionId;

    @Setter
    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;

    @Setter
    @Column(columnDefinition = "LONGTEXT") // JSON 데이터를 저장할 수 있도록 LONGTEXT 설정
    private String detectionJson;

}