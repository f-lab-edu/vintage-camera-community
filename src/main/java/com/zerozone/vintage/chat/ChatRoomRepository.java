package com.zerozone.vintage.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Lock(LockModeType.OPTIMISTIC)
    Optional<ChatRoom> findByUser1IdAndUser2IdOrUser2IdAndUser1Id(Long user1Id, Long user2Id, Long user2Id2, Long user1Id2);
}
