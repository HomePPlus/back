package com.safehouse.api.users;

import com.safehouse.api.users.dto.request.InspectorSignUpDto;
import com.safehouse.api.users.dto.request.ResidentSignUpDto;
import com.safehouse.api.users.dto.response.EmailVerificationResponseDto;
import com.safehouse.api.users.dto.response.RegistrationResponseDto;
import com.safehouse.api.users.dto.response.UserProfileResponseDto;
import com.safehouse.domain.user.service.EmailValidationService;
import com.safehouse.domain.user.service.EmailVerificationService;
import com.safehouse.domain.user.service.RegistrationService;
import com.safehouse.domain.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.safehouse.api.users.dto.response.EmailValidationResponseDto;
import com.safehouse.common.response.ApiResponse;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class JoinController {
    private final RegistrationService registrationService;
    private final UserProfileService userProfileService;
    private final EmailVerificationService emailVerificationService;
    private final EmailValidationService emailValidationService;


    @PostMapping("/resident/join")
    public ApiResponse<RegistrationResponseDto> registerResident(
            @RequestBody @Valid ResidentSignUpDto dto) {
        return registrationService.registerResident(dto);
    }

    @PostMapping("/inspector/join")
    public ApiResponse<RegistrationResponseDto> registerInspector(
            @RequestBody @Valid InspectorSignUpDto dto) {
        return registrationService.registerInspector(dto);
    }

    @GetMapping("/profile")
    public ApiResponse<UserProfileResponseDto> getProfile() {
        return userProfileService.getProfile();
    }

    @GetMapping("/check-email")
    public ApiResponse<EmailValidationResponseDto> checkEmail(@RequestParam String email) {
        return emailValidationService.checkEmail(email);
    }

    @PostMapping("/send-verification")
    public ApiResponse<EmailVerificationResponseDto> sendVerificationCode(@RequestParam String email) {
        return emailVerificationService.sendVerificationCode(email);
    }

    @PostMapping("/verify-code")
    public ApiResponse<EmailVerificationResponseDto> verifyCode(
            @RequestParam String email,
            @RequestParam String code) {
        return emailVerificationService.verifyEmail(email, code);
    }
}