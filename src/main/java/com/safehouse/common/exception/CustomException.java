package com.safehouse.common.exception;

public class CustomException {
    // 사용자를 찾을 수 없을 때 발생하는 예외
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    // 이미 존재하는 이메일로 회원가입을 시도할 때 발생하는 예외
    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }

    // 인증 코드가 만료되었을 때 발생하는 예외
    public static class VerificationCodeExpiredException extends RuntimeException {
        public VerificationCodeExpiredException(String message) {
            super(message);
        }
    }

    // 잘못된 인증 코드를 입력했을 때 발생하는 예외
    public static class InvalidVerificationCodeException extends RuntimeException {
        public InvalidVerificationCodeException(String message) {
            super(message);
        }
    }

    // 이메일 인증이 완료되지 않은 상태에서 로그인을 시도할 때 발생하는 예외
    public static class EmailNotVerifiedException extends RuntimeException {
        public EmailNotVerifiedException(String message) {
            super(message);
        }
    }

    // 비밀번호가 일치하지 않을 때 발생하는 예외
    public static class PasswordMismatchException extends RuntimeException {
        public PasswordMismatchException(String message) {
            super(message);
        }
    }

    // 신고 내용이 비어있을 때 발생하는 예외
    public static class InvalidRequestException extends RuntimeException {
        public InvalidRequestException(String message) {
            super(message);
        }
    }

    // JSON 형식이 잘못되었을 때 발생하는 예외
    public static class InvalidJsonFormatException extends RuntimeException {
        public InvalidJsonFormatException(String message) {
            super(message);
        }
    }

    // 파일 업로드 실패 시 발생하는 예외
    public static class FileUploadException extends RuntimeException {
        public FileUploadException(String message) {
            super(message);
        }
    }

    // 비밀번호 재설정 과정에서 문제가 발생했을 때 발생하는 예외
    public static class PasswordResetFailedException extends RuntimeException {
        public PasswordResetFailedException(String message) {
            super(message);
        }
    }

    // 이메일 전송 실패 시 발생하는 예외
    public static class EmailSendingFailedException extends RuntimeException {
        public EmailSendingFailedException(String message) {
            super(message);
        }
    }
    // 일정 관리 예외
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
    // 중복 예약 예외 처리
    public static class DuplicateScheduleException extends RuntimeException {
        public DuplicateScheduleException(String message) {
            super(message);
        }
    }
    //게시글이 없을때 발생하는 예외
    public static class PostNotExist extends RuntimeException{
        public PostNotExist(String message){
            super(message);
        }
    }

    //게시글이 본인것이 아닐때
    public static class PostNotOwner extends RuntimeException{
        public PostNotOwner(String message){
            super(message);
        }
    }


    public static class ResourceNotFoundException extends RuntimeException{
        public ResourceNotFoundException(String message){
            super(message);
        }
    }
}
