package com.safehouse.service;
import com.safehouse.dto.EmailValidationResponseDto;
import com.safehouse.exception.CustomException;
import com.safehouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

// 이메일 중복 확인
@Service
@RequiredArgsConstructor
public class EmailValidationService {
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    public ResponseEntity<?> checkEmail(String email) {
        try {
            if (userRepository.existsByEmail(email)) {
                throw new CustomException.EmailAlreadyExistsException(getMessage("email.duplicate"));
            }
            return ResponseEntity.ok(new EmailValidationResponseDto(getMessage("email.available")));
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}

