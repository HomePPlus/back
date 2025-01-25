package com.safehouse.api.model;

import com.safehouse.api.model.response.DetectionResponse;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.model.service.DetectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/model")
public class DetectionController {
    private final DetectionService detectionService;

    public DetectionController(DetectionService detectionService) {
        this.detectionService = detectionService;
    }

    @PostMapping(value = "/detect")
    public ResponseEntity<ApiResponse<List<DetectionResponse.Detection>>> detectImage(
            @RequestParam("file") MultipartFile file) {
        try {
            ApiResponse<List<DetectionResponse.Detection>> response = detectionService.getDetection(file);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "File processing failed: " + e.getMessage(), null));
        }
    }
}

