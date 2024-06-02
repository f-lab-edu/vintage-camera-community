package com.zerozone.vintage.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class BoardForm {

    @NotBlank
    @Length(max = 50)
    private String title;

    @NotNull(message = "카테고리를 선택해주세요.")
    private BoardCategory category;

    @NotBlank
    private String fullDescription;
}
