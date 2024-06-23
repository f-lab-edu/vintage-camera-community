package com.zerozone.vintage.notification;

import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.dto.CustomResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationApiController {

    private final NotificationService notificationService;


    @GetMapping("/all")
    @Operation(summary = "모든 알림 조회", description = "모든 알림을 조회")
    @ApiResponse(responseCode = "200", description = "알림 조회 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<List<Notification>>> getAllNotifications(@CheckedUser Account account) {
        List<Notification> notifications = notificationService.getAllNotifications(account);
        return ResponseEntity.ok(new CustomResDto<>(1, "알림 조회 성공.", notifications));
    }

    @GetMapping("/unread")
    @Operation(summary = "읽지 않은 알림 조회", description = "읽지 않은 알림을 조회")
    @ApiResponse(responseCode = "200", description = "알림 조회 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<List<Notification>>> getUnreadNotifications(@CheckedUser Account account) {
        List<Notification> notifications = notificationService.getUnreadNotifications(account);
        return ResponseEntity.ok(new CustomResDto<>(1, "알림 조회 성공.", notifications));
    }


    @PostMapping("/{id}/read")
    @Operation(summary = "알림 읽음 표시", description = "특정 알림을 읽음 상태로 변경")
    @ApiResponse(responseCode = "200", description = "알림 읽음 표시 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(new CustomResDto<>(1, "알림 읽음 표시 성공.", null));
    }
}