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
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

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

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @PostMapping("/account")
    @Operation(summary = "회원 가입", description = "새로운 계정을 등록하고 로그인 처리.")
    @ApiResponse(responseCode = "200", description = "계정 생성 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Map<String, Object>>> signUpFormSubmit(@Valid SignUpForm signUpForm, HttpServletRequest request, HttpServletResponse response){
        Account account = accountService.newAccountProcess(signUpForm);
        AuthenticationManager.login(account, request, response);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("accountId", account.getId());
        return ResponseEntity.ok(new CustomResDto<>(1, "계정 생성에 성공했습니다.", responseMap));
    }


    @PostMapping("/email-verification")
    @Operation(summary = "이메일 인증", description = "제공된 토큰을 사용하여 이메일 인증을 수행.")
    @ApiResponse(responseCode = "200", description = "이메일 인증 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<?> checkEmailToken(@RequestParam String email, @RequestParam String token, HttpServletRequest request, HttpServletResponse response) {
        Optional<Account> accountOptional = accountRepository.findByEmail(email);

        Account account = accountOptional.orElseThrow(() -> new CustomException("잘못된 이메일입니다."));

        if (!account.isValidToken(token)) {
            throw new CustomException("잘못된 토큰입니다.");
        }

        account.completeSignUp();
        AuthenticationManager.login(account, request, response);
        //구글 이메일에서 인증 버튼 후 인증 완료 화면으로 다이렉트 이동
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/email-verification-success")).build();
    }


    @PostMapping("/resend-confirm-email")
    @Operation(summary = "인증 이메일 재전송", description = "인증 이메일에 대한 재요청. 이메일은 1시간에 한 번만 재전송 가능.")
    @ApiResponse(responseCode = "200", description = "인증 이메일 재전송 요청 결과", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<String>> resendConfirmEmail(@RequestBody EmailForm request) {
        Optional<Account> accountOptional = accountRepository.findByEmail(request.getEmail());

        Account account = accountOptional.orElseThrow(() -> new CustomException("잘못된 이메일입니다."));

        accountService.sendSignUpConfirmEmail(account, "resend");
        return ResponseEntity.ok(new CustomResDto<>(1, "인증 이메일을 다시 보냈습니다.", "성공"));
    }


}