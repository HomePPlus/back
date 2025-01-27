package com.safehouse.api.auth.login;

import com.safehouse.api.auth.login.dto.request.LoginDto;
import com.safehouse.api.auth.login.dto.response.LoginResponseDto;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.auth.service.LoginService;
import jakarta.servlet.http.HttpServletResponse;
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
    public ApiResponse<LoginResponseDto> login(
            @RequestBody @Valid LoginDto loginDto,
            HttpServletResponse response // 여기도 확인
    ) {
        return userService.login(loginDto, response);
    }
}