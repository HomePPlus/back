package com.safehouse.api.auth.account.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VerifyResetCodeRequestDto {
    @NotBlank(message = "email.notblank")
    @Email(message = "email.format")
    private String email;

    @NotBlank(message = "email.not.verified")
    private String code;

}