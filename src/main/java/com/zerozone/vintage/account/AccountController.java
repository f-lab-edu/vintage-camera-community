package com.zerozone.vintage.account;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AccountController {

    @GetMapping("/signUp")
    public String signUpForm(Model model){
        model.addAttribute("signUpForm", new SignUpForm());
        return "account/signUp";
    }

    @PostMapping("/signUp")
    public String signUpFormSubmit(@Valid @ModelAttribute SignUpForm signUpForm, BindingResult result){
        if(result.hasErrors()){
            return "account/signUp";
        }

        //TODO 회원가입 후 이메일 진행
        return "redirect:/";


    }

    @GetMapping("/login")
    public String loginForm(Model model){
        return "account/login";
    }



}
