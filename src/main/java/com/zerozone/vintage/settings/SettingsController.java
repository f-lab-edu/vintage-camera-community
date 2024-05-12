package com.zerozone.vintage.settings;

import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {

    @GetMapping("/profile")
    public String showProfileForm(@CheckedUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new Profile(account));
        return "settings/profile";
    }



}
