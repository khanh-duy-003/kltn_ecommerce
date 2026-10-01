package com.pk.core.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Điểm khởi động. Các module khác (common, model, identity, business, api) nằm ngoài gói này
 * nên phải chỉ rõ gói gốc com.pk.core để Spring quét thấy.
 * Repository Mirage được bật ở BusinessRepositoryConfig (toàn bộ repository - kể cả User/Role/
 * RefreshToken trước đây ở pk-identity - nay gộp chung vào pk-business.repository), bean
 * "sqlManager" tạo ở MirageConfig - cả hai đã chuyển hẳn sang pk-business (2026-09-25), pk-service
 * chỉ còn đúng lớp khởi động này. DataSeeder (dữ liệu demo dev) cũng đã chuyển sang pk-business.
 */
@SpringBootApplication(scanBasePackages = "com.pk.core")
public class PkServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PkServiceApplication.class, args);
    }
}
