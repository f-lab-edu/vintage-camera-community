package com.zerozone.vintage.comment;

import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.dto.CustomResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "댓글 등록", description = "게시글 관련 댓글 등록")
    @ApiResponse(responseCode = "200", description = "게시글 댓글 등록 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Comment>> createBoardComment(@CheckedUser Account account, @PathVariable Long boardId, @RequestBody String content) {
        Comment newComment = commentService.createBoardComment(boardId, content, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "댓글 등록에 성공.", newComment));
    }

    @PostMapping("/meeting/{meetingId}")
    @Operation(summary = "모임 댓글 등록", description = "모임 관련 댓글 등록")
    @ApiResponse(responseCode = "200", description = "모임 댓글 등록 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Comment>> createMeetingComment(@CheckedUser Account account, @PathVariable Long meetingId, @RequestBody String content) {
        Comment newComment = commentService.createMeetingComment(meetingId, content, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "댓글 등록에 성공.", newComment));
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "게시글 관련 댓글 수정")
    @ApiResponse(responseCode = "200", description = "게시글 댓글 수정 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Comment>> updateComment(@CheckedUser Account account, @PathVariable Long commentId, @RequestBody String content) {
        Comment updatedComment = commentService.updateComment(commentId, content, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "댓글 수정에 성공.", updatedComment));
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "게시글 관련 댓글 삭제")
    @ApiResponse(responseCode = "200", description = "게시글 댓글 삭제 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Void>> deleteComment(@CheckedUser Account account, @PathVariable Long commentId) {
        commentService.deleteComment(commentId, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "댓글이 삭제되었습니다.", null));
    }
}