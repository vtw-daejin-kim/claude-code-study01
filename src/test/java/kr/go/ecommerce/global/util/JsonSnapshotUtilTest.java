package kr.go.ecommerce.global.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonSnapshotUtilTest {

    @Test
    void toJsonShouldSerialize() {
        SampleDto dto = new SampleDto("hello", 42);
        String json = JsonSnapshotUtil.toJson(dto);
        assertThat(json).contains("\"name\":\"hello\"");
        assertThat(json).contains("\"value\":42");
    }

    @Test
    void fromJsonShouldDeserialize() {
        String json = "{\"name\":\"world\",\"value\":99}";
        SampleDto dto = JsonSnapshotUtil.fromJson(json, SampleDto.class);
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("world");
        assertThat(dto.getValue()).isEqualTo(99);
    }

    @Test
    void toJsonNullShouldReturnNull() {
        assertThat(JsonSnapshotUtil.toJson(null)).isNull();
    }

    @Test
    void fromJsonNullShouldReturnNull() {
        assertThat(JsonSnapshotUtil.fromJson(null, SampleDto.class)).isNull();
    }

    @Test
    void fromJsonBlankShouldReturnNull() {
        assertThat(JsonSnapshotUtil.fromJson("", SampleDto.class)).isNull();
        assertThat(JsonSnapshotUtil.fromJson("  ", SampleDto.class)).isNull();
    }

    @Test
    void roundTrip() {
        SampleDto original = new SampleDto("test", 123);
        String json = JsonSnapshotUtil.toJson(original);
        SampleDto restored = JsonSnapshotUtil.fromJson(json, SampleDto.class);
        assertThat(restored).isNotNull();
        assertThat(restored.getName()).isEqualTo(original.getName());
        assertThat(restored.getValue()).isEqualTo(original.getValue());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class SampleDto {
        private String name;
        private int value;
    }
}
