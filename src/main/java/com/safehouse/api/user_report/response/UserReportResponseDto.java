package com.safehouse.api.user_report.response;

import com.safehouse.domain.report.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserReportResponseDto {
    private Long reportId;
    private LocalDateTime reportDate;
    private String reportDescription;
    private String reportDetailAddress;
    private String area;
    private String defectType;

    public static UserReportResponseDto fromEntity(Report report) {
        return new UserReportResponseDto(
                report.getReportId(),
                report.getReportDate(),
                report.getReportDescription(),
                report.getReportDetailAddress(),
                report.getArea(),
                report.getDefectType()
        );
    }
}