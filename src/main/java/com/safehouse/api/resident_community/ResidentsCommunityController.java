package com.safehouse.api.resident_community;

import com.safehouse.api.resident_community.request.ResidentsCommunityRequestDto;
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

//@RestController
//@RequestMapping("/api/resident_communities")
//@RequiredArgsConstructor
//public class ResidentsCommunityController {
//    private final ResidentsCommunityService communityService;
//
//    @PostMapping
//    public ResponseEntity<ResidentsCommunityResponseDto> createPost(
//            @Valid @RequestBody ResidentsCommunityRequestDto requestDto,
//            @AuthenticationPrincipal UserDetails userDetails) {
//        return ResponseEntity.ok(communityService.createPost(requestDto, userDetails.getUsername()));
//    }
//
//    @GetMapping("/{communityPostId}")
//    public ResponseEntity<ResidentsCommunityResponseDto> getPost(@PathVariable("communityPostId") Long postId) {
//        return ResponseEntity.ok(communityService.getPost(postId));
//    }
//
//    @GetMapping
//    public ResponseEntity<List<ResidentsCommunityResponseDto>> getAllPosts() {
//        return ResponseEntity.ok(communityService.getAllPosts());
//    }
//
//    @PutMapping("/{communityPostId}")
//    @PreAuthorize("@communityService.isOwner(#communityPostId, authentication.name)")
//    public ResponseEntity<ResidentsCommunityResponseDto> updatePost(
//            @PathVariable("communityPostId") Long communityPostId,
//            @RequestBody ResidentsCommunityRequestDto requestDto
//    ) {
//        return ResponseEntity.ok(communityService.updatePost(communityPostId, requestDto));
//    }
//
//    @DeleteMapping("/{communityPostId}")
//    @PreAuthorize("@residentsCommunityService.isOwner(#communityPostId, authentication.name)")
//    public ResponseEntity<?> deletePost(@PathVariable("communityPostId") Long communityPostId) {
//        communityService.deletePost(communityPostId);
//        return ResponseEntity.ok().build();
//    }
//}

@RestController
@RequestMapping("/api/resident_communities")
@RequiredArgsConstructor
public class ResidentsCommunityController {
    private final ResidentsCommunityService communityService;

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
}
