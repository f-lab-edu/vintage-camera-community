package com.zerozone.vintage.settings;

import com.zerozone.vintage.account.AccountRepository;
import com.zerozone.vintage.account.AccountService;
import com.zerozone.vintage.account.SignUpForm;
import com.zerozone.vintage.domain.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;

    @BeforeEach
    void beforeEach() {
        accountRepository.deleteAll();
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickName("zerozone");
        signUpForm.setEmail("00zero0zone00@gmail.com");
        signUpForm.setPassword("test0000!");
        accountService.newAccountProcess(signUpForm);
    }

    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 화면 접근")
    @Test
    void updateProfileForm() throws Exception{
        String bio = "누구게유";
        mockMvc.perform(get("/settings/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }


    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 정상 입력")
    @Test
    void updateProfileSuccess() throws Exception{
        String bio = "{\"bio\": \"개발자 공존입니다.\"}";
        mockMvc.perform(post("/api/settings/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bio)
                        .with(csrf()))
               .andExpect(status().isOk());

        Optional<Account> accountOptional = accountRepository.findByNickname("zerozone");
        assertTrue(accountOptional.isPresent());
        Account account = accountOptional.get();
        assertEquals("개발자 공존입니다.", account.getBio());

    }

    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 비정상 입력 오류")
    @Test
    void updateProfileError() throws Exception{
        //긴 문장
        String longBio = "{\"bio\": \"나랏말싸미 듕귁에 달아문자와로 서르 사맛디 아니할쎄 이런 전차로 어린 백셩이 니르고져 홇베이셔도 마참네 제 뜨들 시러펴디 몯핧 노미하니아 내 이랄 윙하야 어엿비너겨 새로 스믈 여듫 짜랄 맹가노니사람마다 해여 수비니겨 날로 쑤메 뻔한킈 하고져 할따라미니라\"}";
        mockMvc.perform(post("/api/settings/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(longBio)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.bio").value("length must be between 0 and 35"));

        Optional<Account> accountOptional = accountRepository.findByEmail("00zero0zone00@gmail.com");
        assertTrue(accountOptional.isPresent());
        Account account = accountOptional.get();
        //업데이트 되지 않았어야 한다.
        assertNull(account.getBio());

    }



}