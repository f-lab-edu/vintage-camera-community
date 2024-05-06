package com.zerozone.vintage.settings;

import com.zerozone.vintage.account.AccountService;
import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.exception.ValidationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<Map<String, Object>> updateProfile(@CheckedUser Account account, @Valid Profile profile, BindingResult bindingResult, Model model){
        Map<String, Object> responseMap = new HashMap<>();
        if(bindingResult.hasErrors()){
            //TODO 요청경로나 메소드명 파라매터 추가로 넘겨서 어디서 에러났는지 문구 지정.
            throw new ValidationException(bindingResult);
        }

        accountService.updateProfile(account, profile);
        responseMap.put("message", "success");
        return ResponseEntity.ok(responseMap);
    }



}
