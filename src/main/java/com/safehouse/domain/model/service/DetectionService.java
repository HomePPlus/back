package com.safehouse.domain.model.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobStorageException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safehouse.api.model.response.DetectionResponse;
import com.safehouse.common.exception.CustomException;
import com.safehouse.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Arrays;

/**
 * 결함 탐지 요청을 처리하는 서비스 클래스.
 */
@Service
@Slf4j
public class DetectionService {
    private final BlobContainerClient blobContainerClient;  // 추가
    private final RestTemplate restTemplate;
    private final MessageSource messageSource;
    private final String fastApiUrl;

    public DetectionService(BlobContainerClient blobContainerClient, RestTemplate restTemplate, MessageSource messageSource,
                            @Value("${fastapi.server.url}") String fastApiUrl) {
        this.blobContainerClient = blobContainerClient;
        this.restTemplate = restTemplate;
        this.messageSource = messageSource;
        this.fastApiUrl = fastApiUrl + "/detect";
    }

    public ApiResponse<DetectionResponse> detectDefect(String storedFileName) {
        try {
            String blobPath = "images/" + storedFileName;
            log.info("결함 탐지 시작 - Blob 경로: {}", blobPath);

            // Azure Blob에서 이미지 다운로드
            BlobClient blobClient = blobContainerClient.getBlobClient(blobPath);
            byte[] imageBytes = blobClient.downloadContent().toBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ByteArrayResource fileResource = new ByteArrayResource(imageBytes) {
                @Override
                public String getFilename() {
                    return storedFileName;
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            log.info("FastAPI 요청 URL: {}", fastApiUrl);
            log.info("요청 헤더: {}", headers);
            log.info("요청 바디 파일명: {}", fileResource.getFilename());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<DetectionResponse> responseEntity = restTemplate.exchange(
                    fastApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    DetectionResponse.class
            );

            // 여기서부터 수정된 부분
            DetectionResponse response = responseEntity.getBody();
            log.info("FastAPI 응답 데이터: {}", response);

            // null 체크 및 예외 처리 강화
            if (response == null || response.getDetections() == null || response.getDetections().isEmpty()) {
                log.info("결함 탐지 결과가 없습니다.");
                return new ApiResponse<>(HttpStatus.OK.value(), "결함이 탐지되지 않았습니다.", null);
            }

            // 응답을 JSON으로 변환하여 저장
            ObjectMapper objectMapper = new ObjectMapper();
            String responseJson = objectMapper.writeValueAsString(response);
            log.info("결함 탐지 결과 JSON: {}", responseJson);


            return new ApiResponse<>(HttpStatus.OK.value(), "결함 탐지 완료", response);

        } catch (BlobStorageException e) {
            log.error("Azure Blob Storage 접근 중 오류 발생 - 경로: images/{}", storedFileName, e);
            throw new CustomException.NotFoundException("Azure Storage에서 이미지를 가져오는데 실패했습니다.");
        } catch (Exception e) {
            log.error("결함 탐지 중 오류 발생", e);
            throw new CustomException.ModelExecutionException("결함 탐지 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public ApiResponse<DetectionResponse> detectDefectJson(String storedFileName) {
        try {
            String blobPath = "test_images/" + storedFileName;
            log.info("결함 탐지 시작 - Blob 경로: {}", blobPath);

            // Azure Blob에서 이미지 다운로드
            BlobClient blobClient = blobContainerClient.getBlobClient(blobPath);
            byte[] imageBytes = blobClient.downloadContent().toBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ByteArrayResource fileResource = new ByteArrayResource(imageBytes) {
                @Override
                public String getFilename() {
                    return storedFileName;
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);  // "file1" -> "file"로 변경

            log.info("FastAPI 요청 URL: {}", fastApiUrl);
            log.info("요청 헤더: {}", headers);
            log.info("요청 바디 파일명: {}", fileResource.getFilename());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<DetectionResponse> responseEntity = restTemplate.exchange(
                    fastApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    DetectionResponse.class
            );

            DetectionResponse response = responseEntity.getBody();
            log.info("FastAPI 응답 데이터: {}", response);

            if (response == null || response.getDetections() == null || response.getDetections().isEmpty()) {
                log.info("결함 탐지 결과가 없습니다.");
                return new ApiResponse<>(HttpStatus.OK.value(), "결함이 탐지되지 않았습니다.", null);
            }

            // 응답을 JSON으로 변환하여 저장
            ObjectMapper objectMapper = new ObjectMapper();
            String responseJson = objectMapper.writeValueAsString(response);
            log.info("결함 탐지 결과 JSON: {}", responseJson);

            return new ApiResponse<>(HttpStatus.OK.value(), "결함 탐지 완료", response);

        } catch (BlobStorageException e) {
            log.error("Azure Blob Storage 접근 중 오류 발생 - 경로: test_images/{}", storedFileName, e);
            throw new CustomException.NotFoundException("Azure Storage에서 이미지를 가져오는데 실패했습니다.");
        } catch (Exception e) {
            log.error("결함 탐지 중 오류 발생", e);
            throw new CustomException.ModelExecutionException("결함 탐지 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        // 1. 파일이 비어있는지 확인
        if (file.isEmpty()) {
            throw new CustomException.BadRequestException("파일이 비어있습니다.");
        }

        // 2. 파일 타입 확인
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            log.error("잘못된 파일 타입: {}", contentType);
            throw new CustomException.BadRequestException("이미지 파일만 업로드 가능합니다.");
        }

        // 3. 허용된 이미지 확장자 확인
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if (!Arrays.asList("jpg", "jpeg", "png", "gif").contains(extension)) {
                log.error("지원하지 않는 파일 확장자: {}", extension);
                throw new CustomException.BadRequestException("지원하지 않는 이미지 형식입니다. (지원 형식: jpg, jpeg, png, gif)");
            }
        }

        // 4. 파일 크기 확인 (예: 10MB 제한)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new CustomException.BadRequestException("파일 크기는 10MB를 초과할 수 없습니다.");
        }
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
