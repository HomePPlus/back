package com.safehouse.domain.auth.service;

import com.safehouse.api.auth.account.request.FindIdRequestDto;
import com.safehouse.api.auth.account.request.ResetPasswordRequestDto;
import com.safehouse.api.auth.account.response.FindIdResponseDto;
import com.safehouse.common.exception.CustomException;
import com.safehouse.common.response.ApiResponse;

import com.safehouse.domain.user.entity.VerificationToken;
import com.safehouse.domain.user.repository.UserRepository;
import com.safehouse.common.security.JwtTokenProvider;
import com.safehouse.domain.user.entity.User;
import com.safehouse.domain.user.repository.VerificationTokenRepository;
import com.safehouse.domain.user.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {
    private final MessageSource messageSource;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // 아이디 찾기
    public ApiResponse<FindIdResponseDto> findId(FindIdRequestDto dto) {
        User user = userRepository.findByUserNameAndPhone(dto.getUserName(), dto.getPhone())
                .orElseThrow(() -> new CustomException.UserNotFoundException(getMessage("user.not.found")));

        String maskedEmail = maskEmail(user.getEmail());

        return new ApiResponse<>(
                200,
                getMessage("id.found"),
                new FindIdResponseDto(maskedEmail)
        );
    }

    private String maskEmail(String email) {
        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        int maskLength = Math.min(2, localPart.length());
        String maskedLocal = localPart.substring(0, localPart.length() - maskLength) + "**";

        return maskedLocal + "@" + domain;
    }

    // 비밀번호 재설정 코드 전송
    public ApiResponse<Void> sendPasswordResetCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException.UserNotFoundException(getMessage("user.not.found")));

        String code = generateVerificationCode();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15); // 15분 유효

        VerificationToken token = verificationTokenRepository.findById(email)
                .orElse(new VerificationToken());
        token.setEmail(email);
        token.setCode(code);
        token.setExpiryDate(expiryDate);
        token.setVerified(false);
        verificationTokenRepository.save(token);

        emailService.sendPasswordResetCode(email, code);

        return new ApiResponse<>(200, getMessage("password.reset.code.sent"), null);
    }

    // 비밀번호 재설정 코드 확인
    public ApiResponse<Void> verifyPasswordResetCode(String email, String code) {
        VerificationToken token = verificationTokenRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new CustomException.InvalidVerificationCodeException(getMessage("token.invalid")));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new CustomException.PasswordResetFailedException(getMessage("token.expired"));
        }

        token.setVerified(true);
        verificationTokenRepository.save(token);

        return new ApiResponse<>(200, getMessage("password.reset.code.verified"), null);
    }

    // 새 비밀번호 설정
    public ApiResponse<Void> resetPassword(String email, String newPassword) {
        VerificationToken token = verificationTokenRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException.InvalidVerificationCodeException(getMessage("token.invalid")));

        if (!token.isVerified()) {
            throw new CustomException.PasswordResetFailedException(getMessage("token.not.verified"));
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException.UserNotFoundException(getMessage("user.not.found")));

        log.info("Before password reset: {}", user);
        user.setPassword(passwordEncoder.encode(newPassword));
        User savedUser = userRepository.save(user);
        log.info("After password reset: {}", savedUser);
        verificationTokenRepository.delete(token);

        return new ApiResponse<>(200, getMessage("password.reset.success"), null);
    }

    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }



}

