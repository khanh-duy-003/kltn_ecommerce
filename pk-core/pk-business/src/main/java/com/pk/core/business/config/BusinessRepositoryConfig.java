package com.pk.core.business.config;

import org.springframework.context.annotation.Configuration;

import vn.com.unit.springframework.data.mirage.repository.config.EnableMirageRepositories;

/**
 * Bật repository Mirage của module business. Cần bean "sqlManager"
 * (xem MirageConfig - cùng gói business.config, chuyển từ pk-service sang ngày 2026-09-25).
 */
@Configuration
@EnableMirageRepositories(basePackages = "com.pk.core.business.repository")
public class BusinessRepositoryConfig {
}
