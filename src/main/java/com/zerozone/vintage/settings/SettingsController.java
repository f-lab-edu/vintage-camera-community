package com.zerozone.vintage.settings;

import com.zerozone.vintage.account.AccountRepository;
import com.zerozone.vintage.account.AccountService;
import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.domain.CameraTag;
import com.zerozone.vintage.dto.CustomResDto;
import com.zerozone.vintage.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final AccountService accountService;

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
    public String showUpdatePasswordForm(@CheckedUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return "/settings/password";
    }

    @GetMapping("/account")
    public String showUpdateAccountForm(@CheckedUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return "/settings/account";
    }

    @GetMapping("/cameraTags")
    public String showUpdateCameraTags(@CheckedUser Account account, Model model) {
        model.addAttribute(account);
        return "/settings/cameraTags";
    }

    @GetMapping("/cameraLocationsTags")
    public String showUpdateCameraLocations(@CheckedUser Account account, Model model) {
        model.addAttribute(account);
        return "/settings/cameraLocations";
    }

}
