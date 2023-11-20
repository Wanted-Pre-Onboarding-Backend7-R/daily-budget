package com.mojh.daliybudget.auth.jwt;


import com.mojh.daliybudget.common.exception.DailyBudgetAppException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mojh.daliybudget.common.exception.ErrorCode.EXPIRED_TOKEN;
import static com.mojh.daliybudget.common.exception.ErrorCode.INVALID_TOKEN;

/**
 * JJWT 의존성 사용해서 만든 JWT
 */
public abstract class AbstractJJwtProvider implements JwtProvider {

    protected long expiration;

    protected SecretKey secretKey;

    protected SignatureAlgorithm signatureAlgorithm;

    public AbstractJJwtProvider(long expiration, SecretKey secretKey, SignatureAlgorithm signatureAlgorithm) {
        this.expiration = expiration;
        this.secretKey = secretKey;
        this.signatureAlgorithm = signatureAlgorithm;
    }

    @Override
    public String generateToken(Map<String, String> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                   .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                   .setClaims(claims)
                   .setIssuedAt(Date.from(now))
                   .setExpiration(Date.from(now.plusMillis(expiration)))
                   .signWith(secretKey, signatureAlgorithm)
                   .compact();
    }

    @Override
    public String generateToken(Map<String, String> claims, String tokenId) {
        Instant now = Instant.now();
        return Jwts.builder()
                   .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                   .setClaims(claims)
                   .setId(tokenId)
                   .setIssuedAt(Date.from(now))
                   .setExpiration(Date.from(now.plusMillis(expiration)))
                   .signWith(secretKey, signatureAlgorithm)
                   .compact();
    }

    @Override
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
        } catch (Exception ex) {
            if (ex instanceof ExpiredJwtException) {
                throw new DailyBudgetAppException(EXPIRED_TOKEN, ex);
            } else {
                throw new DailyBudgetAppException(INVALID_TOKEN, ex);
            }
        }
    }

    @Override
    public <T> T parseClaim(String token, String claimName) {
        Map<String, String> claims = parseClaims(token);
        return (T) claims.get(claimName);
    }

    @Override
    public Map<String, String> parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                       .setSigningKey(secretKey)
                       .build()
                       .parseClaimsJws(token)
                       .getBody()
                       .entrySet().stream()
                       .collect(Collectors.toMap(Map.Entry::getKey, entry -> String.valueOf(entry.getValue())));
        } catch (ExpiredJwtException ex) {
            return ex.getClaims()
                     .entrySet().stream()
                     .collect(Collectors.toMap(Map.Entry::getKey, entry -> String.valueOf(entry.getValue())));
        } catch (Exception ex) {
            throw new DailyBudgetAppException(INVALID_TOKEN, ex);
        }
    }

    @Override
    public long getRemainingExpirationTime(String token) {
        try {
            return Jwts.parserBuilder()
                       .setSigningKey(secretKey)
                       .build()
                       .parseClaimsJws(token)
                       .getBody()
                       .getExpiration()
                       .getTime() - Instant.now().toEpochMilli();
        } catch (Exception ex) {
            if (ex instanceof ExpiredJwtException) {
                throw new DailyBudgetAppException(EXPIRED_TOKEN, ex);
            } else {
                throw new DailyBudgetAppException(INVALID_TOKEN, ex);
            }
        }
    }

}
