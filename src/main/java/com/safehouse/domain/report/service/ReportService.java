package com.safehouse.domain.report.service;

import com.safehouse.domain.report.entity.Report;
import com.safehouse.domain.report.entity.ReportImage;
import com.safehouse.domain.user.entity.User;
import com.safehouse.api.report.response.ImageResponseDto;
import com.safehouse.api.report.request.ReportRequestDto;
import com.safehouse.api.report.response.ReportResponseDto;
import com.safehouse.domain.report.repository.ReportRepository;
import com.safehouse.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final UserRepository userRepository;  // UserRepository 추가

    @Value("${file.upload.path}")
    private String fileUploadPath;

    public ReportResponseDto createReport(ReportRequestDto request, List<MultipartFile> files) {
        // 사용자 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Report report = Report.builder()
                .user(user)  // user 설정
                .reportDetailAddress(request.getReportDetailAddress())
                .defectType(request.getDefectType())
                .reportDescription(request.getReportDescription())
                .reportDate(LocalDateTime.now())
                .images(new ArrayList<>())
                .build();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String originalFileName = file.getOriginalFilename();
                String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
                String filePath = fileUploadPath + storedFileName;

                try {
                    file.transferTo(new File(filePath));

                    ReportImage reportImage = ReportImage.builder()
                            .reportImageName(originalFileName)
                            .reportImageUrl(filePath)  // DB에 저장될 경로
                            .report(report)
                            .build();

                    report.getImages().add(reportImage);
                } catch (IOException e) {
                    throw new RuntimeException("이미지 저장 실패", e);
                }
            }
        }

        Report savedReport = reportRepository.save(report);
        return convertToResponse(savedReport);
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
                .userId(report.getUser().getUserId())  // user의 ID를 반환
                .reportDetailAddress(report.getReportDetailAddress())
                .defectType(report.getDefectType())
                .reportDescription(report.getReportDescription())
                .reportDate(report.getReportDate())
                .images(imageResponses)
                .build();
    }
}