package com.safehouse.domain.report.entity;

import com.safehouse.api.reports.request.ReportRequestDto;
import com.safehouse.common.service.AddressUtil;
import com.safehouse.domain.model.entity.DetectionResult;
import com.safehouse.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(nullable = false)
    private LocalDateTime reportDate;

    @Column(length = 100)
    private String reportTitle; // 제목 추가

    @Column(nullable = false, length = 200)
    private String reportDescription;

    @Column(nullable = false)
    private String reportDetailAddress;

    // 신고가 들어온 구
    @Column(nullable = false)
    private String area;

    // 모델 결과 저장
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetectionResult> detectionResults;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String defectType;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean reserved; // 예약 여부 플래그 추가

    // getter, setter 추가
    @Setter
    @Column
    private String detectionLabel; // 감지된 label 문자열 저장

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportImage> images = new ArrayList<>();

    public void update(ReportRequestDto request) {
        this.reportTitle = request.getReportTitle();
        this.reportDetailAddress = request.getReportDetailAddress();
        this.defectType = request.getDefectType();
        this.reportDescription = request.getReportDescription();
        this.area = AddressUtil.extractDistrict(request.getReportDetailAddress());
    }

}