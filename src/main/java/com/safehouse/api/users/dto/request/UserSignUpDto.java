package com.safehouse.api.users.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@Getter @Setter
@NoArgsConstructor
public class UserSignUpDto {
    @NotBlank(message = "{userName.notblank}")
    private String userName;

    @Email(message = "{email.format}")
    @NotBlank(message = "{email.notblank}")
    private String email;

    @NotBlank(message = "{password.notblank}")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&(){}^])[A-Za-z\\d@$!%*#?&(){}^]{8,24}$",
            message = "{password.format}")
    private String password;

    @NotBlank(message = "{detailAddress.notblank}")
    private String detailAddress;   //주소

    @Pattern(regexp = "^[0-9]{10,11}$", message = "{phone.format}")
    @NotBlank(message = "{phone.notblank}")
    private String phone;
}
