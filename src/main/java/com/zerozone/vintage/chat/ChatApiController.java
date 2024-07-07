package com.zerozone.vintage.chat;

import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.dto.CustomResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Transactional
public class ChatApiController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {
        Long roomId = chatRoomService.getOrCreateRoomId(Long.valueOf(message.getAuthorId()), message.getRoomId());
        message.setRoomId(roomId);

        return chatMessageService.saveMessage(message);
    }

    @PostMapping("/sendMessage")
    @Operation(summary = "채팅 메시지 전송", description = "채팅방에 메시지 전송")
    @ApiResponse(responseCode = "200", description = "메시지 전송 성공", content = @Content(schema = @Schema(implementation = ChatMessage.class)))
    public ResponseEntity<ChatMessage> sendMessageViaRest(@RequestBody ChatMessage message) {
        Long roomId = chatRoomService.getOrCreateRoomId(Long.valueOf(message.getAuthorId()), message.getRoomId());
        message.setRoomId(roomId);

        ChatMessage savedMessage = chatMessageService.saveMessage(message);
        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/messages")
    @Operation(summary = "채팅 메시지 조회", description = "채팅방의 모든 메시지 조회")
    @ApiResponse(responseCode = "200", description = "메시지 조회 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<List<ChatMessage>>> getMessages(@RequestParam Long roomId) {
        List<ChatMessage> messages = chatMessageService.getMessagesByRoomId(roomId);
        return ResponseEntity.ok(new CustomResDto<>(1, "메시지 조회 성공", messages));
    }

    @GetMapping("/roomId")
    @Operation(summary = "채팅방 ID 조회 또는 생성", description = "상대방 ID를 사용하여 채팅방 ID를 조회하거나 생성")
    @ApiResponse(responseCode = "200", description = "채팅방 ID 조회 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Long>> getOrCreateRoomId(@CheckedUser Account account, @RequestParam Long otherUserId) {
        Long roomId = chatRoomService.getOrCreateRoomId(account.getId(), otherUserId);
        return ResponseEntity.ok(new CustomResDto<>(1, "채팅방 ID 조회 성공", roomId));
    }
}
