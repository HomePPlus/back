package com.safehouse.api.user_report;

import com.safehouse.api.user_report.response.UserReportResponseDto;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.user_report.service.UserReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user_reports")
@RequiredArgsConstructor
public class UserReportController {

    private final UserReportService userReportService;

    @GetMapping
    @PreAuthorize("hasRole('INSPECTOR')")
    public ApiResponse<List<UserReportResponseDto>> getUserReports() {
        return userReportService.getUserReports();
    }
}
