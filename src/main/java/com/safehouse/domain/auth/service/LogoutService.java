package com.safehouse.domain.auth.service;

import com.safehouse.common.response.ApiResponse;
import com.safehouse.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MessageSource messageSource;

    public ApiResponse<Void> logout(String token) {
        jwtTokenProvider.addToBlacklist(token);

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
