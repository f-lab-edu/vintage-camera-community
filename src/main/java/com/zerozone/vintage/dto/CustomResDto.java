package com.zerozone.vintage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 공통 응답 DTO 클래스이다.
 * @param <T> 응답 데이터의 타입이다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "공통 응답 DTO")
public class CustomResDto<T> {
    @Schema(description = "응답 코드", example = "1")
    private int code; //(1: 성공, 0: 실패)

    @Schema(description = "응답 메시지", example = "success")
    private String message;

    @Schema(description = "응답 데이터")
    private T data;
}
