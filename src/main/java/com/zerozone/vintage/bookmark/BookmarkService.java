package com.zerozone.vintage.bookmark;

import com.zerozone.vintage.domain.Bookmark;
import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.domain.Board;
import com.zerozone.vintage.exception.CustomException;
import com.zerozone.vintage.board.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final BoardRepository boardRepository;

    public Bookmark addBookmark(Long boardId, Account account) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException("게시글을 찾을 수 없습니다."));

        Bookmark bookmark = Bookmark.builder()
                .board(board)
                .account(account)
                .build();
        return bookmarkRepository.save(bookmark);
    }

    public void removeBookmark(Long bookmarkId, Account account) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new CustomException("북마크를 찾을 수 없습니다."));

        if (!bookmark.getAccount().equals(account)) {
            throw new CustomException("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        bookmarkRepository.delete(bookmark);
    }
}
