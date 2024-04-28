package com.zerozone.vintage.account;

import com.zerozone.vintage.domain.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @GetMapping("/signUp")
    public String signUpForm(Model model){
        model.addAttribute("signUpForm", new SignUpForm());
        return "account/signUp";
    }

    @GetMapping("/checkedEmail")
    public String checkedEmail(Account account, Model model) {
        model.addAttribute("email", account.getEmail());
        return "account/checkedEmail";
    }

    /*

    @GetMapping("/checkEmailToken")
    public String checkEmailToken(String token, String email, Model model , HttpServletRequest request, HttpServletResponse response){
        Account account = accountRepository.findByEmail(email);
        String view = "account/checkedEmail";
        if(account == null){
            model.addAttribute("error", "wrong.email");
            return view;
        }

        if(!account.isValidToken(token)){
            model.addAttribute("error", "wrong.token");
            return view;
        }

        account.completeSignUp();
        accountService.login(account, request, response);
        model.addAttribute("nickName", account.getNickname());
        return view;
    }
*/

    @GetMapping
    public String home(@CheckedUser Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }
        return "index";
    }

    @GetMapping("/checkEmail")
    public String checkEmail(@CheckedUser Account account, Model model) {
        model.addAttribute("email", account.getEmail());
        return "account/checkEmail";
    }


    /*
    @GetMapping("/resendConfirmEmail")
    public String resendConfirmEmail(@CheckedUser Account account, Model model){
        if(!account.canSendConfirmEmail()){ //전송 가능 체크
            model.addAttribute("error", "인증 이메일은 1시간에 한번만 전송할 수 있습니다.");
            model.addAttribute("email", account.getEmail());
            return "account/checkEmail";
        }

        accountService.sendSignUpConfirmEmail(account);
        return "redirect:/";
    }
*/

    @GetMapping("/login")
    public String loginForm(Model model){
        return "account/login";
    }


}
