package com.safehouse.api.auth.login;

import com.safehouse.api.auth.login.dto.request.LoginDto;
import com.safehouse.api.auth.login.dto.response.LoginResponseDto;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.auth.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final LoginService userService;

    public LoginController(LoginService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@RequestBody @Valid LoginDto loginDto) {
        return userService.login(loginDto);
    }
}


