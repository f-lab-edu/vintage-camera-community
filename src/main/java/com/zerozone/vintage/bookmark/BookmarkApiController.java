package com.zerozone.vintage.bookmark;

import com.zerozone.vintage.domain.Bookmark;
import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.dto.CustomResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkApiController {

    private final BookmarkService bookmarkService;

    @PostMapping("/board/{boardId}")
    public ResponseEntity<CustomResDto<Bookmark>> addBookmark(@CheckedUser Account account, @PathVariable Long boardId) {
        Bookmark newBookmark = bookmarkService.addBookmark(boardId, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "북마크 등록 성공.", newBookmark));
    }

    @DeleteMapping("/{bookmarkId}")
    public ResponseEntity<CustomResDto<Void>> removeBookmark(@CheckedUser Account account, @PathVariable Long bookmarkId) {
        bookmarkService.removeBookmark(bookmarkId, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "북마크가 삭제되었습니다.", null));
    }
}