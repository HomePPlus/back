package com.safehouse.domain.auth.service;

import com.safehouse.common.response.ApiResponse;
import com.safehouse.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MessageSource messageSource;

    public ApiResponse<Void> logout(String token, HttpServletResponse response) {
        // 환경에 따라 도메인 설정 (프로덕션 vs 로컬)
        String domain = isProduction() ? "koreacentral-01.azurewebsites.net" : "localhost";

        // JWT 토큰을 블랙리스트에 추가
        jwtTokenProvider.addToBlacklist(token);

        // JWT_TOKEN 쿠키 삭제
        Cookie jwtCookie = new Cookie("JWT_TOKEN", null);
        jwtCookie.setMaxAge(0); // 쿠키 만료
        jwtCookie.setPath("/"); // 루트 경로에 적용
        jwtCookie.setHttpOnly(true); // 클라이언트에서 접근 불가 (보안 강화)
        jwtCookie.setSecure(true); // HTTPS 환경에서만 전송
        jwtCookie.setDomain(domain); // 배포된 도메인으로 설정 (필수)
        response.addCookie(jwtCookie);

        // isAuthenticated 쿠키 삭제
        Cookie isAuthCookie = new Cookie("isAuthenticated", null);
        isAuthCookie.setMaxAge(0); // 쿠키 만료
        isAuthCookie.setPath("/"); // 루트 경로에 적용
        isAuthCookie.setHttpOnly(false); // 클라이언트에서 접근 가능 (UI 용도)
        isAuthCookie.setSecure(true); // HTTPS 환경에서만 전송
        isAuthCookie.setDomain(domain); // 환경에 따라 도메인 설정
        response.addCookie(isAuthCookie);

        return new ApiResponse<>(
                200,
                getMessage("logout.success"),
                null
        );
    }

    /**
     * 프로덕션 환경 여부를 확인하는 메서드
     * @return true if production, false otherwise
     */
    private boolean isProduction() {
        // Spring 환경 변수를 사용하여 프로파일 확인
        String activeProfile = System.getProperty("spring.profiles.active", "default");
        return "prod".equalsIgnoreCase(activeProfile);
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
