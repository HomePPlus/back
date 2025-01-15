package com.safehouse.service;
import com.safehouse.dto.LoginResponseDto;
import com.safehouse.exception.CustomException;
import org.springframework.context.i18n.LocaleContextHolder;
import com.safehouse.domain.User;
import com.safehouse.dto.LoginDto;
import com.safehouse.repository.UserRepository;
import com.safehouse.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// 로그인 처리, JWT 토큰 생성 및 검증
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MessageSource messageSource;

    public ResponseEntity<?> login(LoginDto loginDto) {
        try {
            User user = userRepository.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new CustomException.UserNotFoundException(getMessage("user.not.found")));

            if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                throw new CustomException.PasswordMismatchException(getMessage("password.mismatch"));
            }

            if (!user.isEmailVerified()) {
                throw new CustomException.EmailNotVerifiedException(getMessage("email.not.verified"));
            }

            String token = jwtTokenProvider.createToken(user.getEmail());
            LoginResponseDto responseDto = new LoginResponseDto(token, getMessage("login.success"));
            return ResponseEntity.ok(responseDto);
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
