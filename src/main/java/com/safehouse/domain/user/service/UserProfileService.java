package com.safehouse.domain.user.service;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.user.entity.User;
import com.safehouse.api.users.dto.response.UserProfileResponseDto;
import com.safehouse.common.exception.CustomException;
import com.safehouse.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

// 사용자 프로필 조회 및 수정
@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    public ApiResponse<UserProfileResponseDto> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException.NotFoundException(getMessage("user.not.found")));

        UserProfileResponseDto profileDto = new UserProfileResponseDto(user);

        return new ApiResponse<>(
                200,
                getMessage("profile.fetch.success"),
                profileDto
        );
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}

