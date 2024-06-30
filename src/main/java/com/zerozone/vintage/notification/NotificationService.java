package com.zerozone.vintage.notification;

import com.zerozone.vintage.account.Account;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<Notification> getAllNotifications(Account recipient) {
        return notificationRepository.findByRecipient(recipient);
    }

    public List<Notification> getUnreadNotifications(Account recipient) {
        return notificationRepository.findByRecipientAndReadFalse(recipient);
    }

    public void createNotification(Account recipient, String message) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .message(message)
                .createdDateTime(LocalDateTime.now())
                .read(false)
                .build();
        notificationRepository.save(notification);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalStateException("알림을 찾을 수 없습니다."));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}