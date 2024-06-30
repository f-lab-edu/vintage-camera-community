package com.zerozone.vintage.notification;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.account.AccountRepository;
import com.zerozone.vintage.account.AccountService;
import com.zerozone.vintage.account.SignUpForm;
import com.zerozone.vintage.comment.CommentService;
import com.zerozone.vintage.comment.Comment;
import com.zerozone.vintage.board.Board;
import com.zerozone.vintage.board.BoardRepository;
import com.zerozone.vintage.meeting.Meeting;
import com.zerozone.vintage.meeting.MeetingRepository;
import com.zerozone.vintage.meeting.MeetingService;
import java.time.LocalDateTime;
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
class NotificationApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private MeetingService meetingService;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @BeforeEach
    void setup() {
        accountRepository.deleteAll();
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickName("zerozone");
        signUpForm.setEmail("00zero0zone00@gmail.com");
        signUpForm.setPassword("test0000!");
        accountService.newAccountProcess(signUpForm);

        SignUpForm otherUserForm = new SignUpForm();
        otherUserForm.setNickName("otherUser");
        otherUserForm.setEmail("otherUser@google.com");
        otherUserForm.setPassword("password123!!");
        accountService.newAccountProcess(otherUserForm);

        Board board = new Board();
        board.setTitle("테스트 게시글");
        board.setAuthor(accountRepository.findByNickname("zerozone").get());
        boardRepository.save(board);

        Meeting meeting = new Meeting();
        meeting.setTitle("테스트 모임");
        meeting.setOrganizer(accountRepository.findByNickname("zerozone").get());
        meetingRepository.save(meeting);
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void notifyOnBoardComment() throws Exception {

        String commentContent = "일반 게시글의 테스트 댓글입니다.";
        Account account = accountRepository.findByNickname("zerozone").get();
        Board board = boardRepository.findAll().get(0);

        Mockito.when(commentService.createBoardComment(Mockito.anyLong(), Mockito.anyString(), Mockito.any(Account.class)))
                .thenAnswer(invocation -> {
                    Comment comment = createTestBoardComment(invocation.getArgument(0), invocation.getArgument(1), account, board);
                    notificationService.createNotification(board.getAuthor(), account.getNickname() + "님이 게시글에 댓글을 달았습니다.");
                    return comment;
                });

        mockMvc.perform(post("/api/comments/board/" + board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentContent))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글 등록에 성공."));

        Mockito.verify(notificationService).createNotification(Mockito.eq(board.getAuthor()), Mockito.anyString());
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void notifyOnMeetingComment() throws Exception {
        String commentContent = "모임 게시글의 테스트 댓글입니다.";
        Account account = accountRepository.findByNickname("zerozone").get();
        Meeting meeting = meetingRepository.findAll().get(0);

        Mockito.when(commentService.createMeetingComment(Mockito.anyLong(), Mockito.anyString(), Mockito.any(Account.class)))
                .thenAnswer(invocation -> {
                    Comment comment = createTestMeetingComment(invocation.getArgument(0), invocation.getArgument(1), account, meeting);
                    notificationService.createNotification(meeting.getOrganizer(), account.getNickname() + "님이 모임에 댓글을 달았습니다.");
                    return comment;
                });

        mockMvc.perform(post("/api/comments/meeting/" + meeting.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentContent))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글 등록에 성공."));

        Mockito.verify(notificationService).createNotification(Mockito.eq(meeting.getOrganizer()), Mockito.anyString());
    }

    @Test
    @WithUserDetails(value = "otherUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void notifyOnMeetingJoin() throws Exception {
        Account account = accountRepository.findByNickname("otherUser").get();
        Meeting meeting = meetingRepository.findAll().get(0);

        //when에 대한 반환이 없는 void 일때
        Mockito.doNothing().when(meetingService).joinMeeting(Mockito.anyLong(), Mockito.any(Account.class));

        mockMvc.perform(post("/api/meetings/" + meeting.getId() + "/join")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("모임에 참여했습니다."));

        notificationService.createNotification(meeting.getOrganizer(), account.getNickname() + "님이 모임에 참여했습니다.");

        Mockito.verify(meetingService).joinMeeting(Mockito.eq(meeting.getId()), Mockito.eq(account));
        Mockito.verify(notificationService).createNotification(Mockito.eq(meeting.getOrganizer()), Mockito.anyString());
    }


    private Comment createTestBoardComment(Long id, String content, Account account, Board board) {
        return Comment.builder()
                .id(id)
                .content(content)
                .author(account)
                .board(board)
                .createdDateTime(LocalDateTime.now())
                .build();
    }

    private Comment createTestMeetingComment(Long id, String content, Account account, Meeting meeting) {
        return Comment.builder()
                .id(id)
                .content(content)
                .author(account)
                .meeting(meeting)
                .createdDateTime(LocalDateTime.now())
                .build();
    }
}
