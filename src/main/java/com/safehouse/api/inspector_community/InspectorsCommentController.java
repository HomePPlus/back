package com.safehouse.api.inspector_community;

import com.safehouse.api.inspector_community.request.InspectorsCommentRequestDto;
import com.safehouse.api.inspector_community.response.InspectorsCommentResponseDto;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.inspector_community.service.InspectorsCommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inspector_communities")
@RequiredArgsConstructor
public class InspectorsCommentController {
    private final InspectorsCommunityService inspectorsCommunityService;

    @PostMapping("/{communityId}/comments")
    @PreAuthorize("isAuthenticated()")  // 로그인 검증
    public ResponseEntity<ApiResponse<InspectorsCommentResponseDto>> createComment(
            @PathVariable(name = "communityId") Long communityId,  // 파라미터 이름 명시
            @RequestBody InspectorsCommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();  // 이메일로 변수명 변경
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inspectorsCommunityService.createComment(communityId, requestDto, email));
    }

    @GetMapping("/{communityId}/comments")
    public ResponseEntity<ApiResponse<List<InspectorsCommentResponseDto>>> getComments(
            @PathVariable Long communityId) {
        return ResponseEntity.ok(inspectorsCommunityService.getComments(communityId));
    }
}
