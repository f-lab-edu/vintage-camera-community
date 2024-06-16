package com.zerozone.vintage.meeting;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    Page<Meeting> findByTitle(String titleKeyword, Pageable pageable);
    Page<Meeting> findByDescription(String descriptionKeyword, Pageable pageable);
    Page<Meeting> findByTitleOrDescription(String titleKeyword, String descriptionKeyword, Pageable pageable);
}