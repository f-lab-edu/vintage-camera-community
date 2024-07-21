package com.zerozone.vintage.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public Long getOrCreateRoomId(Long user1Id, Long user2Id) {
        if (user1Id == null || user2Id == null) {
            throw new IllegalArgumentException("채팅 유저ID가 없습니다. 확인해주세요.");
        }

        try {
            Long minId = Math.min(user1Id, user2Id);
            Long maxId = Math.max(user1Id, user2Id);

            Optional<ChatRoom> roomOpt = chatRoomRepository.findByUser1IdAndUser2Id(minId, maxId);

            return roomOpt.map(ChatRoom::getId).orElseGet(() -> {
                ChatRoom newRoom = ChatRoom.builder()
                        .user1Id(user1Id)
                        .user2Id(user2Id)
                        .build();
                return chatRoomRepository.save(newRoom).getId();
            });
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("잠시 후 다시 시도해주세요.", e);
        }
    }

    public Long getRoomId(Long user1Id, Long user2Id) {
        if (user1Id == null || user2Id == null) {
            throw new IllegalArgumentException("채팅 유저ID가 없습니다. 확인해주세요.");
        }

        Long minId = Math.min(user1Id, user2Id);
        Long maxId = Math.max(user1Id, user2Id);

        return chatRoomRepository.findByUser1IdAndUser2Id(minId, maxId)
                .map(ChatRoom::getId)
                .orElse(null);
    }



    public Long createRoom(Long user1Id, Long user2Id) {
        if (user1Id == null || user2Id == null) {
            throw new IllegalArgumentException("채팅 유저ID가 없습니다. 확인해주세요.");
        }

        ChatRoom newRoom = ChatRoom.builder()
                .user1Id(user1Id)
                .user2Id(user2Id)
                .build();
        return chatRoomRepository.save(newRoom).getId();
    }

}
