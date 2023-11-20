package com.mojh.dailybudget.auth.jwt;

import java.util.Map;

public interface JwtProvider {

    String generateToken(Map<String, String> claims);

    String generateToken(Map<String, String> claims, String tokenId);

    void validateToken(String token);

    <T> T parseClaim(String token, String claimName);

    Map<String, String> parseClaims(String token);

    long getRemainingExpirationTime(String token);

}

