package com.safehouse.api.inspector_community;

import com.safehouse.api.inspector_community.request.InspectorsCommunityRequestDto;
import com.safehouse.api.inspector_community.response.InspectorsCommunityResponseDto;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.inspector_community.service.InspectorsCommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inspector_communities")
@RequiredArgsConstructor
public class InspectorsCommunityController {
    private final InspectorsCommunityService communityService;

    @PostMapping
    public ApiResponse<InspectorsCommunityResponseDto> createPost(
            @Valid @RequestBody InspectorsCommunityRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return communityService.createPost(requestDto, userDetails.getUsername());
    }

    @GetMapping("/{inspectorPostId}")
    public ApiResponse<InspectorsCommunityResponseDto> getPost(
            @PathVariable("inspectorPostId") Long postId) {
        return communityService.getPost(postId);
    }

    @GetMapping
    public ApiResponse<List<InspectorsCommunityResponseDto>> getAllPosts() {
        return communityService.getAllPosts();
    }

    @PutMapping("/{inspectorPostId}")
    @PreAuthorize("@inspectorsCommunityService.isOwner(#inspectorPostId, authentication.name)")
    public ApiResponse<InspectorsCommunityResponseDto> updatePost(
            @PathVariable("inspectorPostId") Long inspectorPostId,
            @RequestBody InspectorsCommunityRequestDto requestDto) {
        return communityService.updatePost(inspectorPostId, requestDto);
    }

    @DeleteMapping("/{inspectorPostId}")
    @PreAuthorize("@inspectorsCommunityService.isOwner(#inspectorPostId, authentication.name)")
    public ApiResponse<Void> deletePost(@PathVariable("inspectorPostId") Long inspectorPostId) {
        return communityService.deletePost(inspectorPostId);
    }
}