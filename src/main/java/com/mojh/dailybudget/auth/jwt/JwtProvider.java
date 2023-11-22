package com.mojh.dailybudget.auth.jwt;

import java.util.Map;

public interface JwtProvider {

    String generateToken(String subject);

    String generateTokenWithClaims(String subject, Map<String, String> claims);

    String generateTokenWithClaims(String subject, Map<String, String> claims, String tokenId);

    void validateToken(String token);

    <T> T parseClaim(String token, String claimName);

    <T> T parseSubject(String token);

    Map<String, String> parseClaims(String token);

    long getRemainingExpirationTime(String token);

}

