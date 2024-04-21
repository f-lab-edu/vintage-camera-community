package com.zerozone.vintage.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpForm {

    @NotNull
    @Size(min=3, max=20)
    @Pattern(regexp = "^[a-z0-9]{3,20}$", message = "아이디는 3 ~20자이며 영어 소문자와 숫자 조합입니다.")
    private String nickName;

    @Email
    @NotNull
    private String email;

    @NotNull
    @Size(min=6, max=16)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9!@#$%^&*()._-]{6,16}$", message = "비밀번호는 6 ~ 16자 이하 영어,숫자,특수문자 조합입니다.")
    private String password;
}
