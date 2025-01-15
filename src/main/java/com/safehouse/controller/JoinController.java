package com.safehouse.controller;

import com.safehouse.dto.InspectorSignUpDto;
import com.safehouse.dto.ResidentSignUpDto;
import com.safehouse.service.EmailValidationService;
import com.safehouse.service.EmailVerificationService;
import com.safehouse.service.RegistrationService;
import com.safehouse.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class JoinController {
    private final RegistrationService registrationService;
    private final UserProfileService userProfileService;
    private final EmailVerificationService emailVerificationService;
    private final EmailValidationService emailValidationService;
    private final MessageSource messageSource;


    @PostMapping("/resident/join")
    public ResponseEntity<?> registerResident(@RequestBody @Valid ResidentSignUpDto dto) {
        return registrationService.registerResident(dto);
    }

    @PostMapping("/inspector/join")
    public ResponseEntity<?> registerInspector(@RequestBody @Valid InspectorSignUpDto dto) {
        return registrationService.registerInspector(dto);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        return userProfileService.getProfile();
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        return emailVerificationService.verifyEmail(token);
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        return emailValidationService.checkEmail(email);
    }

    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        return emailVerificationService.sendVerificationCode(request.get("email"));
    }
}