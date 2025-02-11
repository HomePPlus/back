package com.safehouse.api.model;

import com.safehouse.api.model.response.DetectionResponse;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.common.service.AzureBlobService;
import com.safehouse.domain.model.service.DetectionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.safehouse.api.model.request.DetectionJsonRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 결함 탐지 요청을 처리하는 컨트롤러 클래스.
 */
@RestController
@RequestMapping("/api/model")
@Slf4j
public class DetectionController {
    private final DetectionService detectionService;
    private final AzureBlobService azureBlobService;

    public DetectionController(DetectionService detectionService, AzureBlobService azureBlobService) {
        this.detectionService = detectionService;
        this.azureBlobService = azureBlobService;
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

    // JSON 방식 (test_images/ 경로)
    @PostMapping(value = "/detect/json")
    public ResponseEntity<ApiResponse<DetectionResponse>> detectImageJson(
            @RequestBody @Valid DetectionJsonRequest request) {  // @Valid 추가
        log.info("JSON 요청 수신: {}", request);  // 로깅 추가
        return ResponseEntity.ok(detectionService.detectDefectJson(request.getFile1()));
    }

    @PostMapping("/upload/image")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @RequestParam("image") MultipartFile image) {
        try {
            String azureUrl = azureBlobService.uploadImageToTestFolder(image);
            // URL 디코딩 후 마지막 파일명만 추출
            String decodedUrl = java.net.URLDecoder.decode(azureUrl, StandardCharsets.UTF_8.name());
            String fileName = decodedUrl.substring(decodedUrl.lastIndexOf('/') + 1);
            return ResponseEntity.ok(new ApiResponse<>(200, "이미지 업로드 성공", fileName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "이미지 업로드 실패: " + e.getMessage(), null));
        }
    }
}
