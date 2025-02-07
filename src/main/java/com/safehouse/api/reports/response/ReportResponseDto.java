package com.safehouse.api.reports.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<ImageResponseDto> images;
    private String detectionResult;
}

