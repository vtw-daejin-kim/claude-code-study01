package kr.go.ecommerce.global.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void successWithoutData() {
        ApiResponse<Void> response = ApiResponse.success();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isNull();
    }

    @Test
    void successWithData() {
        ApiResponse<String> response = ApiResponse.success("hello");
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo("hello");
    }

    @Test
    void createdWithData() {
        ApiResponse<Long> response = ApiResponse.created(42L);
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo(42L);
    }
}
