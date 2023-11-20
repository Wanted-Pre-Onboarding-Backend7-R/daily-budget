package com.mojh.dailybudget.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokensResponse {

    private String accessToken;

    private String refreshToken;

    @Builder
    public TokensResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
