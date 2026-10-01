package com.pk.core.test.identity;

import com.pk.core.common.exception.BusinessException;
import com.pk.core.identity.service.AuthService;
import com.pk.core.identity.service.impl.AuthServiceImpl;
import com.pk.core.identity.service.RefreshTokenService;
import com.pk.core.model.dto.request.LoginRequestDto;
import com.pk.core.model.dto.request.RegisterRequestDto;
import com.pk.core.model.dto.response.TokenResponseDto;
import com.pk.core.identity.security.jwt.JwtProvider;
import com.pk.core.model.entity.RoleEntity;
import com.pk.core.business.repository.RoleRepo;
import com.pk.core.model.entity.UserEntity;
import com.pk.core.business.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepo users;
    @Mock RoleRepo roles;
    @Mock PasswordEncoder encoder;
    @Mock JwtProvider jwtProvider;
    @Mock RefreshTokenService refreshTokens;

    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthServiceImpl(users, roles, encoder, jwtProvider, refreshTokens);
    }

    private UserEntity user(boolean enabled) {
        UserEntity u = new UserEntity("a@b.com", "hashed", "An", null);
        u.setEnabled(enabled);
        u.getRoles().add(new RoleEntity(RoleEntity.CUSTOMER));
        return u;
    }

    @Test
    void registerRejectsExistingEmail() {
        when(users.countByEmail("a@b.com")).thenReturn(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.register(new RegisterRequestDto("A@B.com", "password1", "An", null)));

        assertEquals("EMAIL_TAKEN", ex.getCode());
        assertEquals(409, ex.getStatus());
        verify(users, never()).create(any());
    }

    @Test
    void registerNormalizesEmailHashesPasswordAndAssignsCustomerRole() {
        when(users.countByEmail("a@b.com")).thenReturn(0L);
        when(encoder.encode("password1")).thenReturn("hashed");
        when(roles.findByName(RoleEntity.CUSTOMER)).thenReturn(new RoleEntity(RoleEntity.CUSTOMER));
        when(refreshTokens.issue(any())).thenReturn("refresh-raw");
        when(jwtProvider.issueAccessToken(any())).thenReturn(new JwtProvider.AccessToken("access", 900));

        TokenResponseDto res = service.register(new RegisterRequestDto("  A@B.com ", "password1", " An ", null));

        ArgumentCaptor<UserEntity> saved = ArgumentCaptor.forClass(UserEntity.class);
        verify(users).create(saved.capture());
        assertEquals("a@b.com", saved.getValue().getEmail());
        assertEquals("hashed", saved.getValue().getPasswordHash());
        assertEquals("An", saved.getValue().getFullName());
                assertEquals("access", res.getAccessToken());
        assertEquals("refresh-raw", res.getRefreshToken());
        assertEquals("Bearer", res.getTokenType());
    }

    @Test
    void loginFailsWithSameCodeForWrongPasswordAndUnknownEmail() {
        when(users.findByEmail("a@b.com")).thenReturn(user(true));
        when(encoder.matches("wrong", "hashed")).thenReturn(false);
        when(users.findByEmail("nobody@b.com")).thenReturn(null);

        BusinessException wrongPw = assertThrows(BusinessException.class,
                () -> service.login(new LoginRequestDto("a@b.com", "wrong")));
        BusinessException unknown = assertThrows(BusinessException.class,
                () -> service.login(new LoginRequestDto("nobody@b.com", "whatever")));

        assertEquals("INVALID_CREDENTIALS", wrongPw.getCode());
        assertEquals(wrongPw.getCode(), unknown.getCode());
        assertEquals(wrongPw.getMessage(), unknown.getMessage());
    }

    @Test
    void loginRejectsDisabledAccount() {
        when(users.findByEmail("a@b.com")).thenReturn(user(false));
        when(encoder.matches("password1", "hashed")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.login(new LoginRequestDto("a@b.com", "password1")));

        assertEquals("ACCOUNT_DISABLED", ex.getCode());
    }

    @Test
    void loginSucceedsWithCorrectPassword() {
        UserEntity u = user(true);
        when(users.findByEmail("a@b.com")).thenReturn(u);
        when(encoder.matches("password1", "hashed")).thenReturn(true);
        when(refreshTokens.issue(u)).thenReturn("refresh-raw");
        when(jwtProvider.issueAccessToken(u)).thenReturn(new JwtProvider.AccessToken("access", 900));

        TokenResponseDto res = service.login(new LoginRequestDto("A@B.com", "password1"));

        assertEquals("access", res.getAccessToken());
        assertEquals(900, res.getExpiresIn());
    }
}
