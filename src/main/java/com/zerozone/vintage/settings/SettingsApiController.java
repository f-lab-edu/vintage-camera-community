package com.zerozone.vintage.settings;

import com.zerozone.vintage.account.AccountService;
import com.zerozone.vintage.account.AuthenticationManager;
import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.account.UserAccount;
import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.dto.CustomResDto;
import com.zerozone.vintage.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@Transactional
public class SettingsApiController {

    private final AccountService accountService;
    private final SecurityContextHolderStrategy securityContextHolderStrategy;
    private final SecurityContextRepository securityContextRepository;
    private final NicknameValidator nicknameValidator;

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(new PasswrodFormValidator());
    }

    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameValidator);
    }


    @PostMapping("/profile")
    @Operation(summary = "프로필 업데이트", description = "사용자의 프로필 정보를 업데이트.")
    @ApiResponse(responseCode = "200", description = "프로필 업데이트 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Profile>> updateProfile(@CheckedUser Account account, @RequestBody @Valid Profile profile, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            throw new CustomException("프로필 업데이트 유효성 검사 실패", bindingResult);
        }

        Profile updatedProfile = accountService.updateProfile(account, profile);
        return ResponseEntity.ok(new CustomResDto<>(1, "프로필 업데이트에 성공했습니다.", updatedProfile));
    }

    @PostMapping("/password")
    @Operation(summary = "패스워드 업데이트", description = "사용자의 패스워드변경을 업데이트.")
    @ApiResponse(responseCode = "200", description = "패스워드 변경 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<String>> updatePassword(@CheckedUser Account account, @RequestBody @Valid PasswordForm passwordForm, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new CustomException("패스워드 변경에 실패", bindingResult);
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        return ResponseEntity.ok(new CustomResDto<>(1, "패스워드 변경에 성공했습니다.", "성공"));
    }

    @PostMapping("/account")
    public ResponseEntity<CustomResDto<String>> updateAccount(@CheckedUser Account account, @RequestBody @Valid NicknameForm nicknameForm, BindingResult bindingResult,
                                                             HttpServletRequest request, HttpServletResponse response) {
        if(bindingResult.hasErrors()){
            throw new CustomException("닉네임 변경에 실패", bindingResult);
        }

        accountService.updateNickname(account, nicknameForm.getNickName());
        AuthenticationManager.login(account, request, response);
        return ResponseEntity.ok(new CustomResDto<>(1, "닉네임 변경에 성공했습니다.", "성공"));
    }


}
