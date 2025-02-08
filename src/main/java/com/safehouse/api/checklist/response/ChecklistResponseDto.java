package com.safehouse.api.checklist.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.safehouse.common.enums.ChecklistStatus;
import com.safehouse.domain.checklist.entity.Checklist;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChecklistResponseDto {
    private String inspectionId;
    private String reportUrl;
    private ChecklistStatus status;
    private String message;

    @Builder
    private ChecklistResponseDto(String inspectionId, String reportUrl,
                                 ChecklistStatus status, String message) {
        this.inspectionId = inspectionId;
        this.reportUrl = reportUrl;
        this.status = status;
        this.message = message;
    }

    public static ChecklistResponseDto of(Checklist checklist) {
        return ChecklistResponseDto.builder()
                .inspectionId(checklist.getInspectionId())
                .reportUrl(checklist.getReportUrl())
                .status(checklist.getStatus())
                .message("체크리스트가 성공적으로 제출되었습니다.")
                .build();
    }
}

