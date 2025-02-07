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

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//@Slf4j
//public class ReportService {
//    private final ReportRepository reportRepository;
//    private final UserRepository userRepository;
//    private final MessageSource messageSource;
//    private boolean shouldDeleteExistingImages;
//    private final DetectionService detectionService;
//    private final DetectionResultRepository detectionResultRepository;
//    private final ObjectMapper objectMapper;
//
//    @Value("${file.upload.path}")
//    private String fileUploadPath;
//
//    public ApiResponse<ReportResponseDto> createReport(Long userId, ReportRequestDto request, List<MultipartFile> files) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new CustomException.NotFoundException(getMessage("user.not.found")));
//
//        Report report = createReportEntity(request, user);
//        processImages(report, files); // 이미지 처리 및 모델 실행
//        Report savedReport = reportRepository.save(report);
//
//        return new ApiResponse<>(
//                HttpStatus.CREATED.value(),
//                getMessage("report.create.success"),
//                convertToResponse(savedReport)
//        );
//    }
//
//    private ReportResponseDto convertToResponse(Report report) {
//        List<ImageResponseDto> imageResponses = report.getImages().stream()
//                .map(image -> ImageResponseDto.builder()
//                        .reportImageName(image.getReportImageName())
//                        .reportImageUrl(image.getReportImageUrl())
//                        .build())
//                .collect(Collectors.toList());
//
//        // DetectionResult 조회 및 설정
//        DetectionResult detectionResult = detectionResultRepository.findTopByReportOrderByDetectionIdDesc(report);
//        String labels = detectionResult != null ? detectionResult.getReport().getDetectionLabel() : null;
//
//        return ReportResponseDto.builder()
//                .id(report.getReportId())
//                .userId(report.getUser().getUserId())
//                .reportTitle(report.getReportTitle()) // 제목 반환
//                .reportDetailAddress(report.getReportDetailAddress())
//                .defectType(report.getDefectType())
//                .reportDescription(report.getReportDescription())
//                .reportDate(report.getReportDate())
//                .images(imageResponses)
//                .detectionResult(labels)
//                .build();
//    }
//
//    private Report createReportEntity(ReportRequestDto request, User user) {
//        String area = AddressUtil.extractDistrict(request.getReportDetailAddress());
//        return Report.builder()
//                .user(user)
//                .reportTitle(request.getReportTitle()) // 제목 설정
//                .reportDetailAddress(request.getReportDetailAddress())
//                .defectType(request.getDefectType())
//                .reportDescription(request.getReportDescription())
//                .reportDate(LocalDateTime.now())
//                .images(new ArrayList<>())
//                .area(area)
//                .build();
//    }
//
//    private void processImages(Report report, List<MultipartFile> files) {
//        if (files == null || files.isEmpty()) return;
//
//        for (MultipartFile file : files) {
//            try {
//                MultipartFile savedFile = saveImage(file); // 저장된 파일 다시 읽기
//                addImageToReport(report, file.getOriginalFilename(), savedFile.getOriginalFilename(), file);
//
//                // 모델 실행 로직 추가
//                DetectionResponse detectionResult = detectionService.getDetection(savedFile).getData();
//                saveDetectionResult(report, detectionResult);
//
//            } catch (IOException e) {
//                log.error("Error saving image: {}", e.getMessage(), e);
//                throw new CustomException.FileUploadException(getMessage("file.upload.failed"));
//            } catch (Exception e) {
//                log.error("Error during model execution: {}", e.getMessage(), e);
//                throw new CustomException.ModelExecutionException(getMessage("model.execution.failed"));
//            }
//        }
//    }
//
//    private void saveDetectionResult(Report report, DetectionResponse detectionResult) {
//        DetectionResult detectionResultEntity = new DetectionResult();
//        detectionResultEntity.setReport(report);
//
//        try {
//            detectionResultEntity.setDetectionJson(objectMapper.writeValueAsString(detectionResult));
//            // label들을 추출하여 문자열로 만듦
//            List<String> labels = detectionResult.getDetections().stream()
//                    .map(DetectionResponse.Detection::getLabel)
//                    .collect(Collectors.toList());
//
//            String labelStr = String.join(", ", labels);
//            report.setDetectionLabel(labelStr);
//
//            detectionResultRepository.save(detectionResultEntity);
//        } catch (JsonProcessingException e) {
//            throw new CustomException.SerializationException(getMessage("serialization.failed"));
//        }
//
//        detectionResultRepository.save(detectionResultEntity);
//    }
//
//    private MultipartFile saveImage(MultipartFile file) throws IOException {
//        // 파일 저장 디렉토리 확인 및 생성
//        File directory = new File(fileUploadPath);
//        if (!directory.exists()) {
//            if (!directory.mkdirs()) {
//                throw new IOException("Failed to create directory: " + fileUploadPath);
//            }
//        }
//
//        // 파일 이름 생성
//        String originalFileName = file.getOriginalFilename();
//        if (originalFileName == null || originalFileName.isEmpty()) {
//            throw new IOException("Invalid file name");
//        }
//
//        // 고유한 파일 이름 생성
//        String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
//        File destination = new File(directory, storedFileName);
//
//        // 파일 저장
//        file.transferTo(destination);
//
//        // 저장된 파일을 다시 읽어 MultipartFile로 변환
//        FileInputStream fis = new FileInputStream(destination);
//        byte[] bytes = new byte[(int) destination.length()];
//        fis.read(bytes);
//        fis.close();
//
//        MultipartFile savedFile = new MultipartFile() {
//            @Override
//            public String getName() {
//                return "file";
//            }
//
//            @Override
//            public String getOriginalFilename() {
//                return storedFileName;
//            }
//
//            @Override
//            public String getContentType() {
//                return file.getContentType();
//            }
//
//            @Override
//            public boolean isEmpty() {
//                return false;
//            }
//
//            @Override
//            public long getSize() {
//                return bytes.length;
//            }
//
//            @Override
//            public byte[] getBytes() throws IOException {
//                return bytes;
//            }
//
//            @Override
//            public InputStream getInputStream() throws IOException {
//                return new ByteArrayInputStream(bytes);
//            }
//
//            @Override
//            public void transferTo(File dest) throws IOException, IllegalStateException {
//                // 이미 파일로 저장된 상태이므로, 이 메서드는 호출되지 않습니다.
//            }
//        };
//
//        return savedFile;
//    }
//
////    private String createImageUrl(String storedFileName) {
////        String imageUrl = "http://localhost:8080/images/" + storedFileName;
////        log.info("이미지 URL 생성: {}", imageUrl);
////        return imageUrl;
////    }
//
//    private String createImageUrl(String storedFileName) {
//        String connectionString = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
//        String containerName = System.getenv("AZURE_STORAGE_CONTAINER_NAME");
//
//        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
//                .connectionString(connectionString)
//                .buildClient();
//        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
//
//        // images 폴더 경로 추가
//        String blobPath = "images/" + storedFileName;
//        BlobClient blobClient = containerClient.getBlobClient(blobPath);
//
//        String imageUrl = blobClient.getBlobUrl();
//        log.info("이미지 URL 생성: {}", imageUrl);
//        return imageUrl;
//    }
//
//
////    private void addImageToReport(Report report, String originalFileName, String storedFileName) {
////        String imageUrl = createImageUrl(storedFileName); // URL 생성
////        ReportImage reportImage = ReportImage.builder()
////                .reportImageName(originalFileName)
////                .reportImageUrl(imageUrl) // 생성된 URL 사용
////                .report(report)
////                .build();
////        report.getImages().add(reportImage);
////    }
//    private void addImageToReport(Report report, String originalFileName, String storedFileName, MultipartFile file) {
//        try {
//            // Azure Storage에 이미지 업로드
//            uploadImageToAzure(storedFileName, file);
//
//            // URL 생성 및 엔티티에 추가
//            String imageUrl = createImageUrl(storedFileName);
//            ReportImage reportImage = ReportImage.builder()
//                    .reportImageName(originalFileName)
//                    .reportImageUrl(imageUrl)
//                    .report(report)
//                    .build();
//            report.getImages().add(reportImage);
//
//        } catch (IOException e) {
//            log.error("이미지 업로드 실패", e);
//            throw new RuntimeException("Failed to upload image to Azure Blob Storage", e);
//        }
//    }
//
//    private void uploadImageToAzure(String storedFileName, MultipartFile file) throws IOException {
//        String connectionString = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
//        String containerName = System.getenv("AZURE_STORAGE_CONTAINER_NAME");
//
//        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
//                .connectionString(connectionString)
//                .buildClient();
//        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
//
//        String blobPath = "images/" + storedFileName;
//        BlobClient blobClient = containerClient.getBlobClient(blobPath);
//
//        blobClient.upload(file.getInputStream(), file.getSize());
//    }
//
//
//    // 전체 조회
//    public ApiResponse<List<ReportResponseDto>> getAllReports() {
//        List<Report> reports = reportRepository.findAll();
//        List<ReportResponseDto> responseDtos = reports.stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//
//        return new ApiResponse<>(
//                HttpStatus.OK.value(),
//                getMessage("report.fetch.success"),
//                responseDtos
//        );
//    }
//
//    // 상세 조회
//    public ApiResponse<ReportResponseDto> getReportDetail(Long reportId) {
//        Report report = reportRepository.findById(reportId)
//                .orElseThrow(() -> new CustomException.NotFoundException(getMessage("report.not.found")));
//
//        return new ApiResponse<>(
//                HttpStatus.OK.value(),
//                getMessage("report.detail.success"),
//                convertToResponse(report)
//        );
//    }
//
//    // 수정
//    public ApiResponse<ReportResponseDto> updateReport(Long reportId, Long userId, ReportRequestDto request, List<MultipartFile> newImages) {
//        Report report = reportRepository.findById(reportId)
//                .orElseThrow(() -> new CustomException.NotFoundException(getMessage("report.not.found")));
//
//        if (!report.getUser().getId().equals(userId)) {
//            throw new CustomException.UnauthorizedException(getMessage("report.unauthorized"));
//        }
//
//        report.update(request);
//
//        // 새로운 이미지가 있는 경우
//        if (newImages != null && !newImages.isEmpty()) {
//            // 기존 이미지 삭제 여부를 선택적으로 처리
//            if (shouldDeleteExistingImages) {
//                deleteExistingImages(report);
//            }
//            processImages(report, newImages);
//        }
//
//        Report updatedReport = reportRepository.save(report);
//        return new ApiResponse<>(
//                HttpStatus.OK.value(),
//                getMessage("report.update.success"),
//                convertToResponse(updatedReport)
//        );
//    }
//
//    private void deleteExistingImages(Report report) {
//        for (ReportImage image : report.getImages()) {
//            File file = new File(image.getReportImageUrl());
//            if (file.exists()) {
//                file.delete();
//            }
//        }
//        report.getImages().clear();
//    }
//
//    // 삭제
//    public ApiResponse<?> deleteReport(Long reportId, Long userId) {
//        Report report = reportRepository.findById(reportId)
//                .orElseThrow(() -> new CustomException.NotFoundException(getMessage("report.not.found")));
//
//        if (!report.getUser().getId().equals(userId)) {
//            throw new CustomException.UnauthorizedException(getMessage("report.unauthorized"));
//        }
//
//        deleteExistingImages(report);
//        reportRepository.delete(report);
//
//        return new ApiResponse<>(
//                HttpStatus.OK.value(),
//                getMessage("report.delete.success"),
//                null
//        );
//    }
//
//    private String getMessage(String code) {
//        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
//    }
//}
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
                log.info("이미지 처리 시작 - 이미지 개수: {}", images.size());

                for (MultipartFile file : images) {
                    try {
                        // Azure에 이미지 저장
                        String storedFileName = uploadImageToAzure(UUID.randomUUID().toString(), file);
                        addImageToReport(report, file.getOriginalFilename(), storedFileName, file);

                        // 모델 실행
                        ApiResponse<DetectionResponse> detectionResult = detectionService.detectDefect(storedFileName);

                        // 결함 유형 저장
                        if (detectionResult != null && detectionResult.getData() != null &&
                                detectionResult.getData().getDetections() != null &&
                                !detectionResult.getData().getDetections().isEmpty()) {
                            String label = detectionResult.getData().getDetections().get(0).getLabel();
                            report.setDetectionLabel(label);
                            log.info("결함 탐지 라벨 저장: {}", label);
                        }

                        // DetectionResult 엔티티 생성 및 저장
                        ObjectMapper objectMapper = new ObjectMapper();
                        String detectionJson = objectMapper.writeValueAsString(detectionResult.getData());

                        DetectionResult detection = DetectionResult.builder()
                                .report(report)
                                .detectionJson(detectionJson)
                                .build();

                        // 양방향 연관관계 설정
                        detection.setReport(report);
                        report.getDetectionResults().add(detection);

                        log.info("탐지 결과 저장 완료: {}", detectionJson);
                    } catch (Exception e) {
                        log.error("이미지 처리 중 오류 발생", e);
                        throw new CustomException.BadRequestException("이미지 처리 중 오류가 발생했습니다: " + e.getMessage());
                    }
                }
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
        return new ApiResponse<>(200, getMessage("report.get.all.success"), responseList);
    }

    public ApiResponse<ReportResponseDto> getReportDetail(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException.NotFoundException(getMessage("report.not.found")));
        return new ApiResponse<>(200, getMessage("report.get.detail.success"),
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

}