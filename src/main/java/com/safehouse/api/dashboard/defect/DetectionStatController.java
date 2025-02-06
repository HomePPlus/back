package com.safehouse.api.dashboard.defect;

import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.dashboard.defect.service.DetectionStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard/detection")
@RequiredArgsConstructor
public class DetectionStatController {

    private final DetectionStatService detectionStatService;

    @GetMapping("/stats")
    public ApiResponse<?> getDetectionStatistics(
            @RequestParam(name = "area", defaultValue = "부산시") String area
    ) {
        return detectionStatService.getDetectionStats(area);
    }
}
