package kr.go.ecommerce.global.exception;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorCodeTest {

    @Test
    void allErrorCodesShouldHaveValidHttpStatus() {
        for (ErrorCode code : ErrorCode.values()) {
            assertThat(code.getStatus()).isNotNull();
            assertThat(code.getStatus().value()).isBetween(100, 599);
        }
    }

    @Test
    void allCodesShouldBeUnique() {
        List<String> codes = Arrays.stream(ErrorCode.values())
                .map(ErrorCode::getCode)
                .toList();
        assertThat(codes).doesNotHaveDuplicates();
    }

    @Test
    void allMessagesShouldNotBeEmpty() {
        for (ErrorCode code : ErrorCode.values()) {
            assertThat(code.getMessage()).isNotBlank();
        }
    }

    @Test
    void specificErrorCodeMappings() {
        assertThat(ErrorCode.INTERNAL_ERROR.getStatus().value()).isEqualTo(500);
        assertThat(ErrorCode.INVALID_INPUT.getStatus().value()).isEqualTo(400);
        assertThat(ErrorCode.UNAUTHORIZED.getStatus().value()).isEqualTo(401);
        assertThat(ErrorCode.FORBIDDEN.getStatus().value()).isEqualTo(403);
        assertThat(ErrorCode.USER_NOT_FOUND.getStatus().value()).isEqualTo(404);
        assertThat(ErrorCode.STOCK_INSUFFICIENT.getStatus().value()).isEqualTo(409);
    }
}
