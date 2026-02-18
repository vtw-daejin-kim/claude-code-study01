package kr.go.ecommerce.global.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    @Test
    void shouldExposeUserId() {
        CustomUserDetails details = new CustomUserDetails(1L, "USER", "pass");
        assertThat(details.getUserId()).isEqualTo(1L);
    }

    @Test
    void shouldExposeRole() {
        CustomUserDetails details = new CustomUserDetails(1L, "ADMIN", "pass");
        assertThat(details.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void authoritiesShouldHaveRolePrefix() {
        CustomUserDetails details = new CustomUserDetails(1L, "USER", "pass");
        assertThat(details.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void accountStatusShouldBeTrue() {
        CustomUserDetails details = new CustomUserDetails(1L, "USER", "pass");
        assertThat(details.isAccountNonExpired()).isTrue();
        assertThat(details.isAccountNonLocked()).isTrue();
        assertThat(details.isCredentialsNonExpired()).isTrue();
        assertThat(details.isEnabled()).isTrue();
    }

    @Test
    void usernameShouldBeUserIdString() {
        CustomUserDetails details = new CustomUserDetails(42L, "USER", "pass");
        assertThat(details.getUsername()).isEqualTo("42");
    }
}
