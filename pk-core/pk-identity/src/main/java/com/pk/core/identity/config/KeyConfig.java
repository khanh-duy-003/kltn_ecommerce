package com.pk.core.identity.config;

import com.nimbusds.jose.jwk.RSAKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/** Nạp (hoặc sinh) cặp khoá RSA dùng ký/kiểm tra JWT. */
@Configuration
public class KeyConfig {

    private static final Logger log = LoggerFactory.getLogger(KeyConfig.class);

    @Bean
    public RSAKey rsaKey(JwtProperties props) {
        try {
            RSAPublicKey publicKey;
            RSAPrivateKey privateKey;
            if (StringUtils.hasText(props.getPrivateKey()) && StringUtils.hasText(props.getPublicKey())) {
                KeyFactory kf = KeyFactory.getInstance("RSA");
                privateKey = (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(decodePem(props.getPrivateKey())));
                publicKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(decodePem(props.getPublicKey())));
            } else {
                log.warn("app.jwt.private-key/public-key chưa cấu hình: sinh khoá RSA tạm thời (CHỈ DÙNG DEV).");
                KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
                gen.initialize(2048);
                KeyPair pair = gen.generateKeyPair();
                publicKey = (RSAPublicKey) pair.getPublic();
                privateKey = (RSAPrivateKey) pair.getPrivate();
            }
            return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID("pk-core-1").build();
        } catch (Exception e) {
            throw new IllegalStateException("Không nạp được khoá JWT: " + e.getMessage(), e);
        }
    }

    static byte[] decodePem(String pem) {
        String body = pem.replaceAll("-----(BEGIN|END)[A-Z ]+-----", "").replaceAll("\\s+", "");
        return Base64.getDecoder().decode(body);
    }
}
