package com.zerozone.vintage.board;

import com.zerozone.vintage.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface BoardRepository extends JpaRepository<Board, Long> {
    boolean existsByTitle(String title);
}

