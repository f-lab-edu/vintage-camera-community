package com.zerozone.vintage.account;

import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.exception.CustomException;
import com.zerozone.vintage.settings.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;


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

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("빈카모 회원 가입 성공 축하 드립니다.");
        mailMessage.setText("/api/account/email-verification?token=" + newAccount.getEmailCheckToken() + "&email=" + newAccount.getEmail());

        javaMailSender.send(mailMessage);
    }

    public Profile updateProfile(Account account, Profile profile) {
        account.setUrl(profile.getUrl());
        account.setOccupation(profile.getOccupation());
        account.setLocation(profile.getLocation());
        account.setBio(profile.getBio());
        account.setProfileImageUrl(profile.getProfileImageUrl());
        accountRepository.save(account);
        return profile;
    }

}
