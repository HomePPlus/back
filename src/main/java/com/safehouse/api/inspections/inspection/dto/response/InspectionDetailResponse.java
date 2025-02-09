package com.safehouse.api.inspections.inspection.dto.response;

import com.safehouse.domain.inspection.entity.Inspection;
import com.safehouse.domain.inspection.entity.InspectionStatus;
import com.safehouse.domain.model.entity.DetectionResult;
import lombok.Builder;
import lombok.Getter;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InspectionDetailResponse {
    private Long inspectionId;
    private String type;
    private String status;
    private LocalDate scheduleDate;
    private LocalDate endDate;
    private String inspectorName;
    private ReportInfo reportInfo;
    private String detectionLabel; // 추가

    // Report 정보 내부 클래스
    @Getter
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ReportInfo {
        private Long reportId;
        private String description;
        private String detailAddress;
        private String defectType;
    }

    // 엔티티 변환 메서드 추가
    public static InspectionDetailResponse from(Inspection inspection) {
        return InspectionDetailResponse.builder()
                .inspectionId(inspection.getInspectionId())
                .type(inspection.getType().getDescription())
                .status(inspection.getStatus().getDescription()) // 한국어 설명
                .scheduleDate(inspection.getScheduleDate())
                .endDate(inspection.getEndDate())
                .inspectorName(inspection.getInspector().getUser().getUserRealName())
                .reportInfo(inspection.getReport() != null ?
                        ReportInfo.builder()
                                .reportId(inspection.getReport().getReportId())
                                .description(inspection.getReport().getReportDescription())
                                .detailAddress(inspection.getReport().getReportDetailAddress())
                                .defectType(inspection.getReport().getDefectType())
                                .build()
                        : null)
                .build();
    }
}
