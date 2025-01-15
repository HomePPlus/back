package com.safehouse.service;
import com.safehouse.domain.User;
import com.safehouse.dto.UserProfileDto;
import com.safehouse.exception.CustomException;
import com.safehouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

// 사용자 프로필 조회 및 수정
@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    public ResponseEntity<?> getProfile() {
        // Get the authenticated user's email
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException.UserNotFoundException(getMessage("user.not.found")));
            UserProfileDto profileDto = new UserProfileDto(user);
            return ResponseEntity.ok(profileDto);
        } catch (CustomException.UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}

