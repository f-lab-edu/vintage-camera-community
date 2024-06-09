package com.zerozone.vintage.like;

import com.zerozone.vintage.domain.LikeDislike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface LikeDislikeRepository extends JpaRepository<LikeDislike, Long> {
}
