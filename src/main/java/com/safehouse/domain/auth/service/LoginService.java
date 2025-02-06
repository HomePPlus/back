package com.safehouse.domain.auth.service;
import com.safehouse.api.auth.login.dto.response.LoginResponseDto;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.common.exception.CustomException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContextHolder;
import com.safehouse.domain.user.entity.User;
import com.safehouse.api.auth.login.dto.request.LoginRequestDto;
import com.safehouse.domain.user.repository.UserRepository;
import com.safehouse.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// 로그인 처리, JWT 토큰 생성 및 검증, 아이디 비번 찾기
@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MessageSource messageSource;

    public ApiResponse<LoginResponseDto> login(LoginRequestDto loginDto, HttpServletResponse response) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new CustomException.NotFoundException(getMessage("user.not.found")));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new CustomException.PasswordException(getMessage("password.mismatch"));
        }

        if (!user.isEmailVerified()) {
            throw new CustomException.EmailNotVerifiedException(getMessage("email.not.verified"));
        }

        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole(), user.getId());
        jwtTokenProvider.addTokenToCookie(token, response);

        return new ApiResponse<>(
                200,
                getMessage("login.success"),
                new LoginResponseDto(
                    getMessage("login.success"), 
                    user.getRole(), 
                    user.getId(),
                    token
                )
        );
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
