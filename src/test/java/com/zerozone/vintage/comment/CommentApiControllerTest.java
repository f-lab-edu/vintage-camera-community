package com.zerozone.vintage.comment;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zerozone.vintage.account.AccountRepository;
import com.zerozone.vintage.account.AccountService;
import com.zerozone.vintage.account.SignUpForm;
import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.domain.Comment;
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
public class CommentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommentService commentService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;

    private Comment comment;
    private Account account;

    @BeforeEach
    void setup() {
        accountRepository.deleteAll();
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickName("zerozone");
        signUpForm.setEmail("00zero0zone00@gmail.com");
        signUpForm.setPassword("test0000!");
        accountService.newAccountProcess(signUpForm);

        comment = new Comment();
        comment.setId(1L);
        comment.setContent("댓글 테스트입니다. 따봉");

        account = new Account();
        account.setId(1L);
        account.setNickname("테스트유저");

        Mockito.when(commentService.createComment(any(Long.class), any(String.class), any(Account.class))).thenReturn(comment);
        Mockito.when(commentService.updateComment(any(Long.class), any(String.class), any(Account.class))).thenReturn(comment);
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createComment() throws Exception {
        String commentContent = "{\"content\": \"Test Comment\"}";

        mockMvc.perform(post("/api/comments/board/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글 등록에 성공."))
                .andExpect(jsonPath("$.data.id").value(comment.getId()));
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateComment() throws Exception {
        String updatedCommentContent = "{\"content\": \"Updated Comment\"}";

        mockMvc.perform(put("/api/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedCommentContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글 수정에 성공."))
                .andExpect(jsonPath("$.data.id").value(comment.getId()));
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteComment() throws Exception {
        mockMvc.perform(delete("/api/comments/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글이 삭제되었습니다."));
    }
}