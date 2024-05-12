package com.zerozone.vintage.settings;

import com.zerozone.vintage.account.AccountRepository;
import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    @GetMapping("/personalProfile/{nickname}")
    public String viewPersonalProfile(@PathVariable String nickname, Model model, @CheckedUser Account account){
        Optional<Account> byNickname = accountRepository.findByNickname(nickname);
        Account isAccount = byNickname.orElseThrow(() -> new CustomException( nickname + "에 해당하는 사용자가 없습니다"));

        model.addAttribute(isAccount);
        model.addAttribute("isOwner", isAccount.equals(account));
        return "account/personalProfile";
    }

    @GetMapping("/profile")
    public String showProfileForm(@CheckedUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account , Profile.class));
        return "settings/profile";
    }

    @GetMapping("/password")
    public String passwordUpdateForm(@CheckedUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return "/settings/password";
    }

    @GetMapping("/account")
    public String updateAccountForm(@CheckedUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return "/settings/account";
    }

}
