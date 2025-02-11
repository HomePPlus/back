package com.safehouse.domain.user.service;
import com.safehouse.api.users.dto.response.EmailValidationResponseDto;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

// 이메일 중복 확인
@Service
@RequiredArgsConstructor
public class EmailValidationService {
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    public ApiResponse<EmailValidationResponseDto> checkEmail(String email) {
        // 이메일 null 또는 빈 문자열 체크
        if (email == null || email.trim().isEmpty()) {
            return ApiResponse.error(400, getMessage("email.required"));
        }

        // 이메일 중복 체크
        if (userRepository.existsByEmail(email)) {
            return ApiResponse.error(409, getMessage("email.duplicate"));
        }

        // 사용 가능한 이메일인 경우
        EmailValidationResponseDto responseDto = new EmailValidationResponseDto(
                true,
                getMessage("email.available")
        );

        return ApiResponse.ok(getMessage("email.available"), responseDto);
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
