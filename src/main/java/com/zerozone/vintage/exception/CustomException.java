package com.zerozone.vintage.exception;

import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.util.Map;

@Getter
public class CustomException  extends RuntimeException{
    private final BindingResult bindingResult;
    private final Map<String, String> errorMap;

    public CustomException(String message) {
        super(message);
        this.bindingResult = null;
        this.errorMap = null;
    }

    public CustomException(String message, BindingResult bindingResult) {
        super(message);
        this.bindingResult = bindingResult;
        this.errorMap = null;
    }

    public CustomException(String message, Map<String, String> errorMap) {
        super(message);
        this.bindingResult = null;
        this.errorMap = errorMap;
    }

}
