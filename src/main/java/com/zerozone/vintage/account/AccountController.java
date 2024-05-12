package com.zerozone.vintage.account;

import com.zerozone.vintage.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @GetMapping("/account")
    public String signUpForm(Model model){
        model.addAttribute("signUpForm", new SignUpForm());
        return "account/account";
    }


    @GetMapping("/checked-email")
    public String emailVerified(Account account, Model model) {
        model.addAttribute("email", account.getEmail());
        return "account/checkedEmail";
    }

    @GetMapping
    public String home(@CheckedUser Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }
        return "index";
    }

    @GetMapping("/check-email")
    public String promptEmailVerification(@CheckedUser Account account, Model model) {
        model.addAttribute("email", account.getEmail());
        return "account/checkEmail";
    }


    @GetMapping("/login")
    public String loginForm(Model model){
        return "account/login";
    }


}
