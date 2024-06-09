package com.zerozone.vintage.bookmark;

import com.zerozone.vintage.account.AccountRepository;
import com.zerozone.vintage.account.AccountService;
import com.zerozone.vintage.account.SignUpForm;
import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.domain.Board;
import com.zerozone.vintage.domain.Bookmark;
import com.zerozone.vintage.board.BoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookmarkApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookmarkService bookmarkService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    BookmarkRepository bookmarkRepository;

    private Account account;
    private Board board;
    private Bookmark bookmark;

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
        board.setTitle("테스트 게시글입니당");
        board.setAuthor(account);
        board = boardRepository.save(board);

        bookmark = new Bookmark();
        bookmark.setBoard(board);
        bookmark.setAccount(account);
        bookmark = bookmarkRepository.save(bookmark);

        Mockito.when(bookmarkService.addBookmark(Mockito.anyLong(), Mockito.any(Account.class))).thenReturn(bookmark);
        Mockito.doNothing().when(bookmarkService).removeBookmark(Mockito.anyLong(), Mockito.any(Account.class));
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void addBookmark() throws Exception {
        mockMvc.perform(post("/api/bookmarks/board/" + board.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("북마크 등록 성공."))
                .andExpect(jsonPath("$.data.board.id").value(board.getId()));
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void removeBookmark() throws Exception {
        mockMvc.perform(delete("/api/bookmarks/" + bookmark.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("북마크가 삭제되었습니다."));
    }
}