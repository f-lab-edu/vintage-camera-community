package com.zerozone.vintage.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpForm signUpForm = (SignUpForm) target;
        if(accountRepository.existsByEmail(signUpForm.getEmail())){
            errors.rejectValue("email", "invaild.email", new Object[]{signUpForm.getEmail()}, "이미 사용 중인 이메일 입니다.");
        }

        if(accountRepository.existsByNickname(signUpForm.getNickName())){
            errors.rejectValue("nickName", "invaild.nickName", new Object[]{signUpForm.getNickName()}, "이미 사용 중인 닉네임 입니다.");
        }
    }
}
