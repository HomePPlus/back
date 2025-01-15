package com.safehouse.controller;

import com.safehouse.dto.LoginDto;
import com.safehouse.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    private final AuthenticationService userService;

    public LoginController(AuthenticationService userService) {
        this.userService = userService;
    }
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto);
    }
}
