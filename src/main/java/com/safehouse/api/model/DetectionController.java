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

    /**
     * 이미지 파일을 업로드 받아 결함 탐지 결과를 반환합니다.
     *
     * @param file 업로드된 이미지 파일
     * @return ResponseEntity<ApiResponse<DetectionResponse>> 형식의 응답
     */
    @PostMapping(value = "/detect")
    public ResponseEntity<ApiResponse<DetectionResponse>> detectImage(
            @RequestParam("file") MultipartFile file) {
        try {
            ApiResponse<DetectionResponse> response = detectionService.getDetection(file);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "File processing failed: " + e.getMessage(), null));
        }
    }
}
