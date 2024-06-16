package com.zerozone.vintage.user;

import com.zerozone.vintage.account.AccountRepository;
import com.zerozone.vintage.account.AccountService;
import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.account.Account;
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
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final AccountService accountService;

    @GetMapping("/personalProfile/{nickname}")
    public String viewPersonalProfile(@PathVariable String nickname, Model model, @CheckedUser Account account){
        Optional<Account> byNickname = accountRepository.findByNickname(nickname);
        Account isAccount = byNickname.orElseThrow(() -> new CustomException(nickname + "에 해당하는 사용자가 없습니다"));

        model.addAttribute(isAccount);
        model.addAttribute("isOwner", isAccount.equals(account));
        return "account/personalProfile";
    }

    @GetMapping("/profile")
    public String showProfileForm(@CheckedUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Profile.class));
        return "user/profile";
    }

    @GetMapping("/password")
    public String showUpdatePasswordForm(@CheckedUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return "/user/password";
    }

    @GetMapping("/account")
    public String showUpdateAccountForm(@CheckedUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return "/user/account";
    }

    @GetMapping("/cameraTags")
    public String showUpdateCameraTags(@CheckedUser Account account, Model model) {
        model.addAttribute(account);
        return "/user/cameraTags";
    }

    @GetMapping("/cameraLocationsTags")
    public String showUpdateCameraLocations(@CheckedUser Account account, Model model) {
        model.addAttribute(account);
        return "/user/cameraLocations";
    }

}
