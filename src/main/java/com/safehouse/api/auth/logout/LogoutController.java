package com.safehouse.api.auth.logout;

import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.auth.service.LogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LogoutController {

    private final LogoutService logoutService;

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @RequestHeader("Authorization") String token,
            HttpServletResponse response
    ) {
        return logoutService.logout(token.replace("Bearer ", ""), response);
    }

}
