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
}
