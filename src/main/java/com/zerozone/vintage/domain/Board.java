package com.zerozone.vintage.domain;

import com.zerozone.vintage.board.BoardCategory;
import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Board {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Account author;

    private String title;

    @Enumerated(EnumType.STRING)
    private BoardCategory boardCategory; // 게시글 분류

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    private String imageUrl;

    @ManyToMany
    private Set<CameraTag> tags = new HashSet<>(); //게시글 카메라 관심 태그

    private LocalDateTime publishedDateTime; // 게시일

    private LocalDateTime updatedDateTime; // 수정일

    private boolean published; // 공개 여부

}
