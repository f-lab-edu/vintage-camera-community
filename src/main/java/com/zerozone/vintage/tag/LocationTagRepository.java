package com.zerozone.vintage.tag;

import com.zerozone.vintage.domain.CameraTag;
import com.zerozone.vintage.domain.LocationTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface LocationTagRepository extends JpaRepository<LocationTag, Long> {
    Optional<LocationTag> findByTitle(String title);
}
