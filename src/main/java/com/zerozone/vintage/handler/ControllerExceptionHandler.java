package com.zerozone.vintage.handler;

import com.zerozone.vintage.exception.EmailException;
import com.zerozone.vintage.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    /**
    *
    * @Valid 어노테이션이 적용된 객체에 발생한 유효성 검증 실패
    *
    */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        Map<String, String> errors = result.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        log.info("Validation failed: {}", errors);
        return ResponseEntity.badRequest().body(Collections.singletonMap("errors", errors));
    }

    /**
     *
     * 사용자 정의 예외 중 잘못된 이메일 예외를 처리
     *
     */
    @ExceptionHandler(EmailException.InvalidEmailException.class)
    public ResponseEntity<Map<String, String>> handleInvalidEmailException(EmailException.InvalidEmailException ex) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(responseMap);
    }

    /**
     *
     * 사용자 정의 예외 중 잘못된 토큰 예외를 처리
     *
     */
    @ExceptionHandler(EmailException.InvalidTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTokenException(EmailException.InvalidTokenException ex) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(responseMap);
    }

    /**
     *
     * 이메일 전송 실패 예외 처리
     *
     */
    @ExceptionHandler(EmailException.EmailSendException.class)
    public ResponseEntity<Map<String, String>> handleEmailSendException(EmailException.EmailSendException ex) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(responseMap);
    }

    /**
     * 새로 정의한 유효성 검사 예외를 처리
     *
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException validationException) {
        BindingResult result = validationException.getBindingResult();
        Map<String, String> errors = result.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (oldValue, newValue) -> oldValue));

        Map<String, Object> response = new HashMap<>();
        response.put("errors", errors);
        response.put("status", "validation error");
        return ResponseEntity.badRequest().body(response);
    }


}
