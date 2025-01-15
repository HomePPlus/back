package com.safehouse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

// ResidentSignUpDto.java (입주민 추가 정보를 담는 DTO)
@Getter @Setter
@NoArgsConstructor
public class ResidentSignUpDto extends UserSignUpDto implements SignUpDto {

//    @NotNull(message = "건물 ID는 필수입니다.")
//    private Long buildingId;
}