package com.zerozone.vintage.like;

import com.zerozone.vintage.board.BoardRepository;
import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.board.Board;
import com.zerozone.vintage.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeDislikeService {

    private final LikeDislikeRepository likeDislikeRepository;
    private final BoardRepository boardRepository;

    public LikeDislike likePost(Long boardId, Account account, boolean isLike) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException("게시글을 찾을 수 없습니다."));

        LikeDislike likeDislike = likeDislikeRepository.findByBoardAndAccount(board, account)
                .orElseGet(() -> LikeDislike.builder()
                .board(board)
                .account(account)
                .build());
        return likeDislikeRepository.save(likeDislike);
    }
}