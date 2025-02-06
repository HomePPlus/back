package com.safehouse.api.auth.login;

import com.safehouse.api.auth.login.dto.request.LoginRequestDto;
import com.safehouse.api.auth.login.dto.response.LoginResponseDto;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.auth.service.LoginService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
    origins = {
        "http://localhost:3000",
        "https://safehouse-react-a5eyc2a9a0byd5hq.koreacentral-01.azurewebsites.net"
    },
    allowedHeaders = "*",
    methods = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.DELETE,
        RequestMethod.OPTIONS
    },
    allowCredentials = "true",
    maxAge = 3600
)
public class LoginController {

    private final LoginService userService;

    public LoginController(LoginService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(
            @RequestBody @Valid LoginRequestDto loginRequestDto,
            HttpServletResponse response // 여기도 확인
    ) {
        return userService.login(loginRequestDto, response);
    }
}