package com.safehouse.dto;

import com.safehouse.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileDto {
    private String userName;
    private String email;
    private String phone;
    private String detailAddress;
    private String role;

    public UserProfileDto(User user) {
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.detailAddress = user.getDetailAddress();
        this.role = user.getRole();
    }
}
