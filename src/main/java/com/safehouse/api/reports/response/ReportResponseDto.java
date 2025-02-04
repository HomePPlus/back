package com.safehouse.api.reports.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDto {
    private Long id;
    private Long userId;  // reporter 대신 userId로 변경
    private String reportTitle; // 제목 추가
    private String reportDetailAddress;
    private String defectType;
    private String reportDescription;
    private LocalDateTime reportDate;
    private List<ImageResponseDto> images;
    private String detectionResult;
}

