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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Transactional
public class AccountApiController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @PostMapping("/signUp")
    public ResponseEntity<?>  signUpFormSubmit(@Valid SignUpForm signUpForm, HttpServletRequest request, HttpServletResponse response){
        Account account = accountService.newAccountProcess(signUpForm);
        accountService.login(account, request, response);
        /*
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(account.getId())
                .toUri();

        return ResponseEntity.ok(Collections.singletonMap("message", "회원가입이 성공적으로 완료되었습니다."));
         */
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "success");
        responseMap.put("accountId", account.getId());
        return ResponseEntity.ok(responseMap);
    }

    @GetMapping("/checkEmailToken")
    public ResponseEntity<?> checkEmailToken(@RequestParam String token, @RequestParam String email, HttpServletRequest request, HttpServletResponse response) {
        Account account = accountRepository.findByEmail(email);
        Map<String, Object> responseMap = new HashMap<>();

        if(account == null) {
            responseMap.put("error", "잘못된 이메일입니다.");
            return ResponseEntity.badRequest().body(responseMap);
        }

        if(!account.isValidToken(token)) {
            responseMap.put("error", "잘못된 토근입니다.");
            return ResponseEntity.badRequest().body(responseMap);
        }

        account.completeSignUp();
        accountService.login(account, request, response);
        responseMap.put("message", "success");
        responseMap.put("nickName", account.getNickname());
        return ResponseEntity.ok(responseMap);
    }

    @GetMapping("/resendConfirmEmail")
    public ResponseEntity<?> resendConfirmEmail(@RequestParam String email) {
        Account account = accountRepository.findByEmail(email);

        Map<String, Object> responseMap = new HashMap<>();

        if (!account.canSendConfirmEmail()) {
            responseMap.put("error", "인증 이메일은 1시간에 한번만 전송할 수 있습니다.");
            return ResponseEntity.badRequest().body(responseMap);
        }

        accountService.sendSignUpConfirmEmail(account);
        responseMap.put("message", "success.");
        return ResponseEntity.ok(responseMap);
    }

}