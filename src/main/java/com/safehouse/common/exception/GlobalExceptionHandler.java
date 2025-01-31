package com.safehouse.common.exception;

import com.safehouse.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 유효성 검사 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ApiResponse<>(400, "Validation error", errors);
    }

    // 커스텀 예외 통합 처리
    @ExceptionHandler({
            CustomException.NotFoundException.class,
            CustomException.ForbiddenException.class,
            CustomException.ConflictException.class,
            CustomException.UnauthorizedException.class
    })
    public ApiResponse<?> handleCustomExceptions(RuntimeException ex) {
        int statusCode = determineStatusCode(ex);
        return new ApiResponse<>(statusCode, ex.getMessage(), null);
    }

    private int determineStatusCode(RuntimeException ex) {
        if (ex instanceof CustomException.NotFoundException) return 404;
        if (ex instanceof CustomException.ForbiddenException) return 403;
        if (ex instanceof CustomException.UnauthorizedException) return 401;
        if (ex instanceof CustomException.ConflictException) return 409;
        return 400;
    }
}
