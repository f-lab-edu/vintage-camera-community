package com.zerozone.vintage.comment;

import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.dto.CustomResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;

    @PostMapping("/board/{boardId}")
    public ResponseEntity<CustomResDto<Comment>> createComment(@CheckedUser Account account, @PathVariable Long boardId, @RequestBody String content) {
        Comment newComment = commentService.createComment(boardId, content, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "댓글 등록에 성공.", newComment));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CustomResDto<Comment>> updateComment(@CheckedUser Account account, @PathVariable Long commentId, @RequestBody String content) {
        Comment updatedComment = commentService.updateComment(commentId, content, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "댓글 수정에 성공.", updatedComment));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<CustomResDto<Void>> deleteComment(@CheckedUser Account account, @PathVariable Long commentId) {
        commentService.deleteComment(commentId, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "댓글이 삭제되었습니다.", null));
    }
}