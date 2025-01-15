package com.safehouse.dto;

import lombok.Getter;
import lombok.Setter;
//메시지 필드: 이메일 유효성 검사 결과에 대한 메시지를 포함.
// 이메일 중복 확인
@Getter
@Setter
public class EmailValidationResponseDto {
    private String message;

    public EmailValidationResponseDto(String message) {
        this.message = message;
    }
}
