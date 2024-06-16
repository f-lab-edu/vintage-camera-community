package com.zerozone.vintage.meeting;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.account.AccountRepository;
import com.zerozone.vintage.account.AccountService;
import com.zerozone.vintage.account.SignUpForm;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MeetingApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MeetingService meetingService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @BeforeEach
    void setup() {
        // 초기 설정: 기존 계정 데이터 삭제 및 새로운 계정 생성
        accountRepository.deleteAll();
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickName("zerozone");
        signUpForm.setEmail("00zero0zone00@gmail.com");
        signUpForm.setPassword("test0000!");
        accountService.newAccountProcess(signUpForm);

        // 다른 새로운 계정
        SignUpForm otherUserForm = new SignUpForm();
        otherUserForm.setNickName("otherUser");
        otherUserForm.setEmail("otherUser@google.com");
        otherUserForm.setPassword("password123!!");
        accountService.newAccountProcess(otherUserForm);
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createMeeting() throws Exception {
        // 상황 설정: 모임 생성 폼 및 테스트 계정 설정
        MeetingForm meetingForm = createMeetingForm();
        Account account = accountRepository.findByNickname("zerozone").get();
        Meeting meeting = createTestMeeting(account);

        // 모임 생성 서비스의 동작을 모킹
        Mockito.when(meetingService.createMeeting(Mockito.any(Meeting.class), Mockito.any(Account.class))).thenReturn(meeting);

        // 행동: 모임 생성 API 호출
        mockMvc.perform(post("/api/meetings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetingForm))
                        .with(csrf()))
                // 검증: HTTP 상태 코드 및 반환 메시지 확인
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("모임 등록에 성공했습니다."))
                .andExpect(jsonPath("$.data.title").value(meeting.getTitle()));
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateMeeting() throws Exception {
        MeetingForm meetingForm = createMeetingForm();
        Account account = accountRepository.findByNickname("zerozone").get();
        Meeting meeting = createTestMeeting(account);

        Mockito.when(meetingService.updateMeeting(Mockito.anyLong(), Mockito.any(Meeting.class), Mockito.any(Account.class)))
                .thenReturn(meeting);

        mockMvc.perform(put("/api/meetings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetingForm))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("모임 수정에 성공했습니다."))
                .andExpect(jsonPath("$.data.title").value(meeting.getTitle()));
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteMeeting() throws Exception {
        Mockito.doNothing().when(meetingService).deleteMeeting(Mockito.anyLong(), Mockito.any(Account.class));

        mockMvc.perform(delete("/api/meetings/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("모임이 삭제되었습니다."));
    }

    @Test
    @WithUserDetails(value = "otherUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void joinMeeting() throws Exception {
        Mockito.doNothing().when(meetingService).joinMeeting(Mockito.anyLong(), Mockito.any(Account.class));

        mockMvc.perform(post("/api/meetings/1/join")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("모임에 참여했습니다."));
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateMeetingStatus() throws Exception {
        Mockito.doNothing().when(meetingService).updateMeetingStatus(Mockito.anyLong(), Mockito.any(MeetingStatus.class));

        mockMvc.perform(put("/api/meetings/1/status")
                        .param("status", "STARTED")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("모임 상태가 업데이트 되었습니다."));
    }

    private MeetingForm createMeetingForm() {
        MeetingForm meetingForm = new MeetingForm();
        meetingForm.setTitle("테스트 모임 제목123");
        meetingForm.setDescription("테스트 모임 설명 블라 블라 블라");
        meetingForm.setStartDate(LocalDate.now());
        meetingForm.setEndDate(LocalDate.now().plusDays(1));
        meetingForm.setStartTime(LocalTime.of(10, 0));
        meetingForm.setEndTime(LocalTime.of(18, 0));
        meetingForm.setTags(Collections.emptySet());
        meetingForm.setPublic(true);

        return meetingForm;
    }

    private Meeting createTestMeeting(Account account) {
        return Meeting.builder()
                .title("테스트 모임 제목123")
                .description("테스트 모임 설명 블라 블라 블라")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(18, 0))
                .tags(Collections.emptySet())
                .organizer(account)
                .status(MeetingStatus.NOT_STARTED)
                .isPublic(true)
                .build();
    }
}