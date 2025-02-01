package com.safehouse.common.exception;

import com.safehouse.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 유효성 검사 실패 예외 처리 (400 Bad Request)
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
            CustomException.BadRequestException.class,    // 잘못된 요청 (400)
            CustomException.NotFoundException.class,     // 리소스 없음 (404)
            CustomException.ForbiddenException.class,    // 권한 없음 (403)
            CustomException.UnauthorizedException.class,// 인증 실패 (401)
            CustomException.ConflictException.class      // 리소스 충돌 (409)
    })
    public ApiResponse<?> handleCustomExceptions(RuntimeException ex) {
        int statusCode = determineStatusCode(ex);
        return new ApiResponse<>(statusCode, ex.getMessage(), null);
    }

    // 예외 타입별 HTTP 상태 코드 매핑
    private int determineStatusCode(RuntimeException ex) {
        if (ex instanceof CustomException.BadRequestException) return 400;
        if (ex instanceof CustomException.NotFoundException) return 404;
        if (ex instanceof CustomException.ForbiddenException) return 403;
        if (ex instanceof CustomException.UnauthorizedException) return 401;
        if (ex instanceof CustomException.ConflictException) return 409;
        return 400; // 기본값 (처리되지 않은 예외)
    }
}
