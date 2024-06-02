package com.zerozone.vintage.board;

import com.zerozone.vintage.domain.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class BoardFormValidator implements Validator {

    private final BoardRepository boardRepository;
    @Override
    public boolean supports(Class<?> clazz) {
        return BoardForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BoardForm boardForm = (BoardForm) target;

        if (boardRepository.existsByTitle(boardForm.getTitle())) {
            errors.rejectValue("title", "duplicate.title", "이미 사용 중인 제목입니다.");
        }

        if (boardForm.getCategory() == null) {
            errors.rejectValue("category", "invalid.category", "카테고리를 선택해야 합니다.");
        }
    }
}
