package com.safehouse.domain.resident_community.service;

import com.safehouse.api.resident_community.request.ResidentsCommunityRequestDto;
import com.safehouse.api.resident_community.response.ResidentsCommunityResponseDto;
import com.safehouse.common.exception.CustomException;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.resident_community.entity.ResidentsCommunity;
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

//@Service
//@Transactional
//@RequiredArgsConstructor
//public class ResidentsCommunityService {
//    private final ResidentsCommunityRepository communityRepository;
//    private final UserRepository userRepository;
//    private final MessageSource messageSource;
//
//    public ResidentsCommunityResponseDto createPost(ResidentsCommunityRequestDto requestDto, String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new CustomException.UserNotFoundException(getMessage("user.not.found")));
//
//        ResidentsCommunity community = ResidentsCommunity.builder()
//                .communityTitle(requestDto.getCommunityTitle())
//                .communityContent(requestDto.getCommunityContent())
//                .communityViews(0L)
//                .user(user)
//                .build();
//
//        ResidentsCommunity savedCommunity = communityRepository.save(community);
//        return convertToDto(savedCommunity);
//    }
//
//    @Transactional() // 게시글 조회
//    public ResidentsCommunityResponseDto getPost(Long postId) {
//        ResidentsCommunity community = communityRepository.findByIdWithUser(postId)
//                .orElseThrow(() -> new CustomException.PostNotExist(getMessage("post.not.found")));
//
//        community.increaseViews();
//        return convertToDto(community);
//    }
//
//    private ResidentsCommunityResponseDto convertToDto(ResidentsCommunity community) {
//        return ResidentsCommunityResponseDto.builder()
//                .communityPostId(community.getCommunityPostId())
//                .communityTitle(community.getCommunityTitle())
//                .communityContent(community.getCommunityContent())
//                .userName(community.getUser().getUserRealName())
//                .userEmail(community.getUser().getEmail())
//                .communityCreatedAt(community.getCommunityCreatedAt())
//                .communityUpdatedAt(community.getCommunityUpdatedAt())
//                .communityViews(community.getCommunityViews())
//                .build();
//    }
//
//    public List<ResidentsCommunityResponseDto> getAllPosts() { //전체 게시글 조회
//        return communityRepository.findAllByOrderByCommunityCreatedAtDesc()
//                .stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }
//
//    private String getMessage(String code) {
//        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
//    }
//
//    public ResidentsCommunityResponseDto updatePost(Long communityPostId, ResidentsCommunityRequestDto requestDto) {
//        ResidentsCommunity community = communityRepository.findById(communityPostId)
//                .orElseThrow(() -> new CustomException.PostNotExist(getMessage("post.not.found")));
//
//        community.update(requestDto.getCommunityTitle(), requestDto.getCommunityContent());
//
//        return ResidentsCommunityResponseDto.from(community);
//    }
//
//    public boolean isOwner(Long communityPostId, String userEmail) {
//        ResidentsCommunity community = communityRepository.findById(communityPostId)
//                .orElseThrow(() -> new CustomException.PostNotOwner(getMessage("post.not.owner")));
//        return community.getUser().getEmail().equals(userEmail);
//    }
//
//    public void deletePost(Long communityPostId) {
//        ResidentsCommunity community = communityRepository.findById(communityPostId)
//                .orElseThrow(() -> new CustomException.PostNotExist(getMessage("post.not.found")));
//        communityRepository.delete(community);
//    }
//}

@Service
@Transactional
@RequiredArgsConstructor
public class ResidentsCommunityService {
    private final ResidentsCommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

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

        community.increaseViews();
        return new ApiResponse<>(
                200,
                getMessage("post.get.success"),
                convertToDto(community)
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

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}


