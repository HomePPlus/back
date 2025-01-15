package com.safehouse.exception;

public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }

    public static class UserNotFoundException extends CustomException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class EmailAlreadyExistsException extends CustomException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class PasswordMismatchException extends CustomException {
        public PasswordMismatchException(String message) {
            super(message);
        }
    }

    public static class EmailNotVerifiedException extends CustomException {
        public EmailNotVerifiedException(String message) {
            super(message);
        }
    }

    public static class InvalidTokenException extends CustomException {
        public InvalidTokenException(String message) {
            super(message);
        }
    }

    public static class TokenExpiredException extends CustomException {
        public TokenExpiredException(String message) {
            super(message);
        }
    }
}
