package com.zerozone.vintage.account;

import com.zerozone.vintage.domain.Account;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AccountControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountRepository accountRepository;
    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("회원 가입 화면 보이는지 테스트")
    @Test
    void signUpForm() throws Exception{
        mockMvc.perform(get("/signUp"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/signUp"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원 가입 유효성 검사 성공")
    @Test
    public void testSignUpWithValidData() throws Exception {
        mockMvc.perform(post("/api/account/signUp")
                        .with(csrf())  // CSRF 토큰을 추가
                        .param("email", "00zero0zone00@gmail.com")  // 유효한 이메일 주소
                        .param("password", "passwrod12!")  // 유효한 비밀번호
                        .param("nickName", "hahihuheho"))  // 유효한 닉네임
                .andExpect(status().isOk())  // 성공 상태
                .andExpect(authenticated().withUsername("hahihuheho"));

        Account account = accountRepository.findByEmail("00zero0zone00@gmail.com");
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "passwrod12!");
        assertNotNull(account.getEmailCheckToken());
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

    @DisplayName("회원 가입 입력값 오류")
    @Test
    void signUpSubmitWrongInput() throws Exception{
        mockMvc.perform(post("/api/account/signUp")
                        .param("nickName","zerozone**") //특수문자가 들어간 유효하지 닉네임
                        .param("email","00zero0zone00@gmail.com")
                        .param("password","passwrod")   // 특수문주랑 숫자가 빠진 유효하지 않는 패스워드
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(unauthenticated());


    }

    @DisplayName("인증 메일 입력값 오류")
    @Test
    void checkEmailTokenWrongInput() throws Exception{
        mockMvc.perform(get("/checkEmailToken")
                        .param("token", "asdxcqwqrdvxvsdv"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checkedEmail"))
                .andExpect(unauthenticated());  //이메일 인증 되지 않음
    }

    @DisplayName("인증 메일 입력값 정상")
    @Test
    void checkEmailToken() throws Exception{
        Account account = Account.builder()
                .email("happy@gmail.com")
                .password("testpassword00!")
                .nickname("zerozone")
                .build();
        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/checkEmailToken")
                        .param("token", newAccount.getEmailCheckToken())
                        .param("email", newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickName"))
                .andExpect(view().name("account/checkedEmail"))
                .andExpect(authenticated().withUsername("zerozone"));

    }



}
