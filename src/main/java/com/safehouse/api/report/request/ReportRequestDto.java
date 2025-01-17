package com.safehouse.api.report.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDto {
    private Long userId;
    private String reportDetailAddress;
    private String defectType;
    private String reportDescription;
}

