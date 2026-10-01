package com.pk.core.test.identity;

import com.nimbusds.jose.jwk.RSAKey;
import com.pk.core.identity.config.JwtConfig;
import com.pk.core.identity.config.JwtProperties;
import com.pk.core.identity.config.KeyConfig;
import com.pk.core.identity.security.jwt.JwtProvider;
import com.pk.core.model.entity.RoleEntity;
import com.pk.core.model.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Test thuần (không khởi động Spring): ký rồi kiểm tra JWT bằng đúng cấu hình production. */
class JwtProviderTest {

    private JwtProperties props;
    private RSAKey key;
    private JwtConfig config;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        props = new JwtProperties();
        props.setIssuer("pk-core-test");
        key = new KeyConfig().rsaKey(props); // không cấu hình khoá => sinh khoá tạm
        config = new JwtConfig();
        user = new UserEntity("a@b.com", "hash", "A", null);
        ReflectionTestUtils.setField(user, "id", 42L);
        user.getRoles().add(new RoleEntity(RoleEntity.ADMIN));
    }

    private JwtProvider provider() {
        return new JwtProvider(config.jwtEncoder(key), props);
    }

    @Test
    void issuedTokenCarriesSubjectRolesAndPermissions() {
        JwtProvider.AccessToken at = provider().issueAccessToken(user);
        Jwt jwt = config.jwtDecoder(key, props).decode(at.token());

        assertEquals("42", jwt.getSubject());
        assertEquals("a@b.com", jwt.getClaimAsString("email"));
        assertTrue(jwt.getClaimAsStringList("roles").contains("ADMIN"));
        assertTrue(jwt.getClaimAsStringList("perms").contains("PRODUCT_WRITE"));
        assertEquals(props.getAccessTokenMinutes() * 60, at.expiresInSeconds());
    }

    @Test
    void expiredTokenIsRejected() {
        // token đã hết hạn từ 10 phút trước (vượt clock-skew 60s); encoder không cho exp <= iat nên tự dựng iat cũ hơn
        java.time.Instant now = java.time.Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(props.getIssuer())
                .issuedAt(now.minusSeconds(1200))
                .expiresAt(now.minusSeconds(600))
                .subject("42")
                .build();
        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();
        String token = config.jwtEncoder(key).encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        assertThrows(JwtException.class, () -> config.jwtDecoder(key, props).decode(token));
    }

    @Test
    void tamperedTokenIsRejected() {
        String token = provider().issueAccessToken(user).token();
        String[] parts = token.split("\\.");
        String payload = parts[1];
        char flipped = payload.charAt(5) == 'A' ? 'B' : 'A';
        String tampered = parts[0] + "." + payload.substring(0, 5) + flipped + payload.substring(6) + "." + parts[2];

        assertThrows(JwtException.class, () -> config.jwtDecoder(key, props).decode(tampered));
    }

    @Test
    void tokenFromAnotherKeyIsRejected() {
        RSAKey otherKey = new KeyConfig().rsaKey(new JwtProperties());
        String token = new JwtProvider(config.jwtEncoder(otherKey), props).issueAccessToken(user).token();

        JwtDecoder decoder = config.jwtDecoder(key, props);
        assertThrows(JwtException.class, () -> decoder.decode(token));
    }

    @Test
    void tokenWithWrongIssuerIsRejected() {
        JwtProperties other = new JwtProperties();
        other.setIssuer("someone-else");
        String token = new JwtProvider(config.jwtEncoder(key), other).issueAccessToken(user).token();

        assertThrows(JwtException.class, () -> config.jwtDecoder(key, props).decode(token));
    }
}
