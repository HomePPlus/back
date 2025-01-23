package com.safehouse.api.resident_community;

import com.safehouse.api.resident_community.request.ResidentsCommentRequestDto;
import com.safehouse.api.resident_community.request.ResidentsCommunityRequestDto;
import com.safehouse.api.resident_community.response.ResidentsCommentResponseDto;
import com.safehouse.api.resident_community.response.ResidentsCommunityResponseDto;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.resident_community.service.ResidentsCommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resident_communities")
@RequiredArgsConstructor
public class ResidentsCommunityController {
    private final ResidentsCommunityService communityService;
    private final ResidentsCommunityService residentsCommunityService;


    @PostMapping
    public ApiResponse<ResidentsCommunityResponseDto> createPost(
            @Valid @RequestBody ResidentsCommunityRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return communityService.createPost(requestDto, userDetails.getUsername());
    }

    @GetMapping("/{communityPostId}")
    public ApiResponse<ResidentsCommunityResponseDto> getPost(
            @PathVariable("communityPostId") Long postId) {
        return communityService.getPost(postId);
    }

    @GetMapping
    public ApiResponse<List<ResidentsCommunityResponseDto>> getAllPosts() {
        return communityService.getAllPosts();
    }

    @PutMapping("/{communityPostId}")
    @PreAuthorize("@communityService.isOwner(#communityPostId, authentication.name)")
    public ApiResponse<ResidentsCommunityResponseDto> updatePost(
            @PathVariable("communityPostId") Long communityPostId,
            @RequestBody ResidentsCommunityRequestDto requestDto) {
        return communityService.updatePost(communityPostId, requestDto);
    }

    @DeleteMapping("/{communityPostId}")
    @PreAuthorize("@residentsCommunityService.isOwner(#communityPostId, authentication.name)")
    public ApiResponse<Void> deletePost(@PathVariable("communityPostId") Long communityPostId) {
        return communityService.deletePost(communityPostId);
    }

    @PostMapping("/comments/{communityId}")
    public ResponseEntity<ApiResponse> createComment(
            @PathVariable(name = "communityId") Long communityId,
            @RequestBody ResidentsCommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(residentsCommunityService.createComment(communityId, requestDto, email));
    }

    // 댓글 조회
    @GetMapping("/comments/{communityId}")
    public ResponseEntity<ApiResponse<List<ResidentsCommentResponseDto>>> getComments(
            @PathVariable(name = "communityId") Long communityId) {
        return ResponseEntity.ok(residentsCommunityService.getComments(communityId));
    }

    // 댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse> updateComment(
            @PathVariable(name = "commentId") Long commentId,
            @RequestBody ResidentsCommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(residentsCommunityService.updateComment(commentId, requestDto, email));
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(
            @PathVariable(name = "commentId") Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(residentsCommunityService.deleteComment(commentId, email));
    }
}
