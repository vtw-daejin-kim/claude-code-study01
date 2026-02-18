package kr.go.ecommerce.global.util;

import kr.go.ecommerce.global.exception.BusinessException;
import kr.go.ecommerce.global.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityUtilTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldExtractUserId() {
        setAuthentication(42L, "USER");
        assertThat(SecurityUtil.getCurrentUserId()).isEqualTo(42L);
    }

    @Test
    void shouldExtractRole() {
        setAuthentication(1L, "ADMIN");
        assertThat(SecurityUtil.getCurrentUserRole()).isEqualTo("ADMIN");
    }

    @Test
    void shouldThrowWhenNoAuthentication() {
        assertThatThrownBy(SecurityUtil::getCurrentUserId)
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldThrowWhenNoAuthenticationForRole() {
        assertThatThrownBy(SecurityUtil::getCurrentUserRole)
                .isInstanceOf(BusinessException.class);
    }

    private void setAuthentication(Long userId, String role) {
        CustomUserDetails details = new CustomUserDetails(userId, role, "");
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
