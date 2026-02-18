package kr.go.ecommerce.global.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("kr.go.ecommerce.domain.*.mapper")
public class MyBatisConfig {
}
