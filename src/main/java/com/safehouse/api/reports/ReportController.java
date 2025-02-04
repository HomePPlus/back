package com.safehouse.api.reports;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safehouse.common.exception.CustomException;
import com.safehouse.common.response.ApiResponse;

import com.safehouse.api.reports.request.ReportRequestDto;
import com.safehouse.api.reports.response.ReportResponseDto;
import com.safehouse.domain.report.service.ReportService;
import com.safehouse.domain.user.entity.User;
import com.safehouse.domain.user.repository.UserRepository;
import com.safehouse.domain.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final MessageSource  messageSource;
    private final UserRepository userRepository;
    private final UserProfileService userProfileService;
    @PostMapping
    public ApiResponse<ReportResponseDto> createReport(
            @RequestPart(value = "report") String reportJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images, @AuthenticationPrincipal UserDetails userDetails) {

        if (reportJson == null || reportJson.trim().isEmpty()) {
            throw new CustomException.BadRequestException(getMessage("report.content.empty"));
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            ReportRequestDto request = mapper.readValue(reportJson, ReportRequestDto.class);
            // 제목이 비어 있을 경우 에러 처리
            if (request.getReportTitle() == null || request.getReportTitle().trim().isEmpty()) {
                throw new CustomException.BadRequestException(getMessage("제목을 입력해주세요."));
            }
            // 이메일로 사용자 조회
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new CustomException.NotFoundException("사용자를 찾을 수 없습니다"));

            return reportService.createReport(user.getUserId(), request, images);
        } catch (JsonProcessingException e) {
            throw new CustomException.BadRequestException(getMessage("json.format.invalid"));
        }
    }

    // 전체 조회
    @GetMapping
    public ApiResponse<List<ReportResponseDto>> getAllReports() {
        return reportService.getAllReports();
    }

    // 상세 조회
    @GetMapping("/{reportId}")
    public ApiResponse<ReportResponseDto> getReportDetail(@PathVariable(name = "reportId") Long reportId) {
        return reportService.getReportDetail(reportId);
    }

    // 수정
    @PutMapping("/{reportId}")
    public ApiResponse<ReportResponseDto> updateReport(
            @PathVariable(name = "reportId") Long reportId,
            @RequestPart(value = "report") String reportJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (reportJson == null || reportJson.trim().isEmpty()) {
            throw new CustomException.BadRequestException(getMessage("report.content.empty"));
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            ReportRequestDto request = mapper.readValue(reportJson, ReportRequestDto.class);
            Long userId = userProfileService.getUserIdByEmail(userDetails.getUsername());

            return reportService.updateReport(reportId, userId, request, images);
        } catch (JsonProcessingException e) {
            throw new CustomException.BadRequestException(getMessage("json.format.invalid"));
        }
    }

    // 삭제
    @DeleteMapping("/{reportId}")
    public ApiResponse<?> deleteReport(
            @PathVariable(name = "reportId") Long reportId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = userProfileService.getUserIdByEmail(userDetails.getUsername());
        return reportService.deleteReport(reportId, userId);
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
