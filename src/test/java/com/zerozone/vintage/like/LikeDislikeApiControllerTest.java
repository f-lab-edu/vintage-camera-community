package com.zerozone.vintage.like;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zerozone.vintage.account.AccountRepository;
import com.zerozone.vintage.account.AccountService;
import com.zerozone.vintage.account.SignUpForm;
import com.zerozone.vintage.board.BoardRepository;
import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.domain.Board;
import com.zerozone.vintage.domain.LikeDislike;
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
public class LikeDislikeApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeDislikeService likeDislikeService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    BoardRepository boardRepository;

    private Account account;
    private Board board;
    private LikeDislike likeDislike;

    @BeforeEach
    void setup() {
        accountRepository.deleteAll();
        boardRepository.deleteAll();

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickName("zerozone");
        signUpForm.setEmail("00zero0zone00@gmail.com");
        signUpForm.setPassword("test0000!");
        accountService.newAccountProcess(signUpForm);

        account = accountRepository.findByNickname("zerozone").get();
        board = new Board();
        board.setTitle("테스트 게시글 제목");
        board.setAuthor(account);
        board = boardRepository.save(board);

        likeDislike = new LikeDislike();
        likeDislike.setBoard(board);
        likeDislike.setAccount(account);
        likeDislike.setLike(true);

        Mockito.when(likeDislikeService.likePost(Mockito.anyLong(), Mockito.any(Account.class), Mockito.anyBoolean())).thenReturn(likeDislike);
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void likePost() throws Exception {
        mockMvc.perform(post("/api/likes/board/" + board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("true")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("좋아요 등록 성공."))
                .andExpect(jsonPath("$.data.board.id").value(board.getId()));
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void dislikePost() throws Exception {
        mockMvc.perform(post("/api/likes/board/" + board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("false")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("싫어요 등록 성공"))
                .andExpect(jsonPath("$.data.board.id").value(board.getId()));
    }
}