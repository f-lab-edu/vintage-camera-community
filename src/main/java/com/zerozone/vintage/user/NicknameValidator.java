package com.zerozone.vintage.user;

import com.zerozone.vintage.account.AccountRepository;
import com.zerozone.vintage.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NicknameValidator  implements Validator {

    private final AccountRepository accountRepository;
    @Override
    public boolean supports(Class<?> clazz) {
        return NicknameForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm) target;
        Optional<Account> byNickname = accountRepository.findByNickname(nicknameForm.getNickName());
        if (byNickname.isPresent()) {
            errors.rejectValue("nickname", "wrong.value", "입력하신 닉네임을 사용할 수 없습니다.");
        }
    }
}
