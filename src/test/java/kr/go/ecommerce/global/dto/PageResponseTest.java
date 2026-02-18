package kr.go.ecommerce.global.dto;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageResponseTest {

    @Test
    void fromSpringPage() {
        Page<String> page = new PageImpl<>(
                List.of("a", "b", "c"),
                PageRequest.of(0, 10),
                3
        );

        PageResponse<String> response = PageResponse.from(page);

        assertThat(response.getContent()).containsExactly("a", "b", "c");
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isEqualTo(3);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.isLast()).isTrue();
    }

    @Test
    void ofMyBatisStyle() {
        List<String> content = List.of("x", "y");
        PageResponse<String> response = PageResponse.of(content, 0, 2, 5);

        assertThat(response.getContent()).containsExactly("x", "y");
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(2);
        assertThat(response.getTotalElements()).isEqualTo(5);
        assertThat(response.getTotalPages()).isEqualTo(3);
        assertThat(response.isLast()).isFalse();
    }

    @Test
    void ofLastPage() {
        PageResponse<String> response = PageResponse.of(List.of("z"), 2, 2, 5);
        assertThat(response.isLast()).isTrue();
    }
}
