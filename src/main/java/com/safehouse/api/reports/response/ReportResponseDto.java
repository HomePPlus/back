package com.safehouse.api.reports.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safehouse.domain.report.entity.Report;
import com.safehouse.domain.report.entity.ReportImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.ArrayList;  // 이 import 추가
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReportResponseDto {
    private Long reportId;
    private Long userId;
    private String reportTitle;
    private String reportDetailAddress;
    private String defectType;
    private String reportDescription;
    private LocalDateTime reportDate;
    private List<String> images;
    private String detectionResult;

    public static ReportResponseDto from(Report report) {
        // 로그 추가
        System.out.println("Detection Results size: " + report.getDetectionResults().size());

        return ReportResponseDto.builder()
                .reportId(report.getReportId())
                .userId(report.getUser().getId())
                .reportTitle(report.getReportTitle())
                .reportDetailAddress(report.getReportDetailAddress())
                .defectType(report.getDefectType())
                .reportDescription(report.getReportDescription())
                .reportDate(report.getReportDate())
                .images(report.getImages().stream()
                        .map(ReportImage::getReportImageUrl)
                        .collect(Collectors.toList()))
                .detectionResult(report.getDetectionLabel())
                .build();
    }
}

