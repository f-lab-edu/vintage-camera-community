package com.zerozone.vintage.bookmark;

import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.dto.CustomResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards/{boardId}/bookmarks")
@RequiredArgsConstructor
public class BookmarkApiController {

    private final BookmarkService bookmarkService;

    @PostMapping
    @Operation(summary = "북마크 등록", description = "게시글 관련 북마크 등록")
    @ApiResponse(responseCode = "200", description = "북마크 등록 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Bookmark>> addBookmarkToBoard(@CheckedUser Account account, @PathVariable Long boardId) {
        Bookmark newBookmark = bookmarkService.addBookmarkToBoard(boardId, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "북마크 등록 성공.", newBookmark));
    }

    @PostMapping("/meeting/{meetingId}")
    @Operation(summary = "모임 북마크 등록", description = "모임 관련 북마크 등록")
    @ApiResponse(responseCode = "200", description = "북마크 등록 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Bookmark>> addBookmarkToMeeting(@CheckedUser Account account, @PathVariable Long meetingId) {
        Bookmark newBookmark = bookmarkService.addBookmarkToMeeting(meetingId, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "북마크 등록 성공.", newBookmark));
    }

    @DeleteMapping("/{bookmarkId}")
    @Operation(summary = "북마크 제거", description = "게시글 관련 북마크 제거")
    @ApiResponse(responseCode = "200", description = "북마크 제거 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Void>> removeBookmark(@CheckedUser Account account, @PathVariable Long bookmarkId) {
        bookmarkService.removeBookmark(bookmarkId, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "북마크가 삭제되었습니다.", null));
    }
}