package com.safehouse.api.users.dto.response;

import com.safehouse.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponseDto {
    private String userName;
    private String email;
    private String phone;
    private String address;
    private String detailAddress;
    private String role;

    public UserProfileResponseDto(User user) {
        this.userName = user.getUserRealName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.address = user.getDetailAddress();
        this.detailAddress = user.getDetailAddress();
        this.role = user.getRole();
    }
}
