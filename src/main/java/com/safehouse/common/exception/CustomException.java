package com.safehouse.common.exception;

public class CustomException {
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class VerificationCodeExpiredException extends RuntimeException {
        public VerificationCodeExpiredException(String message) {
            super(message);
        }
    }

    public static class InvalidVerificationCodeException extends RuntimeException {
        public InvalidVerificationCodeException(String message) {
            super(message);
        }
    }

    public static class EmailNotVerifiedException extends RuntimeException {
        public EmailNotVerifiedException(String message) {
            super(message);
        }
    }

    public static class PasswordMismatchException extends RuntimeException {
        public PasswordMismatchException(String message) {
            super(message);
        }
    }
    // 신고 내용이 비어있을 때 발생하는 예외처리
    public static class InvalidRequestException extends RuntimeException {
        public InvalidRequestException(String message) {
            super(message);
        }
    }
    // JSON 형식이 잘못되었을 때 발생하는 예외처리
    public static class InvalidJsonFormatException extends RuntimeException {
        public InvalidJsonFormatException(String message) {
            super(message);
        }
    }
    // 파일 업로드 실패 시 발생하는 예외처리
    public static class FileUploadException extends RuntimeException {
        public FileUploadException(String message) {
            super(message);
        }
    }


}
