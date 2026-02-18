package kr.go.ecommerce.global.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    void ofCodeAndMessage() {
        ErrorResponse response = ErrorResponse.of("C001", "Internal server error");
        assertThat(response.getCode()).isEqualTo("C001");
        assertThat(response.getMessage()).isEqualTo("Internal server error");
        assertThat(response.getFieldErrors()).isEmpty();
    }

    @Test
    void ofCodeMessageAndFieldErrors() {
        List<FieldError> fieldErrors = List.of(
                FieldError.of("email", "", "must not be blank"),
                FieldError.of("name", "", "must not be blank")
        );
        ErrorResponse response = ErrorResponse.of("C002", "Invalid input value", fieldErrors);
        assertThat(response.getCode()).isEqualTo("C002");
        assertThat(response.getFieldErrors()).hasSize(2);
        assertThat(response.getFieldErrors().get(0).getField()).isEqualTo("email");
    }
}
