package com.zerozone.vintage.board;

import com.zerozone.vintage.domain.Account;
import com.zerozone.vintage.domain.Board;
import com.zerozone.vintage.exception.CustomException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private  final BoardRepository boardRepository;
    private final ModelMapper modelMapper;

    public Board createNewPost(Board board, Account account) {
        board.setAuthor(account);
        board.setPublishedDateTime(LocalDateTime.now());
        board.setPublished(true); //우선 모두 다 활성화로 진행
        //TODO 추후 카메라 태그값이나 게시글 태그값 추가, 공개비공개 설정 추가
        Board newPost = boardRepository.save(board);
        return newPost;
    }

    public Board getBoard(Long id) {
        return boardRepository.findById(id).orElseThrow(() -> new CustomException("해당 게시글을 찾을 수 없습니다."));
    }

    public Board updatePost(Long id, BoardForm boardForm, Account account) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException("게시글을 찾을 수 없습니다."));

        modelMapper.map(boardForm, board);
        board.setUpdatedDateTime(LocalDateTime.now());

        return boardRepository.save(board);
    }

    public Board findById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new CustomException("게시글을 찾을 수 없습니다."));
    }


    public void deletePost(Long id, Account account) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException("게시글을 찾을 수 없습니다."));

        if (!board.getAuthor().equals(account)) {
            throw new CustomException("삭제 권한이 없습니다.");
        }

        boardRepository.delete(board);
    }

    public List<Board> findAllBoards() {
        return boardRepository.findAll();
    }
}
