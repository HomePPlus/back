package com.safehouse.api.users.dto.response;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class RegistrationResponseDto {
    private boolean success;    // 회원가입 성공 여부
    private String message;     // 결과 메시지
    private String userType;    // 사용자 타입 (RESIDENT/INSPECTOR)
}

