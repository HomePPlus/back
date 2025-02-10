package com.safehouse.domain.report.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
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
import com.safehouse.domain.user.entity.Inspector;
import com.safehouse.domain.user.repository.InspectorRepository;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final ReportRepository reportRepository;
    private final DetectionService detectionService;
    private final MessageSource messageSource;
    private final BlobContainerClient blobContainerClient;
    private final UserRepository userRepository;
    private String containerName;
    private final InspectorRepository inspectorRepository;

    @Transactional
    public ApiResponse<ReportResponseDto> createReport(Long userId, ReportRequestDto request, List<MultipartFile> images) {
        try {
            // 1. 사용자 확인 및 로그
            log.info("신고 생성 시작 - userId: {}, request: {}", userId, request);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException.NotFoundException("사용자를 찾을 수 없습니다."));

            // 2. Report 엔티티 생성
            Report report = Report.builder()
                    .user(user)
                    .reportTitle(request.getReportTitle())
                    .reportDescription(request.getReportDescription())
                    .reportDetailAddress(request.getReportDetailAddress())
                    .defectType(request.getDefectType())
                    .area(AddressUtil.extractDistrict(request.getReportDetailAddress()))
                    .reportDate(LocalDateTime.now())
                    .detectionResults(new ArrayList<>())
                    .images(new ArrayList<>())
                    .build();

            // 3. 이미지 처리 및 모델 실행
            if (images != null && !images.isEmpty()) {
                StringBuilder allDetectionLabels = new StringBuilder();
                double totalScore = 0.0;

                for (MultipartFile file : images) {
                    String storedFileName = UUID.randomUUID().toString();
                    String imageUrl = uploadImageToAzure(storedFileName, file);
                    addImageToReport(report, file.getOriginalFilename(), imageUrl, file);

                    // AI 모델 실행 및 결함 라벨 저장
                    ApiResponse<DetectionResponse> detectionResult = detectionService.detectDefect(imageUrl);
                    if (detectionResult != null && detectionResult.getData() != null) {
                         // DetectionResult 엔티티 생성 및 저장
                        ObjectMapper objectMapper = new ObjectMapper();
                        String detectionJson = objectMapper.writeValueAsString(detectionResult.getData());
                        
                        DetectionResult detection = DetectionResult.builder()
                            .detectionJson(detectionJson)
                            .report(report)  // 연관관계 설정
                            .build();
                        
                        report.getDetectionResults().add(detection);  // 양방향 연관관계 설정
                        
                        List<String> labels = detectionResult.getData().getDetections().stream()
                            .map(d -> d.getLabel())
                            .collect(Collectors.toList());
                        
                        String labelString = String.join(", ", labels);
                        if (allDetectionLabels.length() > 0) {
                            allDetectionLabels.append("; ");
                        }
                        allDetectionLabels.append(labelString);
                        log.info("탐지된 결함: {}", labelString);

                        totalScore = detectionResult.getData().getTotalScore();
                    }
                }
                
                if (allDetectionLabels.length() > 0) {
                    report.setDetectionLabel(allDetectionLabels.toString());
                }
                report.setTotalScore(totalScore);
            }

            // 4. Report 저장
            Report savedReport = reportRepository.save(report);
            log.info("신고 저장 완료 - reportId: {}", savedReport.getReportId());

            // 5. 응답 생성
            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "신고가 성공적으로 생성되었습니다.",
                    ReportResponseDto.from(savedReport)
            );

        } catch (Exception e) {
            log.error("신고 생성 중 오류 발생", e);
            throw new CustomException.BadRequestException("신고 생성에 실패했습니다: " + e.getMessage());
        }
    }




    public ApiResponse<List<ReportResponseDto>> getAllReports() {
        List<Report> reports = reportRepository.findAll();
        List<ReportResponseDto> responseList = reports.stream()
                .map(ReportResponseDto::from)
                .toList();
        return new ApiResponse<>(200, getMessage("report.fetch.success"), responseList);
    }

    public ApiResponse<ReportResponseDto> getReportDetail(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException.NotFoundException(getMessage("report.not.found")));
        return new ApiResponse<>(200, getMessage("report.detail.success"),
                ReportResponseDto.from(report));
    }

    public ApiResponse<ReportResponseDto> updateReport(Long reportId, Long userId, ReportRequestDto request,
                                                       List<MultipartFile> newImages) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException.NotFoundException(getMessage("report.not.found")));

        if (!report.getUser().getId().equals(userId)) {
            throw new CustomException.UnauthorizedException(getMessage("report.unauthorized"));
        }

        report.update(request);

        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile file : newImages) {
                try {
                    String originalFilename = file.getOriginalFilename();
                    String storedFileName = generateStoredFileName(originalFilename);
                    addImageToReport(report, originalFilename, storedFileName, file);
                } catch (Exception e) {  // IOException을 Exception으로 변경
                    log.error("이미지 처리 중 오류", e);
                    throw new CustomException.BadRequestException("이미지 처리 중 오류가 발생했습니다.");
                }
            }
        }

        return new ApiResponse<>(200, getMessage("report.update.success"),
                ReportResponseDto.from(report));
    }

    public ApiResponse<?> deleteReport(Long reportId, Long userId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException.NotFoundException(getMessage("report.not.found")));

        if (!report.getUser().getId().equals(userId)) {
            throw new CustomException.UnauthorizedException(getMessage("report.unauthorized"));
        }

        // Azure Storage에서 이미지 삭제
        for (ReportImage image : report.getImages()) {
            try {
                deleteImageFromAzure(image.getReportImageUrl());
            } catch (Exception e) {
                log.error("이미지 삭제 중 오류 발생", e);
                // 이미지 삭제 실패는 로그만 남기고 계속 진행
            }
        }

        reportRepository.delete(report);
        return new ApiResponse<>(200, getMessage("report.delete.success"), null);
    }

    private void addImageToReport(Report report, String originalFileName, String storedFileName, MultipartFile file) {
        try {
            // 1. 이미지 업로드 시작 로깅
            log.info("이미지 업로드 시작 - originalFileName: {}, storedFileName: {}", originalFileName, storedFileName);

            // Azure Storage에 이미지 업로드
            String imageUrl = uploadImageToAzure(storedFileName, file);

            // 2. 이미지 URL 생성 확인
            log.info("생성된 이미지 URL: {}", imageUrl);

            // 이미지 정보를 리포트에 추가
            ReportImage reportImage = ReportImage.builder()
                    .reportImageName(originalFileName)
                    .reportImageUrl(imageUrl)
                    .report(report)
                    .build();

            // 3. 생성된 ReportImage 엔티티 확인
            log.info("생성된 ReportImage 엔티티: {}", reportImage);

            report.getImages().add(reportImage);

            log.info("이미지 업로드 및 연관관계 설정 완료");
        } catch (Exception e) {
            log.error("이미지 업로드 실패 - originalFileName: {}", originalFileName, e);
            throw new CustomException.BadRequestException("이미지 업로드에 실패했습니다.");
        }
    }


    private String uploadImageToAzure(String originalFileName, MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString();
            String blobPath = "images/" + fileName;

            // Azure Storage에 업로드
            BlobClient blobClient = blobContainerClient.getBlobClient(blobPath);
            blobClient.upload(file.getInputStream(), file.getSize());

            // 파일명만 반환
            return fileName;  // URL이 아닌 파일명만 반환

        } catch (Exception e) {
            log.error("이미지 업로드 중 오류 발생", e);
            throw new CustomException.BadRequestException("이미지 업로드에 실패했습니다.");
        }
    }


    private void deleteImageFromAzure(String imageUrl) {
        try {
            String blobName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            BlobClient blobClient = blobContainerClient.getBlobClient("images/" + blobName);
            blobClient.delete();
        } catch (Exception e) {
            log.error("Azure Storage 이미지 삭제 실패: {}", imageUrl, e);
            throw new CustomException.BadRequestException("이미지 삭제에 실패했습니다.");
        }
    }

    // 예약되지 않은 신고 목록 조회 추가
    public ApiResponse<List<ReportResponseDto>> getNonReservedReports() {
        List<Report> reports = reportRepository.findNonReservedReports();
        List<ReportResponseDto> responseDtos = reports.stream()
                .map(ReportResponseDto::from)
                .collect(Collectors.toList());

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                getMessage("reservation.list.success"),
                responseDtos
        );
    }

    // 점검자별 구역의 예약되지 않은 신고 목록 조회
    public ApiResponse<List<ReportResponseDto>> getNonReservedReportsByInspector(String inspectorEmail) {
        Inspector inspector = inspectorRepository.findByUser_Email(inspectorEmail)
                .orElseThrow(() -> new CustomException.NotFoundException(
                        getMessage("inspector.not.found")));

        List<Report> reports = reportRepository.findNonReservedReportsByArea(inspector.getArea());
        List<ReportResponseDto> responseDtos = reports.stream()
                .map(ReportResponseDto::from)
                .collect(Collectors.toList());

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                getMessage("report.fetch.nonreserved.area.success"),
                responseDtos
        );
    }


    private String generateStoredFileName(String originalFilename) {
        String ext = extractExtension(originalFilename);
        return UUID.randomUUID().toString() + "." + ext;
    }

    private String extractExtension(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return pos != -1 ? originalFilename.substring(pos + 1) : "";
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    private void processImages(Report report, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;

        for (MultipartFile file : files) {
            try {
                String storedFileName = UUID.randomUUID().toString();
                String imageUrl = uploadImageToAzure(storedFileName, file);

                // AI 모델 실행 및 결함 라벨 저장
                ApiResponse<DetectionResponse> detectionResult = detectionService.detectDefect(imageUrl);
                if (detectionResult != null && detectionResult.getData() != null) {
                    List<String> labels = detectionResult.getData().getDetections().stream()
                        .map(detection -> detection.getLabel())
                        .collect(Collectors.toList());
                    
                    String allLabels = String.join(", ", labels); // PaintDamage, Exposure, Spalling
                    report.setDetectionLabel(allLabels);
                    log.info("탐지된 결함: {}", allLabels);
                }

            } catch (Exception e) {
                log.error("이미지 처리 중 오류", e);
                throw new CustomException.BadRequestException("이미지 처리 중 오류가 발생했습니다.");
            }
        }
    }

}