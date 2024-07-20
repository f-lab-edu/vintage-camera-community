package com.zerozone.vintage.chat;

import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.account.AccountRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final AccountRepository accountRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        Optional<Account> author = accountRepository.findById(message.getAuthorId());
        author.ifPresent(account -> message.setAuthorName(account.getNickname()));
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getMessagesByRoomId(Long roomId) {
        return chatMessageRepository.findByRoomId(roomId);
    }
}

