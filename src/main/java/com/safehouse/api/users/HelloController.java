package com.safehouse.api.users;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/api/users/hello")
    public String hello() {
        return "Hello, World!";
    }
}
