package kr.go.ecommerce.global.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        String secret = "test-secret-key-that-is-at-least-256-bits-long-for-hs256-algorithm-testing";
        provider = new JwtTokenProvider(secret, 3600000L);
    }

    @Test
    void createTokenShouldReturnJwt3Parts() {
        String token = provider.createToken(1L, "USER");
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void shouldExtractUserId() {
        String token = provider.createToken(42L, "USER");
        assertThat(provider.getUserId(token)).isEqualTo(42L);
    }

    @Test
    void shouldExtractRole() {
        String token = provider.createToken(1L, "ADMIN");
        assertThat(provider.getRole(token)).isEqualTo("ADMIN");
    }

    @Test
    void validTokenShouldPassValidation() {
        String token = provider.createToken(1L, "USER");
        assertThat(provider.validateToken(token)).isTrue();
    }

    @Test
    void tamperedTokenShouldFailValidation() {
        String token = provider.createToken(1L, "USER");
        String tampered = token + "x";
        assertThat(provider.validateToken(tampered)).isFalse();
    }

    @Test
    void expiredTokenShouldFailValidation() {
        JwtTokenProvider shortLived = new JwtTokenProvider(
                "test-secret-key-that-is-at-least-256-bits-long-for-hs256-algorithm-testing",
                -1000L // already expired
        );
        String token = shortLived.createToken(1L, "USER");
        assertThat(provider.validateToken(token)).isFalse();
    }

    @Test
    void nullTokenShouldFailValidation() {
        assertThat(provider.validateToken(null)).isFalse();
    }

    @Test
    void blankTokenShouldFailValidation() {
        assertThat(provider.validateToken("")).isFalse();
        assertThat(provider.validateToken("  ")).isFalse();
    }

    @Test
    void garbageTokenShouldFailValidation() {
        assertThat(provider.validateToken("not.a.jwt")).isFalse();
    }
}
