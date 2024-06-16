package com.zerozone.vintage.board;

import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.dto.CustomResDto;
import com.zerozone.vintage.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/board/general")
@RequiredArgsConstructor
@Transactional
public class BoardApiController {


    private final BoardService boardService;
    private final ModelMapper modelMapper;

    @PostMapping
    @Operation(summary = "게시글 등록", description = "일반 게시글 등록")
    @ApiResponse(responseCode = "200", description = "게시글 등록 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Board>> createPost(@CheckedUser Account account, @RequestBody @Valid BoardForm boardForm, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new CustomException("게시글 등록 유효성 검사 실패", bindingResult);
        }
        Board newPost = boardService.createNewPost(modelMapper.map(boardForm, Board.class), account);
        return ResponseEntity.ok(new CustomResDto<>(1, "게시글 등록에 성공.", newPost));
    }

    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정", description = "일반 게시글 수정")
    @ApiResponse(responseCode = "200", description = "게시글 수정 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Board>> updatePost(@CheckedUser Account account, @PathVariable Long id, @RequestBody @Valid BoardForm boardForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new CustomException("게시글 수정 유효성 검사 실패", bindingResult);
        }
        Board updatedPost = boardService.updatePost(id, boardForm, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "게시글 수정에 성공.", updatedPost));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제", description = "일반 게시글 삭제")
    @ApiResponse(responseCode = "200", description = "게시글 삭제 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Board>> deletePost(@CheckedUser Account account, @PathVariable Long id) {
        boardService.deletePost(id, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "게시글이 삭제되었습니다.", null));
    }


    @GetMapping("/search")
    @Operation(summary = "게시글 검색", description = "일반 게시글 조건 검색")
    @ApiResponse(responseCode = "200", description = "게시글 검색 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<List<Board>>> searchPosts(@RequestParam(required = false) BoardCategory category, @RequestParam String keyword, @RequestParam String searchType) {
        List<Board> results = boardService.searchPosts(category, keyword, searchType);
        return ResponseEntity.ok(new CustomResDto<>(1, "검색 결과입니다.", results));
    }

}
