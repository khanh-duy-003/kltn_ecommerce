package com.pk.core.identity.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Cấu hình JWT (prefix app.jwt).
 * privateKey/publicKey: nội dung PEM (có hoặc không có dòng BEGIN/END). Để trống => sinh cặp khoá tạm
 * khi khởi động (chỉ dùng dev: restart là mọi token cũ mất hiệu lực).
 */
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String issuer = "pk-core";
    private long accessTokenMinutes = 15;
    private long refreshTokenDays = 14;
    private String privateKey;
    private String publicKey;

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
    public long getAccessTokenMinutes() { return accessTokenMinutes; }
    public void setAccessTokenMinutes(long accessTokenMinutes) { this.accessTokenMinutes = accessTokenMinutes; }
    public long getRefreshTokenDays() { return refreshTokenDays; }
    public void setRefreshTokenDays(long refreshTokenDays) { this.refreshTokenDays = refreshTokenDays; }
    public String getPrivateKey() { return privateKey; }
    public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }
    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
}
