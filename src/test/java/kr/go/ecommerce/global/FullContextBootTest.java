package kr.go.ecommerce.global;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class FullContextBootTest extends IntegrationTestBase {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
    }

    @Test
    void securityFilterChainBeanExists() {
        assertThat(context.containsBean("filterChain")).isTrue();
    }

    @Test
    void passwordEncoderBeanExists() {
        assertThat(context.containsBean("passwordEncoder")).isTrue();
    }
}
