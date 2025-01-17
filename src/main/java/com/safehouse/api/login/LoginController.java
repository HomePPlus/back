package com.safehouse.api.login;

import com.safehouse.api.login.dto.request.LoginDto;
import com.safehouse.api.login.dto.response.LoginResponseDto;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.auth.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final AuthenticationService userService;

    public LoginController(AuthenticationService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@RequestBody @Valid LoginDto loginDto) {
        return userService.login(loginDto);
    }
}


