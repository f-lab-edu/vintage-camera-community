package com.zerozone.vintage.bookmark;

import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.board.Board;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
public class Bookmark {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Board board;

    @ManyToOne
    private Account account;
}