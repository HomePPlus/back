package com.safehouse.api.inspections.schedules;

import com.safehouse.api.inspections.schedules.dto.request.RegularScheduleRequestDto;
import com.safehouse.api.inspections.schedules.dto.request.ReportScheduleRequestDto;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.inspection.service.ScheduleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api/schedules")
@PreAuthorize("hasRole('INSPECTOR')")
@RequiredArgsConstructor  // 추가
public class ScheduleController {
    private final ScheduleService scheduleService;  // 추가

    @PostMapping("/reports")
    public ApiResponse<?> createReportSchedule(@RequestBody ReportScheduleRequestDto request,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        return scheduleService.createReportSchedule(request, userDetails.getUsername());
    }

    @GetMapping("/reports")
    public ApiResponse<?> getMyReportSchedules(@AuthenticationPrincipal UserDetails userDetails) {
        return scheduleService.getReportSchedulesByInspector(userDetails.getUsername());
    }

    @GetMapping("/reports/{scheduleId}")
    public ApiResponse<?> getReportScheduleDetail(@PathVariable Long scheduleId,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        return scheduleService.getReportScheduleDetail(scheduleId, userDetails.getUsername());
    }

    @PostMapping("/regular")
    public ApiResponse<?> createRegularSchedule(@RequestBody RegularScheduleRequestDto request,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        return scheduleService.createRegularSchedule(request, userDetails.getUsername());
    }

    @GetMapping("/regular")
    public ApiResponse<?> getMyRegularSchedules(@AuthenticationPrincipal UserDetails userDetails) {
        return scheduleService.getRegularSchedulesByInspector(userDetails.getUsername());
    }

    @GetMapping("/regular/{scheduleId}")
    public ApiResponse<?> getRegularScheduleDetail(@PathVariable Long scheduleId,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        return scheduleService.getRegularScheduleDetail(scheduleId, userDetails.getUsername());
    }
}
