package com.zerozone.vintage.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NicknameForm {
    @NotBlank
    @Size(min=3, max=20)
    @Pattern(regexp = "^[a-z0-9]{3,20}$")
    private String nickName;
}
