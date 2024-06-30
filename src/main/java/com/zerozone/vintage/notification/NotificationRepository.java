package com.zerozone.vintage.notification;

import com.zerozone.vintage.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient(Account recipient);

    List<Notification> findByRecipientAndReadFalse(Account recipient);
}