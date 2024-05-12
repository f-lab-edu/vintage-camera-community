package com.zerozone.vintage.settings;

import com.zerozone.vintage.account.AccountService;
import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.dto.CustomResDto;
import com.zerozone.vintage.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@Transactional
public class SettingsApiController {

    private final AccountService accountService;

    @PostMapping("/profile")
    @Operation(summary = "프로필 업데이트", description = "사용자의 프로필 정보를 업데이트.")
    @ApiResponse(responseCode = "200", description = "프로필 업데이트 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Profile>> updateProfile(@CheckedUser Account account, @RequestBody @Valid Profile profile, BindingResult bindingResult, Model model){
        Map<String, Object> responseMap = new HashMap<>();
        if(bindingResult.hasErrors()){
            throw new CustomException("프로필 업데이트 유효성 검사 실패", bindingResult);
        }

        Profile updatedProfile = accountService.updateProfile(account, profile);
        return ResponseEntity.ok(new CustomResDto<>(1, "프로필 업데이트에 성공했습니다.", updatedProfile));
    }



}
