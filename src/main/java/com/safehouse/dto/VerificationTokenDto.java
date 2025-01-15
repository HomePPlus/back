package com.safehouse.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationTokenDto {
    private String email;
    private String token;
}
