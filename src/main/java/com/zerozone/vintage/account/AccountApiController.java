package com.zerozone.vintage.account;

import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.dto.CustomResDto;
import com.zerozone.vintage.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    @PostMapping("/account")
    @Operation(summary = "회원 가입", description = "새로운 계정을 등록하고 로그인 처리.")
    @ApiResponse(responseCode = "200", description = "계정 생성 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Map<String, Object>>> signUpFormSubmit(@Valid SignUpForm signUpForm, HttpServletRequest request, HttpServletResponse response){
        Account account = accountService.newAccountProcess(signUpForm);
        login(account, request, response);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("accountId", account.getId());
        return ResponseEntity.ok(new CustomResDto<>(1, "계정 생성에 성공했습니다.", responseMap));
    }


    @PostMapping("/email-verification")
    @Operation(summary = "이메일 인증", description = "제공된 토큰을 사용하여 이메일 인증을 수행.")
    @ApiResponse(responseCode = "200", description = "이메일 인증 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<?>checkEmailToken(@RequestBody EmailForm emailForm, HttpServletRequest request, HttpServletResponse response) {
        Optional<Account> accountOptional = accountRepository.findByEmail(emailForm.getEmail());
        Map<String, Object> responseMap = new HashMap<>();

        Account account = accountOptional.orElseThrow(() -> new CustomException("잘못된 이메일입니다."));

        if (!account.isValidToken(emailForm.getToken())) {
            throw new CustomException("잘못된 토큰입니다.");
        }

        account.completeSignUp();
        login(account, request, response);
        responseMap.put("nickName", account.getNickname());
        return ResponseEntity.ok(new CustomResDto<>(1, "이메일 인증 성공", responseMap));
    }


    @PostMapping("/resend-confirm-email")
    @Operation(summary = "인증 이메일 재전송", description = "인증 이메일에 대한 재요청. 이메일은 1시간에 한 번만 재전송 가능.")
    @ApiResponse(responseCode = "200", description = "인증 이메일 재전송 요청 결과", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<String>> resendConfirmEmail(@RequestBody EmailForm request) {
        Optional<Account> accountOptional = accountRepository.findByEmail(request.getEmail());
        Map<String, Object> responseMap = new HashMap<>();

        Account account = accountOptional.orElseThrow(() -> new CustomException("잘못된 이메일입니다."));

        accountService.sendSignUpConfirmEmail(account, "resend");
        return ResponseEntity.ok(new CustomResDto<>(1, "인증 이메일을 다시 보냈습니다.", "성공"));
    }


    //로그인
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