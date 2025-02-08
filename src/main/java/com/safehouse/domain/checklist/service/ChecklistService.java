package com.safehouse.domain.checklist.service;

import com.safehouse.api.checklist.request.ChecklistRequestDto;
import com.safehouse.api.checklist.response.ChecklistResponseDto;
import com.safehouse.api.checklist.response.FastApiResponseDto;
import com.safehouse.common.client.FastApiClient;
import com.safehouse.domain.checklist.entity.Checklist;
import com.safehouse.domain.checklist.repository.ChecklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final FastApiClient fastApiClient;

    @Value("${fastapi_checklist.server.url}")
    private String fastApiUrl;

    @Transactional
    public ChecklistResponseDto submitChecklist(ChecklistRequestDto requestDto) {
        // 1. Entity 저장
        Checklist checklist = checklistRepository.save(requestDto.toEntity());

        // 2. FastAPI 호출
        FastApiResponseDto fastApiResponse = fastApiClient.submitChecklist(requestDto);

        // 3. 응답 데이터로 체크리스트 업데이트
        checklist.updateReportUrl(fastApiResponse.getDownloadUrl());

        return ChecklistResponseDto.of(checklist);
    }

    public ResponseEntity<Resource> downloadReport(String reportId) {
        try {
            byte[] fileContent = fastApiClient.downloadPdfFromUrl(reportId);
            ByteArrayResource resource = new ByteArrayResource(fileContent);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=inspection_report_" + reportId + ".pdf")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @Transactional
    public ResponseEntity<Resource> submitAndDownloadChecklist(ChecklistRequestDto requestDto) {
        // 1. 체크리스트 데이터 저장
        ChecklistResponseDto savedChecklist = submitChecklist(requestDto);
        
        // 2. PDF 다운로드 (기존 downloadReport 메서드 활용)
        return downloadReport(savedChecklist.getInspectionId());
    }
}

