package com.safehouse.common.exception;

public class CustomException {
    // 잘못된 요청 (클라이언트 오류)에 대한 예외
    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message);
        }
    }

    // 입력값이 유효하지 않을 때 발생하는 예외 (필수 파라미터 누락, 잘못된 형식의 입력값 등)
    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String message) {
            super(message);
        }
    }

    // 요청한 리소스(사용자, 게시글, 일정 등)를 찾을 수 없을 때 발생하는 예외
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }

    // 이미 존재하는 리소스(이메일, 일정 등)로 인해 충돌이 발생할 때의 예외
    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) {
            super(message);
        }
    }

    // 인증 코드 관련 예외 (만료, 잘못된 코드)
    public static class VerificationException extends RuntimeException {
        public VerificationException(String message) {
            super(message);
        }
    }

    // 이메일 인증이 완료되지 않은 상태에서 로그인을 시도할 때 발생하는 예외
    public static class EmailNotVerifiedException extends RuntimeException {
        public EmailNotVerifiedException(String message) {
            super(message);
        }
    }

    // 비밀번호 관련 예외 (불일치, 재설정 실패)
    public static class PasswordException extends RuntimeException {
        public PasswordException(String message) {
            super(message);
        }
    }

    // 파일 업로드 실패 시 발생하는 예외
    public static class FileUploadException extends RuntimeException {
        public FileUploadException(String message) {
            super(message);
        }
    }

    // 이메일 전송 실패 시 발생하는 예외
    public static class EmailSendingFailedException extends RuntimeException {
        public EmailSendingFailedException(String message) {
            super(message);
        }
    }

    // 인증되지 않은 사용자가 접근을 시도할 때 발생하는 예외
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

    // 접근 권한이 없는 리소스에 접근을 시도할 때 발생하는 예외
    public static class ForbiddenException extends RuntimeException {
        public ForbiddenException(String message) {
            super(message);
        }
    }

    // 지역 검증 실패 예외
    public static class InvalidAreaException extends BadRequestException {
        public InvalidAreaException(String message) {
            super(message);
        }
    }

    // 통계 데이터 없음 예외
    public static class NoStatsDataException extends NotFoundException {
        public NoStatsDataException(String message) {
            super(message);
        }
    }

    // 모델 실행 중 오류 발생 시 발생하는 예외
    public static class ModelExecutionException extends RuntimeException {
        public ModelExecutionException(String message) {
            super(message);
        }
    }

    // JSON 직렬화 실패 시 발생하는 예외
    public static class SerializationException extends RuntimeException {
        public SerializationException(String message) {
            super(message);
        }
    }
}
