package com.safehouse.domain.inspection.service;

import com.safehouse.api.inspections.inspection.dto.response.InspectionDetailResponse;
import com.safehouse.common.exception.CustomException;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.common.service.AddressUtil;
import com.safehouse.domain.inspection.entity.Inspection;
import com.safehouse.domain.inspection.entity.InspectionStatus;
import com.safehouse.domain.inspection.entity.InspectionType;
import com.safehouse.domain.inspection.repository.InspectionRepository;
import com.safehouse.domain.report.entity.Report;
import com.safehouse.domain.report.repository.ReportRepository;
import com.safehouse.domain.user.entity.Inspector;
import com.safehouse.domain.user.repository.InspectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final ReportRepository reportRepository;
    private final InspectorRepository inspectorRepository;
    private final MessageSource messageSource;

    // 공통 점검 생성 메서드
    @Transactional
    public ApiResponse<InspectionDetailResponse> createInspection(
            InspectionType type,
            Long reportId,
            LocalDate scheduleDate,
            String description,
            String email
    ) {
        Inspector inspector = getInspectorByEmail(email);
        Report report = type == InspectionType.REPORT
                ? validateReport(reportId, inspector)
                : null;

        if (type == InspectionType.REPORT) {
            report = validateReport(reportId, inspector);
            report.setReserved(true); // 예약 플래그 업데이트
            reportRepository.save(report); // 변경 사항 저장
        }

        validateScheduleDate(scheduleDate);
        validateSchedule(type, inspector, scheduleDate, reportId);

        Inspection inspection = Inspection.builder()
                .type(type)
                .report(report)
                .inspector(inspector)
                .scheduleDate(scheduleDate)
                .description(description)
                .build();

        return ApiResponse.created(
                getMessage("inspection.create.success"),
                convertToDto(inspectionRepository.save(inspection))
        );
    }

    // 점검 상태 업데이트
    @Transactional
    public ApiResponse<InspectionDetailResponse> updateStatus(
            Long inspectionId,
            InspectionStatus newStatus,
            String email
    ) {
        Inspector inspector = getInspectorByEmail(email);
        Inspection inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new CustomException.NotFoundException(
                        getMessage("inspection.not.found")));

        validateOwnership(inspector, inspection);

        try {
            // 현재 상태와 변경하려는 상태 검증
            InspectionStatus.validateTransition(inspection.getStatus(), newStatus);
        } catch (IllegalStateException ex) {
            throw new CustomException.ConflictException(ex.getMessage()); // 예외 변환
        }
        inspection.updateStatus(newStatus);

        return ApiResponse.ok(
                getMessage("inspection.update.success"),
                convertToDto(inspection)
        );
    }


    @Transactional(readOnly = true)
    public ApiResponse<InspectionDetailResponse> getInspectionDetail(Long inspectionId, String email) {
        Inspector inspector = getInspectorByEmail(email);
        Inspection inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new CustomException.NotFoundException(
                        getMessage("inspection.not.found")));

        validateOwnership(inspector, inspection);
        return ApiResponse.ok(convertToDto(inspection));
    }

    // 점검 목록 조회
    public ApiResponse<List<InspectionDetailResponse>> getInspections(
            InspectionType type,
            String email
    ) {
        Inspector inspector = getInspectorByEmail(email);
        List<Inspection> inspections = inspectionRepository.findByTypeAndInspector(type, inspector);

        return inspections.isEmpty()
                ? ApiResponse.ok(getMessage("inspection.list.empty"), Collections.emptyList())
                : ApiResponse.ok(getMessage("inspection.retrieve.success"), convertToDtos(inspections));
    }

    // 특정 신고에 대한 완료된 점검 여부 확인 -> 완료된 점검에 대한 서비스 필요할 경우
    public boolean isInspectionCompleted(Report report) {
        return inspectionRepository.existsByReportAndStatusIn(report,
                Arrays.asList(InspectionStatus.COMPLETED));
    }

    // 점검자별 점검 상태 통계
    public ApiResponse<Map<String, Long>> getInspectionStatistics(String email) {
        Inspector inspector = getInspectorByEmail(email);
        List<Object[]> results = inspectionRepository.countByInspectorAndStatus(inspector);
        Map<String, Long> statistics = new HashMap<>();
        for (Object[] result : results) {
            InspectionStatus status = (InspectionStatus) result[0];
            Long count = (Long) result[1];
            statistics.put(status.getDescription(), count);
        }
        return ApiResponse.ok(statistics);
    }

    // 지역별 점검 상태 통계
    public ApiResponse<Map<String, Map<String, Long>>> getAreaInspectionStatistics(String area) {
        List<Object[]> results = inspectionRepository.getRawInspectionData();

        // 결과를 가공하여 Map<String, Map<String, Long>> 형태로 변환
        Map<String, Map<String, Long>> statistics = new HashMap<>();
        Map<String, Long> totalStats = new HashMap<>();

        for (Object[] row : results) {
            String region = normalizeRegion((String) row[0]); // 지역 정보 정규화
            String status = ((InspectionStatus) row[1]).getDescription(); // 상태 정보
            Long count = (Long) row[2]; // 개수

            // 지역별 통계 추가
            statistics.putIfAbsent(region, new HashMap<>());
            statistics.get(region).merge(status, count, Long::sum);

            // 부산시 전체 합계 계산
            if ("부산시".equals(area)) {
                totalStats.merge(status, count, Long::sum);
            }
        }

        // 부산시 전체 요청 처리
        if ("부산시".equals(area)) {
            return ApiResponse.ok(Map.of("부산시", totalStats));
        }

        // 특정 지역 요청 처리
        if (area != null && !area.isEmpty()) {
            Map<String, Map<String, Long>> filteredStatistics = new HashMap<>();
            for (Map.Entry<String, Map<String, Long>> entry : statistics.entrySet()) {
                if (entry.getKey().contains(area)) { // 특정 구를 포함하는 경우만 필터링
                    filteredStatistics.put(entry.getKey(), entry.getValue());
                }
            }
            return ApiResponse.ok(filteredStatistics);
        }

        return ApiResponse.ok(statistics);
    }

    // 지역 이름 정규화 메서드 추가
    private String normalizeRegion(String region) {
        if (region.startsWith("부산 ")) {
            return region.substring(3); // "부산 영도구" → "영도구"
        }
        return region;
    }

    // 오늘 예약 조회
    public ApiResponse<List<InspectionDetailResponse>> getInspectionsByDate(String email, LocalDate date) {
        // 점검자 조회
        Inspector inspector = inspectorRepository.findByUser_Email(email)
                .orElseThrow(() -> new CustomException.NotFoundException(
                        messageSource.getMessage("inspector.not.found", null, Locale.KOREAN)
                ));

        List<Inspection> inspections = inspectionRepository
                .findByInspectorAndScheduleDate(inspector, date);

        String messageKey = inspections.isEmpty()
                ? "inspection.today.empty"
                : "inspection.today.success";

        return ApiResponse.ok(
                messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale()),
                convertToDtos(inspections)
        );
    }



    // === 내부 메서드 ===
    private Report validateReport(Long reportId, Inspector inspector) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException.NotFoundException(
                        getMessage("report.not.found")));

        String reportArea = AddressUtil.extractDistrict(report.getReportDetailAddress());
        if (!inspector.getArea().equals(reportArea)) {
            throw new CustomException.ForbiddenException(
                    getMessage("inspection.area.mismatch"));
        }
        return report;
    }

    private void validateScheduleDate(LocalDate scheduleDate) {
        if (scheduleDate == null) {
            throw new CustomException.BadRequestException(getMessage("inspection.schedule.date.null"));
        }
        if (scheduleDate.isBefore(LocalDate.now())) {
            throw new CustomException.BadRequestException(getMessage("inspection.schedule.date.past"));
        }
    }

    private void validateSchedule(InspectionType type, Inspector inspector, LocalDate scheduleDate, Long reportId) {
        if (type == InspectionType.REGULAR) {
            if (inspectionRepository.existsByInspectorAndScheduleDateAndReport_ReportId(
                    inspector,
                    scheduleDate,
                    reportId
            )) {
                throw new CustomException.ConflictException(
                        getMessage("inspection.schedule.conflict.same.report"));
            }
        } else if (type == InspectionType.REPORT) {
            // 신고 점검의 경우, 동일한 날짜에 동일한 신고 ID로 예약된 점검 여부 확인
            if (inspectionRepository.existsByInspectorAndScheduleDateAndReport_ReportId(
                    inspector,
                    scheduleDate,
                    reportId
            )) {
                throw new CustomException.ConflictException(
                        getMessage("inspection.schedule.conflict.same.report"));
            }
        }
    }

    private void validateOwnership(Inspector inspector, Inspection inspection) {
        if (!inspection.getInspector().equals(inspector)) {
            throw new CustomException.ForbiddenException(
                    getMessage("inspection.ownership.mismatch"));
        }
    }

    private Inspector getInspectorByEmail(String email) {
        return inspectorRepository.findByUser_Email(email)
                .orElseThrow(() -> new CustomException.NotFoundException(
                        getMessage("inspector.not.found")));
    }

    private List<InspectionDetailResponse> convertToDtos(List<Inspection> inspections) {
        return inspections.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private InspectionDetailResponse convertToDto(Inspection inspection) {
        return InspectionDetailResponse.builder()
                .inspectionId(inspection.getInspectionId())
                .type(inspection.getType().getDescription()) // "REGULAR" → "정기"
                .status(inspection.getStatus().getDescription()) // Enum → 한국어 설명
                .scheduleDate(inspection.getScheduleDate())
                .endDate(inspection.getEndDate())
                .inspectorName(inspection.getInspector().getUser().getUserRealName())
                .reportInfo(inspection.getReport() != null
                        ? InspectionDetailResponse.ReportInfo.builder()
                        .reportId(inspection.getReport().getReportId())
                        .description(inspection.getReport().getReportDescription())
                        .detailAddress(inspection.getReport().getReportDetailAddress())
                        .defectType(inspection.getReport().getDefectType())
                        .build()
                        : null)
                .detectionLabel(inspection.getReport() != null ? inspection.getReport().getDetectionLabel() : null) // 추가
            .build();
    }


    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

}