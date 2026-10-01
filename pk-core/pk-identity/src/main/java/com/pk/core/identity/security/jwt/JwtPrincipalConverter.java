package com.pk.core.identity.security.jwt;

import com.pk.core.identity.security.model.SecurityUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** Chuyển Jwt đã kiểm tra thành Authentication với principal là SecurityUser (ROLE_xxx + permission). */
@Component
public class JwtPrincipalConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<String> roles = jwt.getClaimAsStringList("roles");
        if (roles != null) {
            roles.forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));
        }
        List<String> perms = jwt.getClaimAsStringList("perms");
        if (perms != null) {
            perms.forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
        }
        SecurityUser principal = new SecurityUser(Long.valueOf(jwt.getSubject()), jwt.getClaimAsString("email"), authorities);
        return new UsernamePasswordAuthenticationToken(principal, jwt.getTokenValue(), authorities);
    }
}
