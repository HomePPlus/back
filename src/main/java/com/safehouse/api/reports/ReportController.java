package com.safehouse.api.reports;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safehouse.common.exception.CustomException;
import com.safehouse.common.response.ApiResponse;

import com.safehouse.api.reports.request.ReportRequestDto;
import com.safehouse.api.reports.response.ReportResponseDto;
import com.safehouse.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final MessageSource  messageSource;

    @PostMapping
    public ApiResponse<ReportResponseDto> createReport(
            @RequestPart(value = "report") String reportJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        if (reportJson == null || reportJson.trim().isEmpty()) {
            throw new CustomException.InvalidRequestException(getMessage("report.content.empty"));
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            ReportRequestDto request = mapper.readValue(reportJson, ReportRequestDto.class);
            return reportService.createReport(request, images);
        } catch (JsonProcessingException e) {
            throw new CustomException.InvalidJsonFormatException(getMessage("json.format.invalid"));
        }
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
