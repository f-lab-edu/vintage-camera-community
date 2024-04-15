package com.zerozone.vintage.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired private MockMvc mockMvc;

    @DisplayName("회원 가입 화면 보이는지 테스트")
    @Test
    void signUpForm() throws Exception{
        mockMvc.perform(get("/signUp"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/signUp"))
                .andExpect(model().attributeExists("signUpForm"));
    }

    @DisplayName("회원 가입 유효성 검사 성공")
    @Test
    public void testSignUpWithValidData() throws Exception {
        mockMvc.perform(post("/signUp")
                        .with(csrf())  // CSRF 토큰을 추가
                        .param("email", "00zero0zone00@gmail.com")  // 유효한 이메일 주소
                        .param("password", "passwrod12")  // 유효한 비밀번호
                        .param("nickname", "hahihuheho"))  // 유효한 닉네임
                .andExpect(status().isOk());  // 성공 상태
    }

}
