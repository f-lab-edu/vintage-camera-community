package com.zerozone.vintage.like;

import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.dto.CustomResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeDislikeApiController {

    private final LikeDislikeService likeDislikeService;

    @PostMapping("/board/{boardId}")
    public ResponseEntity<CustomResDto<LikeDislike>> likePost(@CheckedUser Account account, @PathVariable Long boardId, @RequestBody boolean isLike) {
        LikeDislike newLikeDislike = likeDislikeService.likePost(boardId, account, isLike);

        String message = isLike ? "좋아요 등록 성공." : "싫어요 등록 성공";

        return ResponseEntity.ok(new CustomResDto<>(1, message, newLikeDislike));
    }
}