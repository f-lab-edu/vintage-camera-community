package com.zerozone.vintage.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.util.Map;

@Getter
public class CustomException  extends RuntimeException{
    private final BindingResult bindingResult;
    private final Map<String, String> errorMap;
    private final HttpStatus status;

    public CustomException(String message) {
        super(message);
        this.bindingResult = null;
        this.errorMap = null;
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public CustomException(String message, BindingResult bindingResult) {
        super(message);
        this.bindingResult = bindingResult;
        this.errorMap = null;
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public CustomException(String message, Map<String, String> errorMap) {
        super(message);
        this.bindingResult = null;
        this.errorMap = errorMap;
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.bindingResult = null;
        this.errorMap = null;
        this.status = status;
    }
}
