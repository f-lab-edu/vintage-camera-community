package com.zerozone.vintage.chat;

import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.dto.CustomResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Transactional
public class ChatApiController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/sendMessage")
    public void sendMessage(ChatMessage message) {
        Long roomId = chatRoomService.getOrCreateRoomId(message.getAuthorId(), message.getOtherUserId());
        message.setRoomId(roomId);

        ChatMessage savedMessage = chatMessageService.saveMessage(message);

        messagingTemplate.convertAndSend("/topic/messages/" + roomId, savedMessage);
    }

    @PostMapping("/sendMessage")
    @Operation(summary = "채팅 메시지 전송", description = "채팅방에 메시지 전송")
    @ApiResponse(responseCode = "200", description = "메시지 전송 성공", content = @Content(schema = @Schema(implementation = ChatMessage.class)))
    public ResponseEntity<ChatMessage> sendMessageViaRest(@RequestBody ChatMessage message) {
        Long roomId = chatRoomService.getOrCreateRoomId(message.getAuthorId(), message.getOtherUserId());
        message.setRoomId(roomId);

        ChatMessage savedMessage = chatMessageService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/messages/" + roomId, savedMessage);
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
    @Operation(summary = "채팅방 ID 조회", description = "상대방 ID를 사용하여 채팅방 ID를 조회")
    @ApiResponse(responseCode = "200", description = "채팅방 ID 조회 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Long>> getRoomId(@CheckedUser Account account, @RequestParam Long otherUserId) {
        Long roomId = chatRoomService.getRoomId(account.getId(), otherUserId);
        if (roomId != null) {
            return ResponseEntity.ok(new CustomResDto<>(1, "채팅방 ID 조회 성공", roomId));
        } else {
            return ResponseEntity.ok(new CustomResDto<>(0, "채팅방이 존재하지 않습니다.", null));
        }
    }

    @PostMapping("/room")
    @Operation(summary = "채팅방 생성", description = "상대방 ID를 사용하여 채팅방을 생성")
    @ApiResponse(responseCode = "201", description = "채팅방 생성 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Long>> createRoom(@CheckedUser Account account, @RequestParam Long otherUserId) {
        Long roomId = chatRoomService.createRoom(account.getId(), otherUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CustomResDto<>(1, "채팅방 생성 성공", roomId));
    }

}
