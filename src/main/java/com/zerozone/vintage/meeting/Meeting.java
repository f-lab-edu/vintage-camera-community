package com.zerozone.vintage.meeting;

import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.tag.CameraTag;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String description;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @ManyToMany
    private Set<CameraTag> tags;  // 모임과 관련된 카메라 태그

    @ManyToOne
    private Account organizer;  // 모임 주최자

    @ManyToMany
    private Set<Account> participants;  // 모임 참여자들

    @Enumerated(EnumType.STRING)
    private MeetingStatus status;  // 모임 상태 (시작됨, 종료됨 등)

    private boolean isPublic;  // 모임 공개 여부
}
