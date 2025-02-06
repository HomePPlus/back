package com.safehouse.api.auth.login.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponseDto {
    private String message;
    private String userType; // "RESIDENT" 또는 "INSPECTOR"
    private Long userId;     // userId 추가
    private String token;  // JWT 토큰 추가

    public LoginResponseDto(String message, String userType, Long userId, String token) {
        this.message = message;
        this.userType = userType;
        this.userId = userId;
        this.token = token;
    }


}
