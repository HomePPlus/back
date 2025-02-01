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

    public LoginResponseDto(String message, String userType) {
        this.message = message;
        this.userType = userType;
    }
}
