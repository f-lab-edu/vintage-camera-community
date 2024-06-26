package com.zerozone.vintage.like;

import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.board.Board;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
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
public class LikeDislike {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Board board;

    @ManyToOne
    private Account account;

    private boolean isLike;

    /*
    낙관적 락 경우
    @Version
    private Long version;
    */
}