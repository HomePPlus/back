package com.safehouse.api.reports.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReportRequestDto {
    private String reportDetailAddress;
    private String defectType;
    private String reportDescription;
    private boolean shouldDeleteExistingImages;

}

