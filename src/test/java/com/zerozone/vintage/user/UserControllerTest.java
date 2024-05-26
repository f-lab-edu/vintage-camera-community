가package com.zerozone.vintage.user;

import com.zerozone.vintage.account.AccountRepository;
import com.zerozone.vintage.account.AccountService;
import com.zerozone.vintage.account.SignUpForm;
import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.domain.CameraTag;
import com.zerozone.vintage.domain.LocationTag;
import com.zerozone.vintage.tag.CameraTagRepository;
import com.zerozone.vintage.tag.LocationTagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    CameraTagRepository cameraTagRepository;
    @Autowired
    LocationTagRepository locationTagRepository;

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
        mockMvc.perform(get("/users/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }


    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정 - 정상 입력")
    @Test
    void updateProfileWithInputSuccess() throws Exception{
        String bio = "{\"bio\": \"개발자 공존입니다.\"}";
        mockMvc.perform(post("/api/users/me/profile")
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
    void updateProfileWithLongBioError() throws Exception{
        //긴 문장
        String longBio = "{\"bio\": \"나랏말싸미 듕귁에 달아문자와로 서르 사맛디 아니할쎄 이런 전차로 어린 백셩이 니르고져 홇베이셔도 마참네 제 뜨들 시러펴디 몯핧 노미하니아 내 이랄 윙하야 어엿비너겨 새로 스믈 여듫 짜랄 맹가노니사람마다 해여 수비니겨 날로 쑤메 뻔한킈 하고져 할따라미니라\"}";
        mockMvc.perform(post("/api/users/me/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(longBio)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.bio").value("length must be between 0 and 35"));

        Optional<Account> accountOptional = accountRepository.findByNickname("zerozone");
        //Optional<Account> accountOptional = accountRepository.findByEmail("00zero0zone00@gmail.com");
        assertTrue(accountOptional.isPresent());
        Account account = accountOptional.get();
        //업데이트 되지 않았어야 한다.
        assertNull(account.getBio());
    }

    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("패스워드 화면 접근")
    @Test
    void updatePassword_view() throws Exception {
        mockMvc.perform(get("/users/password"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("패스워드 변경값 정상")
    @Test
    void updatePasswordWithInputSuccess() throws Exception {
        String passwordJson = "{\"newPassword\":\"test1234!\", \"newPasswordConfirm\":\"test1234!\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwordJson)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());

        Optional<Account> zerozone = accountRepository.findByNickname("zerozone");
        assertTrue(zerozone.isPresent());
        assertTrue(passwordEncoder.matches("test1234!", zerozone.get().getPassword()));
    }

    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("패스워드 변경값 불일치")
    @Test
    void updatePasswordWithInputFail() throws Exception {
        String passwordJson = "{\"newPassword\":\"test1234!\", \"newPasswordConfirm\":\"11111111\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwordJson)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest());
    }

    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("관심카메라 태그 화면 접근")
    @Test
    void cameraTagsView() throws Exception {
        mockMvc.perform(get("/users/cameraTags"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"));
    }

    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("관심카메라 태그 조회 성공")
    @Test
    void getCameraTagsWithSuccess() throws Exception {
        CameraTag cameraTag = cameraTagRepository.save(CameraTag.builder().title("카메라").build());
        Account account = accountRepository.findByNickname("zerozone").get();
        accountService.addCameraTag(account, cameraTag);

        mockMvc.perform(get("/api/users/me/camera-tags")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("관심 카메라 태그 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").value("카메라"));
    }

    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("관심카메라 태그 등록 성공")
    @Test
    void addCameraTagsWithSuccess() throws Exception {
        String tagJson = "{\"title\":\"카메라\"}";

        mockMvc.perform(post("/api/users/me/camera-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tagJson)
                        .with(csrf()))
                .andDo(print()) // 요청과 응답을 출력하여 디버깅에 도움을 준다.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("관심카메라 등록에 성공."));

        Optional<Account> accountOptional = accountRepository.findByNickname("zerozone");
        assertTrue(accountOptional.isPresent());
        Account account = accountOptional.get();
        assertNotNull(account.getCameraTags()); // cameraTags가 null이 아닌지 확인
        assertTrue(account.getCameraTags().stream().anyMatch(tag -> tag.getTitle().equals("카메라")));
    }





    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("관심카메라 태그 삭제 성공")
    @Test
    void removeCameraTagsWithSuccess() throws Exception {
        CameraTag cameraTag = cameraTagRepository.save(CameraTag.builder().title("카메라").build());
        Account account = accountRepository.findByNickname("zerozone").get();
        accountService.addCameraTag(account, cameraTag);

        String tagJson = "{\"title\":\"카메라\"}";
        mockMvc.perform(delete("/api/users/me/camera-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tagJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("관심카메라 삭제에 성공."));

        account = accountRepository.findByNickname("zerozone").get();
        assertFalse(account.getCameraTags().stream().anyMatch(tag -> tag.getTitle().equals("카메라")));
    }

    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("활동지역 태그 화면 접근")
    @Test
    void cameraLocationTagsView() throws Exception {
        mockMvc.perform(get("/users/cameraLocationsTags"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"));
    }

    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("활동지역 태그 조회 성공")
    @Test
    void getCameraLocationTagsWithSuccess() throws Exception {
        LocationTag locationTag = locationTagRepository.save(LocationTag.builder().title("서울시청").build());
        Account account = accountRepository.findByNickname("zerozone").get();
        accountService.addLocationTag(account, locationTag);

        mockMvc.perform(get("/api/users/me/location-tags")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("활동 지역 태그 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").value("서울시청"));
    }


    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("활동지역 태그 등록 성공")
    @Test
    void addCameraLocationTagsWithSuccess() throws Exception {
        String tagJson = "{\"title\":\"서울시청\"}";

        mockMvc.perform(post("/api/users/me/location-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tagJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("활동 지역 태그 등록에 성공."));

        Optional<Account> accountOptional = accountRepository.findByNickname("zerozone");
        assertTrue(accountOptional.isPresent());
        Account account = accountOptional.get();
        assertTrue(account.getLocationTags().stream().anyMatch(tag -> tag.getTitle().equals("서울시청")));
    }

    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("활동지역 태그 삭제 성공")
    @Test
    void removeLocationTagsWithSuccess() throws Exception {
        LocationTag locationTag = locationTagRepository.save(LocationTag.builder().title("서울시청").build());
        Account account = accountRepository.findByNickname("zerozone").get();
        accountService.addLocationTag(account, locationTag);

        String tagJson = "{\"title\":\"서울시청\"}";
        mockMvc.perform(delete("/api/users/me/location-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tagJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("활동 지역 태그 삭제에 성공."));

        account = accountRepository.findByNickname("zerozone").get();
        assertFalse(account.getLocationTags().stream().anyMatch(tag -> tag.getTitle().equals("서울시청")));
    }

}