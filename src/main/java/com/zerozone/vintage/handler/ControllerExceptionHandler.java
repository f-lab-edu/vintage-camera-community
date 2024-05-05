package com.zerozone.vintage.handler;

import com.zerozone.vintage.exception.ControllerException;
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

    //공통 에러핸들러 추가
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        Map<String, String> errors = result.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        log.info("Validation failed: {}", errors);
        return ResponseEntity.badRequest().body(Collections.singletonMap("errors", errors));
    }

    @ExceptionHandler(ControllerException.InvalidEmailException.class)
    public ResponseEntity<Map<String, String>> handleInvalidEmailException(ControllerException.InvalidEmailException ex) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(responseMap);
    }

    @ExceptionHandler(ControllerException.InvalidTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTokenException(ControllerException.InvalidTokenException ex) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(responseMap);
    }

    @ExceptionHandler(ControllerException.EmailSendException.class)
    public ResponseEntity<Map<String, String>> handleEmailSendException(ControllerException.EmailSendException ex) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(responseMap);
    }

}
