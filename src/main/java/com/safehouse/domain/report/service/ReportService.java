package com.safehouse.domain.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safehouse.api.model.response.DetectionResponse;
import com.safehouse.api.reports.response.ImageResponseDto;
import com.safehouse.common.exception.CustomException;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.model.entity.DetectionResult;
import com.safehouse.domain.model.repository.DetectionResultRepository;
import com.safehouse.domain.model.service.DetectionService;
import com.safehouse.domain.report.entity.Report;
import com.safehouse.domain.report.entity.ReportImage;
import com.safehouse.domain.user.entity.User;
import com.safehouse.api.reports.request.ReportRequestDto;
import com.safehouse.api.reports.response.ReportResponseDto;
import com.safehouse.domain.report.repository.ReportRepository;
import com.safehouse.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.safehouse.common.service.AddressUtil;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private boolean shouldDeleteExistingImages;
    private final DetectionService detectionService;
    private final DetectionResultRepository detectionResultRepository;
    private final ObjectMapper objectMapper;

    @Value("${file.upload.path}")
    private String fileUploadPath;

    public ApiResponse<ReportResponseDto> createReport(Long userId, ReportRequestDto request, List<MultipartFile> files) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException.NotFoundException(getMessage("user.not.found")));

        Report report = createReportEntity(request, user);
        processImages(report, files); // 이미지 처리 및 모델 실행
        Report savedReport = reportRepository.save(report);

        return new ApiResponse<>(
                HttpStatus.CREATED.value(),
                getMessage("report.create.success"),
                convertToResponse(savedReport)
        );
    }

    private ReportResponseDto convertToResponse(Report report) {
        List<ImageResponseDto> imageResponses = report.getImages().stream()
                .map(image -> ImageResponseDto.builder()
                        .reportImageName(image.getReportImageName())
                        .reportImageUrl(image.getReportImageUrl())
                        .build())
                .collect(Collectors.toList());

        // DetectionResult 조회 및 설정
        DetectionResult detectionResult = detectionResultRepository.findTopByReportOrderByDetectionIdDesc(report);
        String labels = detectionResult != null ? detectionResult.getReport().getDetectionLabel() : null;

        return ReportResponseDto.builder()
                .id(report.getReportId())
                .userId(report.getUser().getUserId())
                .reportTitle(report.getReportTitle()) // 제목 반환
                .reportDetailAddress(report.getReportDetailAddress())
                .defectType(report.getDefectType())
                .reportDescription(report.getReportDescription())
                .reportDate(report.getReportDate())
                .images(imageResponses)
                .detectionResult(labels)
                .build();
    }

    private Report createReportEntity(ReportRequestDto request, User user) {
        String area = AddressUtil.extractDistrict(request.getReportDetailAddress());
        return Report.builder()
                .user(user)
                .reportTitle(request.getReportTitle()) // 제목 설정
                .reportDetailAddress(request.getReportDetailAddress())
                .defectType(request.getDefectType())
                .reportDescription(request.getReportDescription())
                .reportDate(LocalDateTime.now())
                .images(new ArrayList<>())
                .area(area)
                .build();
    }

    private void processImages(Report report, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;

        for (MultipartFile file : files) {
            try {
                MultipartFile savedFile = saveImage(file); // 저장된 파일 다시 읽기
                addImageToReport(report, file.getOriginalFilename(), savedFile.getOriginalFilename());

                // 모델 실행 로직 추가
                DetectionResponse detectionResult = detectionService.getDetection(savedFile).getData();
                saveDetectionResult(report, detectionResult);

            } catch (IOException e) {
                log.error("Error saving image: {}", e.getMessage(), e);
                throw new CustomException.FileUploadException(getMessage("file.upload.failed"));
            } catch (Exception e) {
                log.error("Error during model execution: {}", e.getMessage(), e);
                throw new CustomException.ModelExecutionException(getMessage("model.execution.failed"));
            }
        }
    }

    private void saveDetectionResult(Report report, DetectionResponse detectionResult) {
        DetectionResult detectionResultEntity = new DetectionResult();
        detectionResultEntity.setReport(report);

        try {
            detectionResultEntity.setDetectionJson(objectMapper.writeValueAsString(detectionResult));
            // label들을 추출하여 문자열로 만듦
            List<String> labels = detectionResult.getDetections().stream()
                    .map(DetectionResponse.Detection::getLabel)
                    .collect(Collectors.toList());

            String labelStr = String.join(", ", labels);
            report.setDetectionLabel(labelStr);

            detectionResultRepository.save(detectionResultEntity);
        } catch (JsonProcessingException e) {
            throw new CustomException.SerializationException(getMessage("serialization.failed"));
        }

        detectionResultRepository.save(detectionResultEntity);
    }

    private MultipartFile saveImage(MultipartFile file) throws IOException {
        // 파일 저장 디렉토리 확인 및 생성
        File directory = new File(fileUploadPath);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Failed to create directory: " + fileUploadPath);
            }
        }

        // 파일 이름 생성
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IOException("Invalid file name");
        }

        // 고유한 파일 이름 생성
        String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        File destination = new File(directory, storedFileName);

        // 파일 저장
        file.transferTo(destination);

        // 저장된 파일을 다시 읽어 MultipartFile로 변환
        FileInputStream fis = new FileInputStream(destination);
        byte[] bytes = new byte[(int) destination.length()];
        fis.read(bytes);
        fis.close();

        MultipartFile savedFile = new MultipartFile() {
            @Override
            public String getName() {
                return "file";
            }

            @Override
            public String getOriginalFilename() {
                return storedFileName;
            }

            @Override
            public String getContentType() {
                return file.getContentType();
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return bytes.length;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return bytes;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(bytes);
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                // 이미 파일로 저장된 상태이므로, 이 메서드는 호출되지 않습니다.
            }
        };

        return savedFile;
    }

    private String createImageUrl(String storedFileName) {
        String imageUrl = "http://localhost:8080/images/" + storedFileName;
        log.info("이미지 URL 생성: {}", imageUrl);
        return imageUrl;
    }

    private void addImageToReport(Report report, String originalFileName, String storedFileName) {
        String imageUrl = createImageUrl(storedFileName); // URL 생성
        ReportImage reportImage = ReportImage.builder()
                .reportImageName(originalFileName)
                .reportImageUrl(imageUrl) // 생성된 URL 사용
                .report(report)
                .build();
        report.getImages().add(reportImage);
    }

    // 전체 조회
    public ApiResponse<List<ReportResponseDto>> getAllReports() {
        List<Report> reports = reportRepository.findAll();
        List<ReportResponseDto> responseDtos = reports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                getMessage("report.fetch.success"),
                responseDtos
        );
    }

    // 상세 조회
    public ApiResponse<ReportResponseDto> getReportDetail(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException.NotFoundException(getMessage("report.not.found")));

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                getMessage("report.detail.success"),
                convertToResponse(report)
        );
    }

    // 수정
    public ApiResponse<ReportResponseDto> updateReport(Long reportId, Long userId, ReportRequestDto request, List<MultipartFile> newImages) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException.NotFoundException(getMessage("report.not.found")));

        if (!report.getUser().getId().equals(userId)) {
            throw new CustomException.UnauthorizedException(getMessage("report.unauthorized"));
        }

        report.update(request);

        // 새로운 이미지가 있는 경우
        if (newImages != null && !newImages.isEmpty()) {
            // 기존 이미지 삭제 여부를 선택적으로 처리
            if (shouldDeleteExistingImages) {
                deleteExistingImages(report);
            }
            processImages(report, newImages);
        }

        Report updatedReport = reportRepository.save(report);
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                getMessage("report.update.success"),
                convertToResponse(updatedReport)
        );
    }

    private void deleteExistingImages(Report report) {
        for (ReportImage image : report.getImages()) {
            File file = new File(image.getReportImageUrl());
            if (file.exists()) {
                file.delete();
            }
        }
        report.getImages().clear();
    }

    // 삭제
    public ApiResponse<?> deleteReport(Long reportId, Long userId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException.NotFoundException(getMessage("report.not.found")));

        if (!report.getUser().getId().equals(userId)) {
            throw new CustomException.UnauthorizedException(getMessage("report.unauthorized"));
        }

        deleteExistingImages(report);
        reportRepository.delete(report);

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                getMessage("report.delete.success"),
                null
        );
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
