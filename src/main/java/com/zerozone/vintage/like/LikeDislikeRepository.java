package com.zerozone.vintage.like;

import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.board.Board;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface LikeDislikeRepository extends JpaRepository<LikeDislike, Long> {
    Optional<LikeDislike> findByBoardAndAccount(Board board, Account account);
}
