package com.pk.core.identity.security.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/** Principal đặt vào SecurityContext sau khi xác thực JWT; lấy bằng @AuthenticationPrincipal. */
public class SecurityUser {

    private final Long id;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public SecurityUser(Long id, String email, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.authorities = authorities;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
}
