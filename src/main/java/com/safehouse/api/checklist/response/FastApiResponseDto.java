package com.safehouse.api.checklist.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//@Getter
//@NoArgsConstructor
//public class FastApiResponseDto {
//    private String status;
//    private String reportId;
//    private String message;
//    private String downloadUrl;
//
//    @Builder
//    public FastApiResponseDto(String status, String reportId, String message, String downloadUrl) {
//        this.status = status;
//        this.reportId = reportId;
//        this.message = message;
//        this.downloadUrl = downloadUrl;
//    }
//}

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FastApiResponseDto {
    private String status;
    private String reportId;
    private String message;
    private String downloadUrl;

    @Builder
    public FastApiResponseDto(String status, String reportId, String message, String downloadUrl) {
        this.status = status;
        this.reportId = reportId;
        this.message = message;
        this.downloadUrl = downloadUrl;
    }
}

