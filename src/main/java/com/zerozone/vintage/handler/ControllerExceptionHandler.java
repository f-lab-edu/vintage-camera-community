package com.zerozone.vintage.handler;

import com.zerozone.vintage.dto.CustomResDto;
import com.zerozone.vintage.exception.CustomException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    /**
     * CustomException과 MethodArgumentNotValidException 유형의 예외를 처리.
     * 유효성 검사 결과 실패 or 사용자 정의 예외가 발생했을 때.
     *
     * @param ex 처리할 예외 객체 (CustomException 또는 MethodArgumentNotValidException)
     * @return CustomResDto
     */

    @ExceptionHandler({CustomException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<CustomResDto<?>> handleException(Exception ex) {
        BindingResult bindingResult = null;

        if (ex instanceof CustomException customException) {
            if (customException.getBindingResult() != null) {
                bindingResult = customException.getBindingResult();
            } else if (customException.getErrorMap() != null) {
                return ResponseEntity.badRequest().body(new CustomResDto<>(0, "오류 발생가 했습니다.", customException.getErrorMap()));
            } else {
                return ResponseEntity.badRequest().body(new CustomResDto<>(0, customException.getMessage(), null));
            }
        } else if (ex instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException) ex).getBindingResult();
        }

        if (bindingResult != null) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (oldValue, newValue) -> oldValue));
            return ResponseEntity.badRequest().body(new CustomResDto<>(0, "유효성 검사 실패했습니다.", errors));
        }

        return ResponseEntity.badRequest().body(new CustomResDto<>(0, "오류가 발생 했습니다.", null));
    }

}
