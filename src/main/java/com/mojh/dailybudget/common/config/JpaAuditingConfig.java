package com.mojh.dailybudget.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@Configuration(proxyBeanMethods = false)
public class JpaAuditingConfig {
}
