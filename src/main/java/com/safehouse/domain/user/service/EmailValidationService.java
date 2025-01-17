package com.safehouse.domain.user.service;
import com.safehouse.api.users.dto.response.EmailValidationResponseDto;
import com.safehouse.common.exception.CustomException;
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
        if (userRepository.existsByEmail(email)) {
            throw new CustomException.EmailAlreadyExistsException(getMessage("email.duplicate"));
        }

        EmailValidationResponseDto responseDto = new EmailValidationResponseDto(
                true,
                getMessage("email.available")
        );

        return new ApiResponse<>(
                200,
                getMessage("email.available"),
                responseDto
        );
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
