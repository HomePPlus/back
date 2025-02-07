package com.safehouse.api.model;

import com.safehouse.api.model.response.DetectionResponse;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.model.service.DetectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 결함 탐지 요청을 처리하는 컨트롤러 클래스.
 */
@RestController
@RequestMapping("/api/model")
public class DetectionController {
    private final DetectionService detectionService;

    public DetectionController(DetectionService detectionService) {
        this.detectionService = detectionService;
    }

    @PostMapping(value = "/detect")
    public ResponseEntity<ApiResponse<DetectionResponse>> detectImage(
            @RequestParam("file") String azureUrl) {
        try {
            ApiResponse<DetectionResponse> response = detectionService.detectDefect(azureUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "File processing error: " + e.getMessage(), null));
        }
    }
}
