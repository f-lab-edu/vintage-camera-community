package com.zerozone.vintage.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface CameraTagRepository extends JpaRepository<CameraTag, Long> {
    Optional<CameraTag> findByTitle(String title);
}
