package com.safehouse.api.resident_community;

import com.safehouse.api.resident_community.request.ResidentsCommentRequestDto;
import com.safehouse.api.resident_community.request.ResidentsCommunityRequestDto;
import com.safehouse.api.resident_community.response.ResidentsCommentResponseDto;
import com.safehouse.api.resident_community.response.ResidentsCommunityResponseDto;
import com.safehouse.common.exception.CustomException;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.resident_community.service.ResidentsCommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resident_communities")
@RequiredArgsConstructor
public class ResidentsCommunityController {
    private final ResidentsCommunityService communityService;
    private final ResidentsCommunityService residentsCommunityService;


//    @PostMapping
//    public ApiResponse<ResidentsCommunityResponseDto> createPost(
//            @Valid @RequestBody ResidentsCommunityRequestDto requestDto,
//            @AuthenticationPrincipal UserDetails userDetails) {
//        return communityService.createPost(requestDto, userDetails.getUsername());
//    }

//    @PostMapping
//    public ApiResponse<ResidentsCommunityResponseDto> createPost(
//            @Valid @RequestBody ResidentsCommunityRequestDto requestDto) {
//
//        // SecurityContext에서 인증 정보 가져오기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new CustomException.UnauthorizedException("인증되지 않은 사용자입니다.");
//        }
//
//        // 이메일 추출 (JWT의 "sub" 클레임)
//        String email = authentication.getName();
//        System.out.println("JWT에서 추출한 이메일: " + email);
//        return communityService.createPost(requestDto, email);
//    }
    @PostMapping
    @PreAuthorize("hasAuthority('RESIDENT')")  // 권한 체크 추가
    public ApiResponse<ResidentsCommunityResponseDto> createPost(
            @Valid @RequestBody ResidentsCommunityRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {  // UserDetails로 직접 받기

        if (userDetails == null) {
            throw new CustomException.UnauthorizedException("인증되지 않은 사용자입니다.");
        }

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
