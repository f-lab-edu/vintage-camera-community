package com.zerozone.vintage.meeting;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    @Query("SELECT m FROM Meeting m LEFT JOIN FETCH m.organizer LEFT JOIN FETCH m.participants LEFT JOIN FETCH m.tags")
    Page<Meeting> findAllWithFetchJoin(Pageable pageable);

    @Query(value = "SELECT * FROM meeting WHERE to_tsvector('simple', title || ' ' || description) @@ to_tsquery('simple', :keyword)", nativeQuery = true)
    Page<Meeting> searchByFullText(@Param("keyword") String keyword, Pageable pageable);
}
