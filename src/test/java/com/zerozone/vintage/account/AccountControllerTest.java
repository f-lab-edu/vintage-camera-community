package com.zerozone.vintage.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired private MockMvc mockMvc;

    @DisplayName("회원 가입 화면 보이는지 테스트")
    @Test
    void signUpForm() throws Exception{
        mockMvc.perform(get("/signUp"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/signUp"));
    }

}
