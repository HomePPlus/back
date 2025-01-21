package com.safehouse.domain.inspector_community.service;

import com.safehouse.api.inspector_community.request.InspectorsCommunityRequestDto;
import com.safehouse.api.inspector_community.response.InspectorsCommunityResponseDto;
import com.safehouse.common.exception.CustomException;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.inspector_community.entity.InspectorsCommunity;
import com.safehouse.domain.inspector_community.repository.InspectorsCommunityRepository;
import com.safehouse.domain.user.entity.Inspector;
import com.safehouse.domain.user.entity.User;
import com.safehouse.domain.user.repository.InspectorRepository;
import com.safehouse.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class InspectorsCommunityService {
    private final InspectorsCommunityRepository communityRepository;
    private final InspectorRepository inspectorRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    public ApiResponse<InspectorsCommunityResponseDto> createPost(InspectorsCommunityRequestDto requestDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException.UserNotFoundException(getMessage("user.not.found")));

        Inspector inspector = inspectorRepository.findByUser(user)
                .orElseThrow(() -> new CustomException.UserNotFoundException(getMessage("inspector.not.found")));

        InspectorsCommunity community = InspectorsCommunity.builder()
                .inspectorCommunityTitle(requestDto.getInspectorCommunityTitle())
                .inspectorCommunityContent(requestDto.getInspectorCommunityContent())
                .inspectorViews(0L)
                .inspector(inspector)
                .build();

        InspectorsCommunity savedCommunity = communityRepository.save(community);
        return new ApiResponse<>(
                200,
                getMessage("post.create.success"),
                convertToDto(savedCommunity)
        );
    }

    @Transactional
    public ApiResponse<InspectorsCommunityResponseDto> getPost(Long postId) {
        InspectorsCommunity community = communityRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new CustomException.PostNotExist(getMessage("post.not.found")));

        community.increaseViews();
        return new ApiResponse<>(
                200,
                getMessage("post.get.success"),
                convertToDto(community)
        );
    }

    public ApiResponse<List<InspectorsCommunityResponseDto>> getAllPosts() {
        List<InspectorsCommunityResponseDto> posts = communityRepository.findAllByOrderByInspectorCommunityCreatedAtDesc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new ApiResponse<>(
                200,
                getMessage("posts.get.success"),
                posts
        );
    }

    public ApiResponse<InspectorsCommunityResponseDto> updatePost(Long inspectorPostId, InspectorsCommunityRequestDto requestDto) {
        InspectorsCommunity community = communityRepository.findById(inspectorPostId)
                .orElseThrow(() -> new CustomException.PostNotExist(getMessage("post.not.found")));

        community.update(requestDto.getInspectorCommunityTitle(), requestDto.getInspectorCommunityContent());
        return new ApiResponse<>(
                200,
                getMessage("post.update.success"),
                convertToDto(community)
        );
    }

    public ApiResponse<Void> deletePost(Long inspectorPostId) {
        InspectorsCommunity community = communityRepository.findById(inspectorPostId)
                .orElseThrow(() -> new CustomException.PostNotExist(getMessage("post.not.found")));
        communityRepository.delete(community);
        return new ApiResponse<>(
                200,
                getMessage("post.delete.success"),
                null
        );
    }

    private InspectorsCommunityResponseDto convertToDto(InspectorsCommunity community) {
        return InspectorsCommunityResponseDto.builder()
                .inspectorPostId(community.getInspectorPostId())
                .inspectorCommunityTitle(community.getInspectorCommunityTitle())
                .inspectorCommunityContent(community.getInspectorCommunityContent())
                .inspectorName(community.getInspector().getInspectorName())
                .inspectorEmail(community.getInspector().getUser().getEmail())
                .inspectorCommunityCreatedAt(community.getInspectorCommunityCreatedAt())
                .inspectorCommunityUpdatedAt(community.getInspectorCommunityUpdatedAt())
                .inspectorViews(community.getInspectorViews())
                .build();
    }

    public boolean isOwner(Long inspectorPostId, String userEmail) {
        InspectorsCommunity community = communityRepository.findById(inspectorPostId)
                .orElseThrow(() -> new CustomException.PostNotOwner(getMessage("post.not.owner")));
        return community.getInspector().getUser().getEmail().equals(userEmail);
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
