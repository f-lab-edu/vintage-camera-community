package com.zerozone.vintage.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified;//인증된 이메일인지

    private String emailCheckToken; //이메일 검증을 위한 토큰

    private LocalDateTime joinedAt; //가입날짜

    private String favoriteCamera; //대표 최애 카메라

    private String bio;

    private String occupation; //직업

    private String location; //살고있는 지역

    private String url;

    private LocalDateTime emailCheckTokenGeneratedAt; //1시간 체크용

    private String profileImageUrl;


    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }
}



