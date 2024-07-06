package com.zerozone.vintage.board;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface BoardRepository extends JpaRepository<Board, Long> {
    boolean existsByTitle(String title);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional(readOnly = false)
    Optional<Board> findByIdForUpdate(Long id);

    //네이티브 쿼리
    @Query(value = "SELECT * FROM board " +
                    "WHERE (:category IS NULL OR board_category = :category) " +
                    "AND ((:searchType = 'title' AND LOWER(title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                    "OR (:searchType = 'description' AND LOWER(full_description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                    "OR (:searchType = 'all' AND (LOWER(title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                    "OR LOWER(full_description) LIKE LOWER(CONCAT('%', :keyword, '%')))))",
            nativeQuery = true)
    List<Board> searchPosts(@Param("category") BoardCategory category, @Param("keyword") String keyword, @Param("searchType") String searchType);

}

