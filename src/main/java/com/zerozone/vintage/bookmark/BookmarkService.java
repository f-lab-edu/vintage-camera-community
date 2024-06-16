package com.zerozone.vintage.bookmark;

import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.board.Board;
import com.zerozone.vintage.exception.CustomException;
import com.zerozone.vintage.board.BoardRepository;
import com.zerozone.vintage.meeting.Meeting;
import com.zerozone.vintage.meeting.MeetingRepository;
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
    private final MeetingRepository meetingRepository;

    public Bookmark addBookmarkToBoard(Long boardId, Account account) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException("게시글을 찾을 수 없습니다."));

        Bookmark bookmark = Bookmark.builder()
                .board(board)
                .account(account)
                .build();
        return bookmarkRepository.save(bookmark);
    }


    public Bookmark addBookmarkToMeeting(Long meetingId, Account account) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new CustomException("모임을 찾을 수 없습니다."));

        Bookmark bookmark = Bookmark.builder()
                .meeting(meeting)
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
