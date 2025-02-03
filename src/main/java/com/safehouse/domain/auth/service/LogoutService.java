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
        jwtTokenProvider.addToBlacklist(token);
        
        Cookie cookie = new Cookie("JWT_TOKEN", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setSecure(true);
        response.addCookie(cookie);
        
        Cookie isAuthCookie = new Cookie("isAuthenticated", null);
        isAuthCookie.setMaxAge(0);
        isAuthCookie.setPath("/");
        isAuthCookie.setSecure(true);
        response.addCookie(isAuthCookie);

        return new ApiResponse<>(
                200,
                getMessage("logout.success"),
                null
        );
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
