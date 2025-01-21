package com.safehouse.domain.inspection.service;

import com.safehouse.api.inspections.schedules.dto.request.RegularScheduleRequestDto;
import com.safehouse.api.inspections.schedules.dto.request.ReportScheduleRequestDto;
import com.safehouse.api.inspections.schedules.dto.response.ScheduleResponseDto;
import com.safehouse.common.exception.CustomException;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.common.service.AddressUtil;
import com.safehouse.domain.inspection.entity.RegularSchedule;
import com.safehouse.domain.inspection.entity.ReportSchedule;
import com.safehouse.domain.inspection.repository.RegularScheduleRepository;
import com.safehouse.domain.inspection.repository.ReportScheduleRepository;
import com.safehouse.domain.report.entity.Report;
import com.safehouse.domain.report.repository.ReportRepository;
import com.safehouse.domain.user.entity.Inspector;
import com.safehouse.domain.user.repository.InspectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {
    private final InspectorRepository inspectorRepository;
    private final ReportRepository reportRepository;
    private final ReportScheduleRepository reportScheduleRepository;
    private final RegularScheduleRepository regularScheduleRepository;
    private final MessageSource messageSource;

    // 신고 점검 일정 생성
    @Transactional
    public ApiResponse<ScheduleResponseDto> createReportSchedule(ReportScheduleRequestDto dto, String email) {
        Inspector inspector = getInspectorByEmail(email);

        // 점검자의 area에 해당하는 신고 목록 조회
        List<Report> reports = reportRepository.findByArea(inspector.getArea());

        if (reports.isEmpty()) {
            throw new CustomException.NotFoundException(getMessage("reports.not.found.for.area"));
        }

        // 첫 번째 신고를 선택 (비즈니스 로직에 따라 변경 가능)
        Report report = reports.get(0);

        // 이미 예약된 신고인지 확인
        if (reportScheduleRepository.existsByReportReportId(report.getReportId())) {
            throw new CustomException.DuplicateScheduleException(getMessage("schedule.already.exists"));
        }

        ReportSchedule schedule = new ReportSchedule();
        schedule.setReport(report);
        schedule.setInspector(inspector);
        schedule.setScheduleDate(dto.getScheduleDate());
        schedule.setNextDate(dto.getNextDate());
        schedule.setStatus(getMessage("schedule.status.scheduled"));

        ReportSchedule savedSchedule = reportScheduleRepository.save(schedule);
        // 신고 점검 생성 시
        return new ApiResponse<>(200, getMessage("schedule.report.create.success"), convertToDto(savedSchedule));
    }

    // 신고 점검 일정 목록 조회
    public ApiResponse<List<ScheduleResponseDto>> getReportSchedulesByInspector(String email) {
        Inspector inspector = getInspectorByEmail(email);

        List<ReportSchedule> schedules = reportScheduleRepository
                .findByInspector_InspectorId(inspector.getInspectorId())
                .stream()
                .filter(schedule -> {
                    String reportDistrict = AddressUtil.extractDistrict(schedule.getReport().getReportDetailAddress());
                    return inspector.getArea().equals(reportDistrict);
                })
                .collect(Collectors.toList());

        if (schedules.isEmpty()) {
            return new ApiResponse<>(200, getMessage("schedule.list.empty"), Collections.emptyList());
        }

        List<ScheduleResponseDto> responseDtos = schedules.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new ApiResponse<>(200, getMessage("schedule.retrieve.success"), responseDtos);
    }


    // 신고 점검 일정 상세 조회
    public ApiResponse<ScheduleResponseDto> getReportScheduleDetail(Long scheduleId, String email) {
        Inspector inspector = getInspectorByEmail(email);

        ReportSchedule schedule = reportScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException.NotFoundException(getMessage("schedule.not.found")));

        if (!schedule.getInspector().equals(inspector)) {
            throw new CustomException.UnauthorizedException(getMessage("schedule.unauthorized"));
        }

        return new ApiResponse<>(200, getMessage("schedule.retrieve.success"), convertToDto(schedule));
    }

    // 정기 점검 일정 생성
    @Transactional
    public ApiResponse<ScheduleResponseDto> createRegularSchedule(RegularScheduleRequestDto dto, String email) {
        Inspector inspector = getInspectorByEmail(email);

        // 같은 날짜에 이미 예약된 정기 점검이 있는지 확인
        if (regularScheduleRepository.existsByInspectorAndScheduleDate(inspector, dto.getScheduleDate())) {
            throw new CustomException.DuplicateScheduleException(getMessage("schedule.regular.already.exists"));
        }

        RegularSchedule schedule = new RegularSchedule();
        schedule.setInspector(inspector);
        schedule.setScheduleDate(dto.getScheduleDate());
        schedule.setNextDate(dto.getNextDate());
        schedule.setStatus(getMessage("schedule.status.scheduled"));

        RegularSchedule savedSchedule = regularScheduleRepository.save(schedule);
        // 정기 점검 생성 시
        return new ApiResponse<>(200, getMessage("schedule.regular.create.success"), convertToDto(savedSchedule));
    }

    // 정기 점검 일정 목록 조회
    public ApiResponse<List<ScheduleResponseDto>> getRegularSchedulesByInspector(String email) {
        Inspector inspector = getInspectorByEmail(email);
        List<RegularSchedule> schedules = regularScheduleRepository
                .findByInspector_InspectorId(inspector.getInspectorId());

        if (schedules.isEmpty()) {
            return new ApiResponse<>(200, getMessage("schedule.list.empty"), Collections.emptyList());
        }

        List<ScheduleResponseDto> responseDtos = schedules.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new ApiResponse<>(200, getMessage("schedule.retrieve.success"), responseDtos);
    }

    // 정기 점검 일정 상세 조회
    public ApiResponse<ScheduleResponseDto> getRegularScheduleDetail(Long scheduleId, String email) {
        Inspector inspector = getInspectorByEmail(email);

        RegularSchedule schedule = regularScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException.NotFoundException(getMessage("schedule.not.found")));

        if (!schedule.getInspector().equals(inspector)) {
            throw new CustomException.UnauthorizedException(getMessage("schedule.unauthorized"));
        }

        return new ApiResponse<>(200, getMessage("schedule.retrieve.success"), convertToDto(schedule));
    }

    // Inspector 조회 메서드
    private Inspector getInspectorByEmail(String email) {
        return inspectorRepository.findByUser_Email(email)
                .orElseThrow(() -> new CustomException.NotFoundException(getMessage("inspector.not.found")));
    }

    // 담당 구역 검증 메서드
    private void validateInspectorArea(Inspector inspector, Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException.NotFoundException(getMessage("report.not.found")));

        String reportDistrict = AddressUtil.extractDistrict(report.getReportDetailAddress());
        if (!inspector.getArea().equals(reportDistrict)) {
            throw new CustomException.UnauthorizedException(getMessage("schedule.unauthorized.area"));
        }
    }

    // DTO 변환 메서드들
    private ScheduleResponseDto convertToDto(ReportSchedule schedule) {
        ScheduleResponseDto dto = new ScheduleResponseDto();
        dto.setScheduleId(schedule.getScheduleId());
        dto.setScheduleDate(schedule.getScheduleDate());
        dto.setNextDate(schedule.getNextDate());
        dto.setStatus(schedule.getStatus());
        dto.setInspectorName(schedule.getInspector().getUser().getUserRealName());

        // Report 정보 설정
        Report report = reportRepository.findById(schedule.getReport().getReportId()).orElse(null);
        if (report != null) {
            ScheduleResponseDto.ReportInfo reportInfo = new ScheduleResponseDto.ReportInfo();
            reportInfo.setReportId(report.getReportId());
            reportInfo.setDescription(report.getReportDescription());
            reportInfo.setDetailAddress(report.getReportDetailAddress());
            reportInfo.setDefectType(report.getDefectType());
            dto.setReport(reportInfo);
        }

        return dto;
    }

    private ScheduleResponseDto convertToDto(RegularSchedule schedule) {
        ScheduleResponseDto dto = new ScheduleResponseDto();
        dto.setScheduleId(schedule.getScheduleId());
        dto.setScheduleDate(schedule.getScheduleDate());
        dto.setNextDate(schedule.getNextDate());
        dto.setStatus(schedule.getStatus());
        dto.setInspectorName(schedule.getInspector().getUser().getUserRealName());
        return dto;
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

}
