package com.safehouse.domain.user.service;

import com.safehouse.api.users.dto.response.EmailVerificationResponseDto;
import com.safehouse.common.exception.CustomException;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.user.entity.VerificationToken;
// import com.safehouse.domain.user.repository.UserRepository;
import com.safehouse.domain.user.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    // private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    // 인증 코드 전송
    public ApiResponse<EmailVerificationResponseDto> sendVerificationCode(String email) {
        // 6자리 랜덤 인증 코드 생성
        String verificationCode = generateVerificationCode();

        try {
            // 이메일 전송
            emailService.sendVerificationEmail(email, verificationCode);

            // 인증 코드 저장
            saveVerificationToken(email, verificationCode);

            EmailVerificationResponseDto responseDto = new EmailVerificationResponseDto(
                    true,
                    getMessage("email.verification.sent"),
                    "SENT"
            );

            return new ApiResponse<>(
                    200,
                    getMessage("email.verification.sent"),
                    responseDto
            );
        } catch (Exception e) {
            throw new RuntimeException("이메일 전송에 실패했습니다.", e);
        }
    }

    // 인증 코드 확인
    public ApiResponse<EmailVerificationResponseDto> verifyEmail(String email, String code) {
        try {
            // 입력값 검증
            if (email == null || email.trim().isEmpty()) {
                return ApiResponse.error(400, getMessage("email.required"));
            }
            if (code == null || code.trim().isEmpty()) {
                return ApiResponse.error(400, getMessage("verification.code.required"));
            }

            // 인증 코드 검증
            VerificationToken verificationToken = verificationTokenRepository
                    .findByEmailAndCode(email, code)
                    .orElse(null);
                    
            if (verificationToken == null) {
                return ApiResponse.error(400, getMessage("email.verification.invalid"));
            }

            if (isCodeExpired(verificationToken.getExpiryDate())) {
                return ApiResponse.error(400, getMessage("email.verification.expired"));
            }

            // 인증 성공 처리
            verificationToken.setVerified(true);
            verificationTokenRepository.save(verificationToken);

            EmailVerificationResponseDto responseDto = new EmailVerificationResponseDto(
                    true,
                    getMessage("email.verification.success"),
                    "VERIFIED"
            );

            return ApiResponse.ok(getMessage("email.verification.success"), responseDto);

        } catch (Exception e) {
            return ApiResponse.error(500, getMessage("verification.failed"));
        }
    }

    // 6자리 랜덤 인증 코드 생성
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    // 인증 코드 저장
    private void saveVerificationToken(String email, String code) {
        // 기존 코드 삭제
        verificationTokenRepository.deleteByEmail(email);

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setEmail(email);
        verificationToken.setCode(code);
        verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(30)); // 30분 유효
        verificationToken.setVerified(false);
        verificationTokenRepository.save(verificationToken);
    }


    private boolean isCodeExpired(LocalDateTime expiryDate) {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        List<VerificationToken> expiredTokens = verificationTokenRepository
                .findByExpiryDateBeforeAndVerifiedFalse(now);
        verificationTokenRepository.deleteAll(expiredTokens);
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
