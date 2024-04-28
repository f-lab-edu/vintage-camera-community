package com.zerozone.vintage.account;

import com.zerozone.vintage.domain.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Transactional
public class AccountApiController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @PostMapping("/signUp")
    public ResponseEntity<?>  signUpFormSubmit(@Valid SignUpForm signUpForm, BindingResult result , HttpServletRequest request, HttpServletResponse response){
        Account account = accountService.newAccountProcess(signUpForm);
        accountService.login(account, request, response);

        return ResponseEntity.ok(Collections.singletonMap("message", "회원가입이 성공적으로 완료되었습니다."));
    }



}