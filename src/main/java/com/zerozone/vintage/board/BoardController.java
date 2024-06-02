package com.zerozone.vintage.board;

import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.domain.Board;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/new-post")
    public String newPostForm(@CheckedUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new BoardForm());
        return "board/create-board";
    }

    @GetMapping("/post/{id}")
    public String viewPost(@CheckedUser Account account, @PathVariable Long id, Model model) {
        Board board = boardService.findById(id);
        model.addAttribute("account", account);
        model.addAttribute("board", board);
        return "board/view-board";
    }

    @GetMapping("/edit-post/{id}")
    public String editPostForm(@CheckedUser Account account, @PathVariable Long id, Model model) {
        Board board = boardService.findById(id);
        model.addAttribute(account);
        model.addAttribute("board", board);
        return "board/edit-board";
    }

    @GetMapping("/board/list")
    public String listAllBoards(Model model) {
        List<Board> boards = boardService.findAllBoards();
        model.addAttribute("boards", boards);
        return "board/list-boards";
    }


}
