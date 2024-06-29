package com.zerozone.vintage.like;

import com.zerozone.vintage.board.BoardRepository;
import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.board.Board;
import com.zerozone.vintage.exception.CustomException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeDislikeService {

    private final LikeDislikeRepository likeDislikeRepository;
    private final BoardRepository boardRepository;
    private final EntityManager entityManager;

    public LikeDislike likePost(Long boardId, Account account, boolean isLike) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException("게시글을 찾을 수 없습니다."));


        LikeDislike likeDislike = likeDislikeRepository.findByBoardAndAccount(board, account)
                .orElseGet(() -> LikeDislike.builder()
                        .board(board)
                        .account(account)
                        .isLike(isLike)
                        .build());

        likeDislike.setLike(isLike);
        return likeDislikeRepository.save(likeDislike);

        /*
        낙관적 락으로 처리할 경우
        try {
            return likeDislikeRepository.save(likeDislike);
        } catch (OptimisticLockingFailureException e) {
            throw new CustomException("요청이 처리되는 중에 문제가 발생했습니다. 다시 시도해주세요.");
        }
        */
    }
}