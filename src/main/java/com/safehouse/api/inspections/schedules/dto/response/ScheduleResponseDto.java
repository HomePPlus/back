package com.safehouse.api.inspections.schedules.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleResponseDto {
    private Long scheduleId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;  // LocalDateTime -> LocalDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextDate;      // LocalDateTime -> LocalDate
    private String status;
    private String inspectorName;

    // 신고 점검일 경우 필요한 정보
    private ReportInfo report;

    @Getter @Setter
    public static class ReportInfo {
        private Long reportId;
        private String description;
        private String detailAddress;
        private String defectType;
    }
}
