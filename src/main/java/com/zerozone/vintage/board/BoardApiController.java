package com.zerozone.vintage.board;

import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.domain.Board;
import com.zerozone.vintage.dto.CustomResDto;
import com.zerozone.vintage.exception.CustomException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/board/general")
@RequiredArgsConstructor
@Transactional
public class BoardApiController {


    private final BoardService boardService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<CustomResDto<Board>> createPost(@CheckedUser Account account, @RequestBody @Valid BoardForm boardForm, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new CustomException("게시글 등록 유효성 검사 실패", bindingResult);
        }
        Board newPost = boardService.createNewPost(modelMapper.map(boardForm, Board.class), account);
        return ResponseEntity.ok(new CustomResDto<>(1, "게시글 등록에 성공.", newPost));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResDto<Board>> updatePost(@CheckedUser Account account, @PathVariable Long id, @RequestBody @Valid BoardForm boardForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new CustomException("게시글 수정 유효성 검사 실패", bindingResult);
        }
        Board updatedPost = boardService.updatePost(id, boardForm, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "게시글 수정에 성공.", updatedPost));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResDto<Board>> deletePost(@CheckedUser Account account, @PathVariable Long id) {
        boardService.deletePost(id, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "게시글이 삭제되었습니다.", null));
    }


}
