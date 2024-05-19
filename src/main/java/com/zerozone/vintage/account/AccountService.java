package com.zerozone.vintage.account;

import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.domain.CameraTag;
import com.zerozone.vintage.domain.LocationTag;
import com.zerozone.vintage.exception.CustomException;
import com.zerozone.vintage.settings.Profile;
import com.zerozone.vintage.tag.CameraTagRepository;
import com.zerozone.vintage.tag.LocationTagRepository;
import com.zerozone.vintage.utils.ImageUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final CameraTagRepository cameraTagRepository;
    private final LocationTagRepository locationTagRepository;
    private final Path rootLocation = Paths.get("uploaded-profile-images"); // 이미지를 저장할 서버 내 폴더 경로 지정


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

        // 프로필 이미지 처리
        if (profile.getProfileImageUrl() != null && !profile.getProfileImageUrl().isEmpty()) {
           String imageName = ImageUtils.saveImage(profile.getProfileImageUrl());
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
        if (account.getCameraTags() == null) {
            account.setCameraTags(new HashSet<>());
        }
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getCameraTags().add(cameraTag));
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
        byId.ifPresent(a -> a.getLocationTags().add(locationTag));
    }

    public void removeLocationTag(Account account, LocationTag locationTag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> {
            a.getLocationTags().remove(locationTag);
        });
    }
}
