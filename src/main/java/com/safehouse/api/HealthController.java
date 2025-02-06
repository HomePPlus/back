package com.safehouse.api;

import com.safehouse.common.service.AzureBlobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HealthController {
    
    private final AzureBlobService azureBlobService;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK: version 1.1.0");
    }

    @PostMapping("/health/upload")
    public ResponseEntity<String> testAzureUpload(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = azureBlobService.uploadImage(file);
            return ResponseEntity.ok("이미지 업로드 성공! URL: " + imageUrl);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("이미지 업로드 실패: " + e.getMessage());
        }
    }
}
