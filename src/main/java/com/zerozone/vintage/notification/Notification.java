package com.zerozone.vintage.notification;

import com.zerozone.vintage.account.Account;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Account recipient;

    private String message;

    private LocalDateTime createdDateTime;

    private boolean read;  // 알림 확인 여부
}