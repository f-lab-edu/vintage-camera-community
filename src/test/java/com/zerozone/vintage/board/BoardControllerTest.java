package com.zerozone.vintage.board;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerozone.vintage.account.AccountRepository;
import com.zerozone.vintage.account.AccountService;
import com.zerozone.vintage.account.SignUpForm;
import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.domain.Board;
import java.util.List;
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
public class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BoardService boardService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;

    private Board board;
    private Account account;


    @BeforeEach
    void beforeEach() {
        accountRepository.deleteAll();

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickName("zerozone");
        signUpForm.setEmail("00zero0zone00@gmail.com");
        signUpForm.setPassword("test0000!");
        accountService.newAccountProcess(signUpForm);

        account = accountRepository.findByNickname("zerozone").get();

        board = new Board();
        board.setId(1L);
        board.setBoardCategory(BoardCategory.valueOf("GENERAL"));
        board.setTitle("테스트 제목");
        board.setFullDescription("테스트 본문");

        Mockito.when(boardService.createNewPost(any(Board.class), any(Account.class))).thenReturn(board);
        Mockito.when(boardService.updatePost(any(Long.class), any(BoardForm.class), any(Account.class))).thenReturn(board);
        Mockito.when(boardService.searchPosts(Mockito.any(), Mockito.anyString(), Mockito.anyString())).thenReturn(List.of(board));
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void newPostSubmit() throws Exception {
        BoardForm boardForm = new BoardForm();
        boardForm.setTitle("테스트 제목 입니당");
        boardForm.setCategory(BoardCategory.valueOf("GENERAL"));
        boardForm.setFullDescription("게시글 입니다다다다");

        mockMvc.perform(post("/api/board/general")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(boardForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 등록에 성공."))
                .andExpect(jsonPath("$.data.id").value(board.getId()));
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void editPostSubmit() throws Exception {
        BoardForm boardForm = new BoardForm();
        boardForm.setTitle("Updated Title");
        boardForm.setCategory(BoardCategory.valueOf("GENERAL"));
        boardForm.setFullDescription("게시글 본문을 수정하겠습니다. 123123123 ");

        mockMvc.perform(put("/api/board/general/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(boardForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 수정에 성공."))
                .andExpect(jsonPath("$.data.id").value(board.getId()));
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deletePost() throws Exception {
        mockMvc.perform(delete("/api/board/general/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글이 삭제되었습니다."));
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void searchPosts() throws Exception {
        mockMvc.perform(get("/api/board/general/search")
                        .param("category", "GENERAL")
                        .param("keyword", "우주")
                        .param("searchType", "title")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("검색 결과입니다."))
                .andExpect(jsonPath("$.data[0].title").value(board.getTitle()));
    }

}