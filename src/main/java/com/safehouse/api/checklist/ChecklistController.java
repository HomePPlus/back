package com.safehouse.api.checklist;

import com.safehouse.api.checklist.request.ChecklistRequestDto;
import com.safehouse.api.checklist.response.ChecklistResponseDto;
import com.safehouse.domain.checklist.service.ChecklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistService checklistService;

    @PostMapping("/submit_checklist")
    public ResponseEntity<ChecklistResponseDto> submitChecklist(
            @Valid @RequestBody ChecklistRequestDto requestDto) {
        return ResponseEntity.ok(checklistService.submitChecklist(requestDto));
    }

    @GetMapping("/download/{report_id}")
    public ResponseEntity<Resource> downloadReport(@PathVariable("report_id") String reportId) {
        return checklistService.downloadReport(reportId);
    }

    @PostMapping("/submitAndDownload_checklist")
    public ResponseEntity<Resource> submitAndDownloadChecklist(
            @Valid @RequestBody ChecklistRequestDto requestDto) {
        return checklistService.submitAndDownloadChecklist(requestDto);
    }
}
