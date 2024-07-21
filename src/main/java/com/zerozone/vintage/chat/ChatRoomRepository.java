package com.zerozone.vintage.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Lock(LockModeType.OPTIMISTIC)
    Optional<ChatRoom> findByUser1IdAndUser2Id(Long minId, Long maxId);
}
