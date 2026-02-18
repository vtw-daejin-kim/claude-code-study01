package kr.go.ecommerce.global.config;

import kr.go.ecommerce.global.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class JpaConfigTest extends IntegrationTestBase {

    @Autowired
    private ApplicationContext context;

    @Test
    void jpaAuditingShouldBeEnabled() {
        assertThat(context.containsBean("jpaAuditingHandler")).isTrue();
    }
}
