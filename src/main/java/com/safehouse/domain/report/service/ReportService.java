package com.safehouse.domain.report.service;

import com.safehouse.api.reports.response.ImageResponseDto;
import com.safehouse.common.exception.CustomException;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.report.entity.Report;
import com.safehouse.domain.report.entity.ReportImage;
import com.safehouse.domain.user.entity.User;
import com.safehouse.api.reports.request.ReportRequestDto;
import com.safehouse.api.reports.response.ReportResponseDto;
import com.safehouse.domain.report.repository.ReportRepository;
import com.safehouse.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @Value("${file.upload.path}")
    private String fileUploadPath;

    public ApiResponse<ReportResponseDto> createReport(ReportRequestDto request, List<MultipartFile> files) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException.UserNotFoundException(getMessage("user.not.found")));

        Report report = createReportEntity(request, user);
        processImages(report, files);
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

        return ReportResponseDto.builder()
                .id(report.getReportId())
                .userId(report.getUser().getUserId())
                .reportDetailAddress(report.getReportDetailAddress())
                .defectType(report.getDefectType())
                .reportDescription(report.getReportDescription())
                .reportDate(report.getReportDate())
                .images(imageResponses)
                .build();
    }

    private Report createReportEntity(ReportRequestDto request, User user) {
        return Report.builder()
                .user(user)
                .reportDetailAddress(request.getReportDetailAddress())
                .defectType(request.getDefectType())
                .reportDescription(request.getReportDescription())
                .reportDate(LocalDateTime.now())
                .images(new ArrayList<>())
                .build();
    }

    private void processImages(Report report, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;

        for (MultipartFile file : files) {
            try {
                String storedFileName = saveImage(file);
                addImageToReport(report, file.getOriginalFilename(), storedFileName);
            } catch (IOException e) {
                throw new CustomException.FileUploadException(getMessage("file.upload.failed"));
            }
        }
    }

    private String saveImage(MultipartFile file) throws IOException {
        String storedFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filePath = fileUploadPath + storedFileName;
        file.transferTo(new File(filePath));
        return filePath;
    }

    private void addImageToReport(Report report, String originalFileName, String filePath) {
        ReportImage reportImage = ReportImage.builder()
                .reportImageName(originalFileName)
                .reportImageUrl(filePath)
                .report(report)
                .build();
        report.getImages().add(reportImage);
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
