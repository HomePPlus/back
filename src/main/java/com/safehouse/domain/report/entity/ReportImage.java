package com.safehouse.domain.report.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reports_images")  // 테이블 이름 변경
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReportImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportImageId;//신고 이미지 id

    @Column(nullable = false)
    private String reportImageName;//신고 이미지 이름

    @Column(nullable = false)
    private String reportImageUrl;//신고 이미지 url

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;//신고 id
}

