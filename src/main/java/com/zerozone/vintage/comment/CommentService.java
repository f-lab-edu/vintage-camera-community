package com.zerozone.vintage.comment;

import com.zerozone.vintage.board.BoardRepository;
import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.board.Board;
import com.zerozone.vintage.exception.CustomException;
import com.zerozone.vintage.meeting.Meeting;
import com.zerozone.vintage.meeting.MeetingRepository;
import com.zerozone.vintage.notification.NotificationService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MeetingRepository meetingRepository;
    private final NotificationService notificationService;

    public Comment createBoardComment(Long boardId, String content, Account account) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException("게시글을 찾을 수 없습니다."));
        Comment comment = saveComment(board, null, content, account);

        notificationService.createNotification(board.getAuthor(), account.getNickname() + "님이 게시글에 댓글을 달았습니다.");

        return comment;
    }

    public Comment createMeetingComment(Long meetingId, String content, Account account) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new CustomException("모임을 찾을 수 없습니다."));
        Comment comment = saveComment(null, meeting, content, account);

        notificationService.createNotification(meeting.getOrganizer(), account.getNickname() + "님이 모임에 댓글을 달았습니다.");

        return comment;
    }

    public Comment updateComment(Long commentId, String content, Account account) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException("댓글을 찾을 수 없습니다."));
        if (!comment.getAuthor().equals(account)) {
            throw new CustomException("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        comment.setContent(content);
        comment.setUpdatedDateTime(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, Account account) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException("댓글을 찾을 수 없습니다."));
        if (!comment.getAuthor().equals(account)) {
            throw new CustomException("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        commentRepository.delete(comment);
    }

    private Comment saveComment(Board board, Meeting meeting, String content, Account account) {
        Comment comment = Comment.builder()
                .board(board)
                .meeting(meeting)
                .author(account)
                .content(content)
                .createdDateTime(LocalDateTime.now())
                .build();
        return commentRepository.save(comment);
    }

}