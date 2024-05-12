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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
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
           String imageName = saveImage(profile.getProfileImageUrl());
           profile.setProfileImageUrl(imageName);
        }

        account.setUrl(profile.getUrl());
        account.setOccupation(profile.getOccupation());
        account.setLocation(profile.getLocation());
        account.setBio(profile.getBio());
        account.setProfileImageUrl(profile.getProfileImageUrl());
        accountRepository.save(account);
        return profile;
    }

    private String saveImage(String imageData) {
        try {
            //이미지 저장전에 폴더 존재 여부 먼저 체크
            ensureDirectoryExists(rootLocation);

            String[] parts = imageData.split(",");
            String mediaType = parts[0].split(";")[0].split(":")[1]; // MIME 타입 추출
            String extension = getExtensionFromMediaType(mediaType); // MIME 타입에 기반한 파일 확장자
            byte[] imageBytes = Base64.getDecoder().decode(parts[1]); // base64 인코딩된 이미지 데이터를 디코딩
            String imageName = UUID.randomUUID().toString() + extension; // 랜덤 UUID와 결정된 확장자를 사용하여 파일 이름 생성

            // 이미지 파일을 저장할 최종 경로를 설정
            Path destinationFile = rootLocation.resolve(Paths.get(imageName))
                    .normalize().toAbsolutePath(); // 절대 경로

            // 생성된 경로에 이미지 데이터를 파일로 쓰기
            Files.write(destinationFile, imageBytes);

            return imageName; // 저장된 파일의 이름을 반환
        } catch (IOException e) {
            throw new RuntimeException("이미지 폴더 저장에 실패 했습니다. ", e);
        }
    }

    private String getExtensionFromMediaType(String mediaType) {
        switch (mediaType) {
            case "image/jpeg":
                return ".jpeg";
            case "image/jpg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            default:
                throw new IllegalArgumentException("허용하지 않는 타입입니다. : " + mediaType);
        }
    }

    private void ensureDirectoryExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }
}
