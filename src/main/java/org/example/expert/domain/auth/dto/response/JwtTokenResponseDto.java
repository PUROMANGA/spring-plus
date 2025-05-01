package org.example.expert.domain.auth.dto.response;

import lombok.Getter;

@Getter

public class JwtTokenResponseDto {

    private final String bearerToken;

    public JwtTokenResponseDto(String bearerToken) {
        this.bearerToken = bearerToken;
    }
}
