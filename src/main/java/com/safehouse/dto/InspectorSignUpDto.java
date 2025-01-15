package com.safehouse.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;


// InspectorSignUpDto.java (관리자 추가 정보를 담는 DTO)
@Getter @Setter
@NoArgsConstructor
public class InspectorSignUpDto extends UserSignUpDto implements SignUpDto {
    @NotBlank(message = "{inspector_company.notblank}")
    private String inspector_company;

    @NotBlank(message = "{inspector_number.notblank}")
    private String inspector_number;

}
