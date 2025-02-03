package com.safehouse.domain.user_report.service;

import com.safehouse.api.user_report.response.UserReportResponseDto;
import com.safehouse.common.exception.CustomException;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.report.repository.ReportRepository;
import com.safehouse.domain.user.entity.User;
import com.safehouse.domain.user.repository.InspectorRepository;
import com.safehouse.domain.user.repository.UserRepository;
import com.safehouse.domain.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserReportService {

    private final UserProfileService userProfileService;
    private final ReportRepository reportRepository;
    private final InspectorRepository inspectorRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ApiResponse<List<UserReportResponseDto>> getUserReports() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException.NotFoundException("User not found"));

        String area = inspectorRepository.findByUser(user)
                .orElseThrow(() -> new CustomException.NotFoundException("Inspector not found"))
                .getArea();

        List<UserReportResponseDto> reports = reportRepository.findByArea(area)
                .stream()
                .map(UserReportResponseDto::fromEntity)
                .collect(Collectors.toList());

        return ApiResponse.ok("사용자 보고서 조회 성공", reports);
    }
}