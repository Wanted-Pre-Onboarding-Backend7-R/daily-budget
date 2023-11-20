package com.mojh.daliybudget.auth.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class RefreshTokenProvider extends AbstractJJwtProvider {

    public RefreshTokenProvider(@Value("${jwt.refresh.token-valid-time}") final long expiration,
                                @Value("${jwt.refresh.secret-key}") final String refreshTokenSecretKeyRaw) {
        super(expiration, Keys.hmacShaKeyFor(refreshTokenSecretKeyRaw.getBytes(StandardCharsets.UTF_8)),
                SignatureAlgorithm.HS512);
    }

}
