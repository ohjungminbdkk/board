package com.example.board.boundedContext.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.board.boundedContext.user")  // JPA Repository 위치
public class JpaConfig {
}