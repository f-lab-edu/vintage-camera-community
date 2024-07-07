package com.zerozone.vintage.chat;

import lombok.RequiredArgsConstructor;
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

        Long sortedUser1Id = Math.min(user1Id, user2Id);
        Long sortedUser2Id = Math.max(user1Id, user2Id);

        Optional<ChatRoom> roomOpt = chatRoomRepository.findByUser1IdAndUser2Id(sortedUser1Id, sortedUser2Id);

        return roomOpt.map(ChatRoom::getId).orElseGet(() -> {
            ChatRoom newRoom = ChatRoom.builder()
                    .user1Id(sortedUser1Id)
                    .user2Id(sortedUser2Id)
                    .build();
            return chatRoomRepository.save(newRoom).getId();
        });
    }
}
