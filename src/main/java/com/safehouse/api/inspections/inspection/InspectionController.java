package com.safehouse.api.inspections.inspection;

import com.safehouse.api.inspections.inspection.dto.request.InspectionStatusUpdateRequest;
import com.safehouse.api.inspections.inspection.dto.request.RegularInspectionRequest;
import com.safehouse.api.inspections.inspection.dto.request.ReportInspectionRequest;
import com.safehouse.api.inspections.inspection.dto.response.InspectionDetailResponse;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.inspection.entity.InspectionStatus;
import com.safehouse.domain.inspection.entity.InspectionType;
import com.safehouse.domain.inspection.service.InspectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inspections")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;

    // 정기 점검 예약
    @PostMapping("/regular")
    public ApiResponse<InspectionDetailResponse> createRegularInspection(
            @Valid @RequestBody RegularInspectionRequest request,
            @AuthenticationPrincipal UserDetails user
    ) {
        return inspectionService.createInspection(
                InspectionType.REGULAR,
                null,
                request.getScheduleDate(),
                request.getDescription(),
                user.getUsername()
        );
    }

    // 신고 점검 예약
    @PostMapping("/report")
    public ApiResponse<InspectionDetailResponse> createReportInspection(
            @Valid @RequestBody ReportInspectionRequest request,
            @AuthenticationPrincipal UserDetails user
    ) {
        return inspectionService.createInspection(
                InspectionType.REPORT,
                request.getReportId(),
                request.getScheduleDate(),
                null,
                user.getUsername()
        );
    }

    // 점검 목록 조회 (타입 필터링)
    @GetMapping
    public ApiResponse<List<InspectionDetailResponse>> getInspections(
            @RequestParam InspectionType type,
            @AuthenticationPrincipal UserDetails user
    ) {
        return inspectionService.getInspections(type, user.getUsername());
    }

    // 점검 상세 조회
    @GetMapping("/{inspectionId}")
    public ApiResponse<InspectionDetailResponse> getInspectionDetailById(
            @PathVariable Long inspectionId,
            @AuthenticationPrincipal UserDetails user
    ) {
        return inspectionService.getInspectionDetail(inspectionId, user.getUsername());
    }

    // 정기 점검 목록 조회
    @GetMapping("/regular")
    public ApiResponse<List<InspectionDetailResponse>> getRegularInspections(
            @AuthenticationPrincipal UserDetails user
    ) {
        return inspectionService.getInspections(
                InspectionType.REGULAR,
                user.getUsername()
        );
    }

    // 신고 점검 목록 조회
    @GetMapping("/report")
    public ApiResponse<List<InspectionDetailResponse>> getReportInspections(
            @AuthenticationPrincipal UserDetails user
    ) {
        return inspectionService.getInspections(
                InspectionType.REPORT,
                user.getUsername()
        );
    }

    // 점검 상태 변경 (변경: request.getStatus() → request.getInspectionStatus())
    @PatchMapping("/{inspectionId}/status")
    public ApiResponse<InspectionDetailResponse> updateInspectionStatus(
            @PathVariable Long inspectionId,
            @Valid @RequestBody InspectionStatusUpdateRequest request,
            @AuthenticationPrincipal UserDetails user
    ) {
        // InspectionStatusUpdateRequest에서 Enum으로 변환된 상태를 사용
        return inspectionService.updateStatus(inspectionId, request.getInspectionStatus(), user.getUsername());
    }

    // 점검자별 점검 상태 통계 (변경: Map<InspectionStatus, Long> → Map<String, Long>)
    @GetMapping("/statistics/inspector")
    public ApiResponse<Map<String, Long>> getInspectorStatistics(
            @AuthenticationPrincipal UserDetails user
    ) {
        return inspectionService.getInspectionStatistics(user.getUsername());
    }

    // 지역별 점검 상태 통계 (변경: Map<String, Map<InspectionStatus, Long>> → Map<String, Map<String, Long>>)
    @GetMapping("/statistics/area")
    public ApiResponse<Map<String, Map<String, Long>>> getAreaStatistics(
            @RequestParam String area
    ) {
        return inspectionService.getAreaInspectionStatistics(area);
    }

    // 오늘 예약 현황 조회
    @GetMapping("/today")
    public ApiResponse<List<InspectionDetailResponse>> getInspectionsByDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserDetails user
    ) {
        if (date == null) {
            date = LocalDate.now();
        }
        return inspectionService.getInspectionsByDate(user.getUsername(), date);
    }
}