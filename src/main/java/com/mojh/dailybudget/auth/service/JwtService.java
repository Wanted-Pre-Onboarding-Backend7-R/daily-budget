package com.mojh.dailybudget.auth.service;

import com.mojh.dailybudget.common.exception.DailyBudgetAppException;
import com.mojh.dailybudget.auth.domain.RefreshToken;
import com.mojh.dailybudget.auth.jwt.JwtProvider;
import com.mojh.dailybudget.auth.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static com.mojh.dailybudget.common.exception.ErrorCode.ALREADY_LOGGED_OUT;
import static com.mojh.dailybudget.common.exception.ErrorCode.INVALID_TOKEN;
import static com.mojh.dailybudget.common.exception.ErrorCode.MISMATCHED_TOKENS_ACCOUNT;
import static com.mojh.dailybudget.auth.SecurityConstants.ACCOUNT_ID_CLAIM_NAME;
import static com.mojh.dailybudget.auth.SecurityConstants.BEARER_PREFIX;
import static com.mojh.dailybudget.auth.SecurityConstants.REFRESH_TOKEN_CHAIN_CLAIM_NAME;


@Service
public class JwtService {
    private final JwtProvider accessTokenProvider;
    private final JwtProvider refreshTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtService(JwtProvider accessTokenProvider, JwtProvider refreshTokenProvider,
                      RefreshTokenRepository refreshTokenRepository) {
        this.accessTokenProvider = accessTokenProvider;
        this.refreshTokenProvider = refreshTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String generateAccessToken(String accountId) {
        Map<String, String> claims = Map.of(ACCOUNT_ID_CLAIM_NAME, accountId);
        return accessTokenProvider.generateToken(claims);
    }

    public String generateRefreshToken(String accountId) {
        return generateRefreshToken(accountId, UUID.randomUUID().toString());
    }

    public String generateRefreshToken(String accountId, String tokenChainId) {
        Map<String, String> claims = Map.of(
                ACCOUNT_ID_CLAIM_NAME, accountId,
                REFRESH_TOKEN_CHAIN_CLAIM_NAME, tokenChainId
        );
        String tokenId = UUID.randomUUID().toString();
        return refreshTokenProvider.generateToken(claims, tokenId);
    }

    public <T> T parseClaimAccessToken(String accessToken, String claimName) {
        return accessTokenProvider.parseClaim(accessToken, claimName);
    }

    public <T> T parseClaimRefreshToken(String refreshToken, String claimName) {
        return refreshTokenProvider.parseClaim(refreshToken, claimName);
    }

    public Map<String, String> parseClaimsRefreshToken(String refreshToken) {
        return refreshTokenProvider.parseClaims(refreshToken);
    }

    /**
     * 남은 만료 시간 milliseconds 단위로 반환
     * @param refreshToken
     * @return
     */
    public long getRemainingExpirationTimeRefreshToken(String refreshToken) {
        return refreshTokenProvider.getRemainingExpirationTime(refreshToken);
    }


    /**
     * 같은 계정에서 생성된 access, refresh token 인지 확인
     * @param accessToken
     * @param refreshToken
     */
    public void validateTokensAccount(String accessToken, String refreshToken) {
        String accessTokenAccountId = parseClaimAccessToken(accessToken, ACCOUNT_ID_CLAIM_NAME);
        String refreshTokenAccountId = parseClaimRefreshToken(refreshToken, ACCOUNT_ID_CLAIM_NAME);

        if(!accessTokenAccountId.equals(refreshTokenAccountId)) {
            throw new DailyBudgetAppException(MISMATCHED_TOKENS_ACCOUNT);
        }
    }

    /**
     * access token 유효성과 만료 여부 확인
     * @param accessToken
     */
    public void validateAccessToken(String accessToken) {
        accessTokenProvider.validateToken(accessToken);
    }

    /**
     * refresh token 유효성과 만료 여부 확인
     * @param refreshToken
     */
    public void validateRefreshToken(String refreshToken) {
        refreshTokenProvider.validateToken(refreshToken);
    }

    public String extractAccessTokenFrom(String header) {
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            throw new DailyBudgetAppException(INVALID_TOKEN);
        }

        return header.substring(BEARER_PREFIX.length());
    }

    @Transactional
    public void saveRefreshToken(String refreshToken) {
        Map<String, String> claims = parseClaimsRefreshToken(refreshToken);
        String refreshTokenChainId = claims.get(REFRESH_TOKEN_CHAIN_CLAIM_NAME);
        String accountId = claims.get(ACCOUNT_ID_CLAIM_NAME);
        String refreshTokenId = claims.get(Claims.ID);
        long ttl = getRemainingExpirationTimeRefreshToken(refreshToken);

        RefreshToken token = RefreshToken.builder()
                                         .chainId(refreshTokenChainId)
                                         .accountId(accountId)
                                         .tokenId(refreshTokenId)
                                         .ttl(ttl)
                                         .build();

        refreshTokenRepository.save(token);
    }

    /**
     * refresh token을 처음 사용하는 경우라면
     * <p>
     * token chain에서 가장 마지막으로 생성한 refresh token과 일치해야 한다
     * @param refreshToken
     * @return 재사용 의심 토큰이면 true 반환
     */
    public boolean isReusingRefreshToken(String refreshToken) {
        Map<String, String> refreshTokenClaims = parseClaimsRefreshToken(refreshToken);
        String refreshTokenChainId = refreshTokenClaims.get(REFRESH_TOKEN_CHAIN_CLAIM_NAME);
        String refreshTokenId = refreshTokenClaims.get(Claims.ID);

        // redis의 token chain내의 가장 최근에 생성된 refresh token이 없으면 이미 revoke 처리된 상태
        RefreshToken token = refreshTokenRepository.findByChainId(refreshTokenChainId)
                                                   .orElseThrow(() -> new DailyBudgetAppException(ALREADY_LOGGED_OUT));

        // 일치하지 않으면 토큰 재발급 요청시 사용한 RT보다 나중에 만들어진 RT가 존재
        // 결국 입력된 RT는 재사용된 토큰
        return !refreshTokenId.equals(token.getTokenId());
    }

    public void revokeRefreshTokenChain(String refreshToken) {
        String refreshTokenChainId = parseClaimsRefreshToken(refreshToken).get(REFRESH_TOKEN_CHAIN_CLAIM_NAME);
        RefreshToken token = refreshTokenRepository.findByChainId(refreshTokenChainId)
                                                   .orElseThrow(() -> new DailyBudgetAppException(ALREADY_LOGGED_OUT));

        refreshTokenRepository.delete(token);
    }

}