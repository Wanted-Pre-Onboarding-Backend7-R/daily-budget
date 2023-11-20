package com.mojh.daliybudget.auth.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class AccessTokenProvider extends AbstractJJwtProvider {

    public AccessTokenProvider(@Value("${jwt.access.token-valid-time}") final long expiration,
                               @Value("${jwt.access.secret-key}") final String accessTokenSecretKeyRaw) {
        super(expiration, Keys.hmacShaKeyFor(accessTokenSecretKeyRaw.getBytes(StandardCharsets.UTF_8)),
                SignatureAlgorithm.HS512);
    }

}
