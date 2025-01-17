package com.safehouse.api.users.dto.response;
import lombok.Getter;
import lombok.AllArgsConstructor;

//이메일 인증 코드
@Getter
@AllArgsConstructor
public class EmailVerificationResponseDto {
    private boolean success;    // 인증 성공 여부
    private String message;     // 결과 메시지
    private String status;      // SENT, VERIFIED, EXPIRED, INVALID 등의 상태
}
