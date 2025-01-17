package com.safehouse.api.users.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

//메시지 필드: 이메일 유효성 검사 결과에 대한 메시지를 포함.
// 이메일 중복 확인
@Getter
@AllArgsConstructor
public class EmailValidationResponseDto {
    private boolean available;  // 이메일 사용 가능 여부
    private String message;     // 결과 메시지
}
