package com.zerozone.vintage.account;

import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.exception.ControllerException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.zerozone.vintage.exception.ControllerException.InvalidTokenException;
import com.zerozone.vintage.exception.ControllerException.InvalidEmailException;
import com.zerozone.vintage.exception.ControllerException.EmailSendException;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Transactional
public class AccountApiController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy;
    private final SecurityContextRepository securityContextRepository;
    private final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> signUpFormSubmit(@Valid SignUpForm signUpForm, HttpServletRequest request, HttpServletResponse response){
        Account account = accountService.newAccountProcess(signUpForm);
        login(account, request, response);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "success");
        responseMap.put("accountId", account.getId());
        return ResponseEntity.ok(responseMap);
    }


    @GetMapping("/email-verification")
    public ResponseEntity<Map<String, Object>> checkEmailToken(@RequestParam String token, @RequestParam String email, HttpServletRequest request, HttpServletResponse response) {
        Optional<Account> accountOptional = accountRepository.findByEmail(email);
        Map<String, Object> responseMap = new HashMap<>();

        Account account = accountOptional.orElseThrow(() -> new InvalidEmailException("잘못된 이메일입니다."));

        if (!account.isValidToken(token)) {
            throw new InvalidTokenException("잘못된 토큰입니다.");
        }

        account.completeSignUp();
        login(account, request, response);
        responseMap.put("message", "success");
        responseMap.put("nickName", account.getNickname());
        return ResponseEntity.ok(responseMap);
    }


    @GetMapping("/resend-confirm-email")
    public ResponseEntity<Map<String, Object>> resendConfirmEmail(@RequestParam String email) {
        Optional<Account> accountOptional = accountRepository.findByEmail(email);

        Map<String, Object> responseMap = new HashMap<>();

        Account account = accountOptional.orElseThrow(() -> new InvalidEmailException("잘못된 이메일입니다."));

        if (!account.canSendConfirmEmail()) {
            throw new EmailSendException("인증 이메일은 1시간에 한번만 전송할 수 있습니다.");
        }

        accountService.sendSignUpConfirmEmail(account);
        responseMap.put("message", "success.");
        return ResponseEntity.ok(responseMap);
    }

    private void login(Account account, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(token);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

}