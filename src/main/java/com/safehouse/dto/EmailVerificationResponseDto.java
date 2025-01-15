package com.safehouse.dto;
//인증 코드 전송 성공
//이메일 인증 완료
//인증 코드 만료
//잘못된 인증 코드
public class EmailVerificationResponseDto {
    private String message;

    public EmailVerificationResponseDto(String message) {
        this.message = message;
    }

    // getter
    public String getMessage() {
        return message;
    }
}
