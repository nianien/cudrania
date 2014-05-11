package com.kavas.misc.spring.resolver;

import java.util.ArrayList;
import java.util.List;

/**
 * 错误消息响应对象
 */
public class ErrorResponse {

    private String errorCode;
    private String message;
    private List<ErrorField> fields;

    public ErrorResponse() {
        errorCode = "";
        message = "";
        fields = new ArrayList<ErrorField>();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public ErrorResponse setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ErrorResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public ErrorResponse addErrorField(ErrorField errorField) {
        if (fields == null) {
            fields = new ArrayList<ErrorField>();
        }
        fields.add(errorField);
        return this;
    }

    public List<ErrorField> getFields() {
        return fields;
    }

    public ErrorResponse setFields(List<ErrorField> fields) {
        this.fields = fields;
        return this;
    }


    /**
     * 错误字段
     */
    public static class ErrorField {
        private String field;
        private String message;

        public ErrorField(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
