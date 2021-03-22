package com.cudrania.side.spring.resolver;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 * 错误消息响应对象
 */
@Builder
@Data
public class ErrorResponse {

    private final String errorCode;
    private final String message;
    @Singular
    private final List<ErrorField> fields;


    /**
     * 错误字段
     */
    @Data
    @AllArgsConstructor
    public static class ErrorField {
        private final String field;
        private final String message;
    }

}
