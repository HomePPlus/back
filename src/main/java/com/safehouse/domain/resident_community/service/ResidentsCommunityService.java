package com.safehouse.domain.resident_community.service;

import com.safehouse.api.resident_community.request.ResidentsCommentRequestDto;
import com.safehouse.api.resident_community.request.ResidentsCommunityRequestDto;
import com.safehouse.api.resident_community.response.ResidentsCommentResponseDto;
import com.safehouse.api.resident_community.response.ResidentsCommunityResponseDto;
import com.safehouse.common.exception.CustomException;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.resident_community.entity.ResidentsComment;
import com.safehouse.domain.resident_community.entity.ResidentsCommunity;
import com.safehouse.domain.resident_community.repository.ResidentsCommentRepository;
import com.safehouse.domain.resident_community.repository.ResidentsCommunityRepository;
import com.safehouse.domain.user.entity.User;
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
public class ResidentsCommunityService {
    private final ResidentsCommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final ResidentsCommentRepository residentsCommentRepository;

    public ApiResponse<ResidentsCommunityResponseDto> createPost(ResidentsCommunityRequestDto requestDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException.UserNotFoundException(getMessage("user.not.found")));

        ResidentsCommunity community = ResidentsCommunity.builder()
                .communityTitle(requestDto.getCommunityTitle())
                .communityContent(requestDto.getCommunityContent())
                .communityViews(0L)
                .user(user)
                .build();

        ResidentsCommunity savedCommunity = communityRepository.save(community);
        return new ApiResponse<>(
                200,
                getMessage("post.create.success"),
                convertToDto(savedCommunity)
        );
    }

    @Transactional
    public ApiResponse<ResidentsCommunityResponseDto> getPost(Long postId) {
        ResidentsCommunity community = communityRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new CustomException.PostNotExist(getMessage("post.not.found")));

        List<ResidentsCommentResponseDto> comments = residentsCommentRepository
                .findByResidentCommunity(community)
                .stream()
                .map(ResidentsCommentResponseDto::from)
                .collect(Collectors.toList());

        community.increaseViews();

        ResidentsCommunityResponseDto responseDto = convertToDto(community);
        responseDto.setComments(comments);  // 댓글 목록 설정

        return new ApiResponse<>(
                200,
                getMessage("post.get.success"),
                responseDto
        );
    }

    public ApiResponse<List<ResidentsCommunityResponseDto>> getAllPosts() {
        List<ResidentsCommunityResponseDto> posts = communityRepository.findAllByOrderByCommunityCreatedAtDesc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new ApiResponse<>(
                200,
                getMessage("posts.get.success"),
                posts
        );
    }

    public ApiResponse<ResidentsCommunityResponseDto> updatePost(Long communityPostId, ResidentsCommunityRequestDto requestDto) {
        ResidentsCommunity community = communityRepository.findById(communityPostId)
                .orElseThrow(() -> new CustomException.PostNotExist(getMessage("post.not.found")));

        community.update(requestDto.getCommunityTitle(), requestDto.getCommunityContent());
        return new ApiResponse<>(
                200,
                getMessage("post.update.success"),
                convertToDto(community)
        );
    }

    public ApiResponse<Void> deletePost(Long communityPostId) {
        ResidentsCommunity community = communityRepository.findById(communityPostId)
                .orElseThrow(() -> new CustomException.PostNotExist(getMessage("post.not.found")));
        communityRepository.delete(community);
        return new ApiResponse<>(
                200,
                getMessage("post.delete.success"),
                null
        );
    }

    private ResidentsCommunityResponseDto convertToDto(ResidentsCommunity community) {
        return ResidentsCommunityResponseDto.builder()
                .communityPostId(community.getCommunityPostId())
                .communityTitle(community.getCommunityTitle())
                .communityContent(community.getCommunityContent())
                .userName(community.getUser().getUserRealName())
                .userEmail(community.getUser().getEmail())
                .communityCreatedAt(community.getCommunityCreatedAt())
                .communityUpdatedAt(community.getCommunityUpdatedAt())
                .communityViews(community.getCommunityViews())
                .build();
    }

    public boolean isOwner(Long communityPostId, String userEmail) {
        ResidentsCommunity community = communityRepository.findById(communityPostId)
                .orElseThrow(() -> new CustomException.PostNotOwner(getMessage("post.not.owner")));
        return community.getUser().getEmail().equals(userEmail);
    }

    @Transactional
    public ApiResponse createComment(Long communityId, ResidentsCommentRequestDto requestDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException.UserNotFoundException(getMessage("user.not.found")));

        ResidentsCommunity community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException.PostNotExist(getMessage("post.not.found")));

        ResidentsComment comment = ResidentsComment.builder()
                .residentsContent(requestDto.getResidentsContent())
                .residentsCommunity(community)
                .user(user)
                .build();

        community.addComment(comment);
        ResidentsComment savedComment = residentsCommentRepository.save(comment);

        return new ApiResponse<>(
                200,
                getMessage("comment.create.success"),
                ResidentsCommentResponseDto.from(savedComment)
        );
    }

    public ApiResponse<List<ResidentsCommentResponseDto>> getComments(Long communityId) {
        ResidentsCommunity community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException.PostNotExist(getMessage("post.not.found")));

        List<ResidentsCommentResponseDto> comments = residentsCommentRepository.findByResidentCommunity(community)
                .stream()
                .map(ResidentsCommentResponseDto::from)
                .collect(Collectors.toList());

        return new ApiResponse<>(
                200,
                getMessage("comment.get.success"),
                comments
        );
    }

    @Transactional
    public ApiResponse deleteComment(Long commentId, String email) {
        ResidentsComment comment = residentsCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException.ResourceNotFoundException(getMessage("comment.not.found")));

        if (!comment.getUser().getEmail().equals(email)) {
            throw new CustomException.UnauthorizedException(getMessage("comment.not.owner"));
        }

        comment.getResidentCommunity().removeComment(comment);
        residentsCommentRepository.delete(comment);

        return new ApiResponse<>(
                200,
                getMessage("comment.delete.success"),
                null
        );
    }

    @Transactional
    public ApiResponse updateComment(Long commentId, ResidentsCommentRequestDto requestDto, String email) {
        ResidentsComment comment = residentsCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException.ResourceNotFoundException(getMessage("comment.not.found")));

        if (!comment.getUser().getEmail().equals(email)) {
            throw new CustomException.UnauthorizedException(getMessage("comment.not.owner"));
        }

        comment.updateContent(requestDto.getResidentsContent());

        return new ApiResponse<>(
                200,
                getMessage("comment.update.success"),
                ResidentsCommentResponseDto.from(comment)
        );
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}