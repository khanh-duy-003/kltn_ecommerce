package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDto {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private UserResponseDto user;

    public static TokenResponseDto bearer(String accessToken, String refreshToken, long expiresIn, UserResponseDto user) {
        return new TokenResponseDto(accessToken, refreshToken, "Bearer", expiresIn, user);
    }
}
