package com.example.community.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 에러 응답을 JSON 객체로 생성하는 유틸리티 메서드
    private ResponseEntity<Map<String, String>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return ResponseEntity.status(status).body(error);
    }

    // 400 Bad Request - JSON 요청 오류 또는 RequestBody 누락
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "지원하지 않는 Content-Type입니다.");
    }

    // 400 Bad Request - JSON 요청 오류 또는 RequestBody 누락
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonParseException(HttpMessageNotReadableException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "잘못된 요청입니다. 요청 바디(JSON)가 존재하는지 확인하세요.");
    }

    // 400 Bad Request - @Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    // 400 Bad Request - 일반적인 잘못된 요청
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // 401 Unauthorized - 인증되지 않은 사용자
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException e) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
    }

    // 403 Forbidden - 권한 없음
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException e) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "이 작업을 수행할 권한이 없습니다.");
    }

    // 404 Not Found - 리소스를 찾을 수 없음
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NoSuchElementException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다.");
    }

    // 500 Internal Server Error - 그 외 모든 예기치 못한 오류 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception e) {
        e.printStackTrace();
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류 발생: " + e.getMessage());
    }
}
