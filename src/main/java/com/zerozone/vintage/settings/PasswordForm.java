package com.zerozone.vintage.settings;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PasswordForm {

    @Length(min = 6 , max = 16)
    private String newPassword;

    @Length(min = 6 , max = 16)
    private String newPasswordConfirm;
}
