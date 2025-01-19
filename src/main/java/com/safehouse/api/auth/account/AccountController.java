package com.safehouse.api.auth.account;


import com.safehouse.api.auth.account.request.FindIdRequestDto;
import com.safehouse.api.auth.account.request.ForgotPasswordRequestDto;
import com.safehouse.api.auth.account.request.ResetPasswordRequestDto;
import com.safehouse.api.auth.account.request.VerifyResetCodeRequestDto;
import com.safehouse.api.auth.account.response.FindIdResponseDto;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.auth.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/find-id")
    public ApiResponse<FindIdResponseDto> findId(@RequestBody @Valid FindIdRequestDto findIdRequestDto) {
        return accountService.findId(findIdRequestDto);
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDto dto) {
        return accountService.sendPasswordResetCode(dto.getEmail());
    }

    @PostMapping("/verify-reset-code")
    public ApiResponse<Void> verifyResetCode(@RequestBody @Valid VerifyResetCodeRequestDto dto) {
        return accountService.verifyPasswordResetCode(dto.getEmail(), dto.getCode());
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody @Valid ResetPasswordRequestDto dto) {
        return accountService.resetPassword(dto.getEmail(), dto.getNewPassword());
    }
}

