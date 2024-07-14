package com.zerozone.vintage.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getMessagesByRoomId(Long roomId) {
        return chatMessageRepository.findByRoomId(roomId);
    }

}
