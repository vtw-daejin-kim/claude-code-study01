package kr.go.ecommerce.global.dto;

import lombok.Getter;

@Getter
public class FieldError {

    private final String field;
    private final String value;
    private final String reason;

    private FieldError(String field, String value, String reason) {
        this.field = field;
        this.value = value;
        this.reason = reason;
    }

    public static FieldError of(String field, String value, String reason) {
        return new FieldError(field, value, reason);
    }
}
