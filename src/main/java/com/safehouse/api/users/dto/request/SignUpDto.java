package com.safehouse.api.users.dto.request;

public interface SignUpDto {
    String getUserName();
    String getEmail();
    String getPassword();
    String getConfirmPassword();
    String getPhone();
    String getDetailAddress();
}
