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

/**
 * 결함 탐지 요청을 처리하는 서비스 클래스.
 */
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

    /**
     * 이미지 파일을 FastAPI 서버로 전송하고, 결함 탐지 결과를 반환합니다.
     *
     * @param file 업로드된 이미지 파일
     * @return ApiResponse<DetectionResponse> 형식의 응답
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    public ApiResponse<DetectionResponse> getDetection(MultipartFile file) throws IOException {
        // 멀티파트 요청 헤더 설정
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

        // FastAPI 서버에 요청 전송
        ResponseEntity<DetectionResponse> responseEntity = restTemplate.exchange(
                fastApiUrl,
                HttpMethod.POST,
                requestEntity,
                DetectionResponse.class
        );

        DetectionResponse response = responseEntity.getBody();

        // 성공 응답 생성
        return new ApiResponse<>(
                200,
                getMessage("detection.success"),
                response
        );
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
