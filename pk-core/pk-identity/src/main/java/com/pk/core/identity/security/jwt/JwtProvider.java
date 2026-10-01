package com.pk.core.identity.security.jwt;


import com.pk.core.identity.config.JwtProperties;
import com.pk.core.identity.security.model.Permission;
import com.pk.core.model.entity.RoleEntity;
import com.pk.core.model.entity.UserEntity;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** Phát hành access token (RS256). Việc kiểm tra token do JwtDecoder + resource server đảm nhiệm. */
@Component
public class JwtProvider {

    public record AccessToken(String token, long expiresInSeconds) {
    }

    private final JwtEncoder encoder;
    private final JwtProperties props;

    public JwtProvider(JwtEncoder encoder, JwtProperties props) {
        this.encoder = encoder;
        this.props = props;
    }

    public AccessToken issueAccessToken(UserEntity user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(props.getAccessTokenMinutes(), ChronoUnit.MINUTES);
        List<String> roles = user.getRoles().stream().map(RoleEntity::getName).sorted().toList();
        List<String> perms = Permission.forRoles(roles).stream().map(Enum::name).sorted().toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(props.getIssuer())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("roles", roles)
                .claim("perms", perms)
                .build();
        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new AccessToken(token, props.getAccessTokenMinutes() * 60);
    }
}
