package com.zerozone.vintage.account;

import com.zerozone.vintage.config.AppProperties;
import com.zerozone.vintage.tag.CameraTag;
import com.zerozone.vintage.tag.LocationTag;
import com.zerozone.vintage.exception.CustomException;
import com.zerozone.vintage.image.ImageStorageService;
import com.zerozone.vintage.mail.EmailMessage;
import com.zerozone.vintage.mail.EmailService;
import com.zerozone.vintage.user.Profile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final ImageStorageService imageStorageService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    public Account newAccountProcess(SignUpForm signUpForm){
        Account newAccount = saveNewAccount(signUpForm);
        //메일 토큰 생성
        newAccount.generateEmailCheckToken();
        //send 메세지 셋팅
        sendSignUpConfirmEmail(newAccount, "login");

        return  newAccount;
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickName())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .build();
        //회원 등록
        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount, String type) {

        if(type.equals("resend")){
            if (!newAccount.canSendConfirmEmail()) {
                throw new CustomException("인증 이메일은 1시간에 한번만 전송할 수 있습니다.");
            }
        }

        Context context = new Context();
        context.setVariable("link", "/api/account/email-verification");
        context.setVariable("host", appProperties.getHost());
        context.setVariable("email", newAccount.getEmail());
        context.setVariable("token", newAccount.getEmailCheckToken());
        context.setVariable("nickname", newAccount.getNickname());
        context.setVariable("linkName", "이메일 인증 확인 부탁드립니다.");
        context.setVariable("message", "빈카모 이메일 인증을 위해서 이메일 확인 버튼을 눌러주세요!");

        String message = templateEngine.process("mail/send-mail", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("빈카모 회원가입 이메일 인증입니다.")
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);

    }

    public Profile updateProfile(Account account, Profile profile) throws IOException {

        // 프로필 이미지 처리
        if (profile.getProfileImageUrl() != null && !profile.getProfileImageUrl().isEmpty()) {
            String imageName = imageStorageService.saveImage(profile.getProfileImageUrl());
            profile.setProfileImageUrl(imageName);
        }

        modelMapper.map(profile, account);
        accountRepository.save(account);
        return profile;
    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void updateNickname(Account account, String nickname) {
        account.setNickname(nickname);
        accountRepository.save(account);
    }

    public void addCameraTag(Account account, CameraTag cameraTag) {

        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> {
            a.getCameraTags().add(cameraTag);
            accountRepository.save(a);
        });
    }

    public Set<CameraTag> getCameraTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getCameraTags();
    }

    public void removeCameraTags(Account account, CameraTag cameraTag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> {
            a.getCameraTags().remove(cameraTag);
        });
    }

    public Set<LocationTag> getLocationTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getLocationTags();
    }

    public void addLocationTag(Account account, LocationTag locationTag) {
        if (account.getLocationTags() == null) {
            account.setLocationTags(new HashSet<>());
        }
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> {
            a.getLocationTags().add(locationTag);
            accountRepository.save(a);
        });
    }

    public void removeLocationTag(Account account, LocationTag locationTag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> {
            a.getLocationTags().remove(locationTag);
        });
    }
}
