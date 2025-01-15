package com.safehouse.service;

import com.safehouse.exception.CustomException;
import com.safehouse.domain.VerificationToken;
import com.safehouse.dto.EmailVerificationResponseDto;
import com.safehouse.exception.CustomException;
import com.safehouse.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

//이메일 인증 코드 생성 및 전송, 이메일 인증 처리
@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final MessageSource messageSource;

    @Transactional
    public ResponseEntity<?> verifyEmail(String token) {
        try {
            VerificationToken verificationToken = tokenRepository.findByToken(token)
                    .orElseThrow(() -> new CustomException.InvalidTokenException(getMessage("token.invalid")));

            if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                tokenRepository.delete(verificationToken);
                throw new CustomException.TokenExpiredException(getMessage("token.expired"));
            }

            verificationToken.setVerified(true);
            tokenRepository.save(verificationToken);
            return ResponseEntity.ok(new EmailVerificationResponseDto(getMessage("email.verification.success")));
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> sendVerificationCode(String email) {
        try {
            tokenRepository.findByEmail(email).ifPresent(tokenRepository::delete);

            String token = generateVerificationToken();
            VerificationToken verificationToken = new VerificationToken(email, token);
            tokenRepository.save(verificationToken);

            emailService.sendVerificationEmail(email, token);

            return ResponseEntity.ok(new EmailVerificationResponseDto(getMessage("email.verification.sent")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String generateVerificationToken() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
