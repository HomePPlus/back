package com.safehouse.api.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safehouse.common.response.ApiResponse;

import com.safehouse.api.report.request.ReportRequestDto;
import com.safehouse.api.report.response.ReportResponseDto;
import com.safehouse.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> createReport(
            @RequestPart(value = "report") String reportJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        if (reportJson == null || reportJson.trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "내용을 확인하여 주시기 바랍니다.",
                            null
                    ));
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            ReportRequestDto request = mapper.readValue(reportJson, ReportRequestDto.class);
            ReportResponseDto response = reportService.createReport(request, images);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            HttpStatus.CREATED.value(),
                            "신고가 성공적으로 등록되었습니다.",
                            response
                    ));

        } catch (JsonProcessingException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "잘못된 JSON 형식입니다.",
                            null
                    ));
        }
    }
}


