package com.safehouse.api.inspections.inspection.dto.request;

import jakarta.validation.constraints.Future;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReportInspectionRequest {
    private Long reportId;
    @Future(message = "미래 날짜만 선택 가능합니다")
    private LocalDate scheduleDate;
}