package com.safehouse.domain.model.service;

import com.safehouse.api.model.response.DetectionResponse;
import com.safehouse.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import java.io.IOException;
import java.util.List;

@Service
public class DetectionService {
    private final RestTemplate restTemplate;
    private final MessageSource messageSource;
    private final String fastApiUrl;

    public DetectionService(RestTemplate restTemplate, MessageSource messageSource,
                            @Value("${fastapi.server.url}") String fastApiUrl) {
        this.restTemplate = restTemplate;
        this.messageSource = messageSource;
        this.fastApiUrl = fastApiUrl + "/detect";
    }

    public ApiResponse<List<DetectionResponse.Detection>> getDetection(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 파일 리소스 생성
        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
            }
        };

        // 멀티파트 바디 구성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);

        // 헤더에 boundary 파라미터 추가
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<DetectionResponse> responseEntity = restTemplate.exchange(
                fastApiUrl,
                HttpMethod.POST,
                requestEntity,
                DetectionResponse.class
        );

        DetectionResponse response = responseEntity.getBody();
        List<DetectionResponse.Detection> detections = response != null ? response.getDetections() : null;

        return new ApiResponse<>(
                200,
                getMessage("detection.success"),
                detections
        );
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
