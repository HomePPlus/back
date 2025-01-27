package com.safehouse.api.auth.login.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponseDto {
    private String message;

    public LoginResponseDto(String message) {
        this.message = message;
    }
}
