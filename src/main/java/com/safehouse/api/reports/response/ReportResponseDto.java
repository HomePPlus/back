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
        String detectionLabel = null;
        if (!report.getDetectionResults().isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(report.getDetectionResults().get(0).getDetectionJson());
                JsonNode detectionsNode = rootNode.get("detections");
                if (detectionsNode != null && detectionsNode.isArray() && detectionsNode.size() > 0) {
                    detectionLabel = detectionsNode.get(0).get("label").asText();
                }
            } catch (Exception e) {
                // JSON 파싱 실패 시 null 유지
            }
        }

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
                .detectionResult(detectionLabel)
                .build();
    }
}

