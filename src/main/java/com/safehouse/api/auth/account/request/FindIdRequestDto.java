package com.safehouse.api.auth.account.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FindIdRequestDto {
    @NotBlank(message = "{user.not.found}")
    private String userName;

    @NotBlank(message = "{phone.notblank}")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "{phone.format}")
    private String phone;
}