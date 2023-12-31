package com.mojh.dailybudget.auth.service;

import com.mojh.dailybudget.auth.dto.LoginRequest;
import com.mojh.dailybudget.auth.dto.TokensResponse;
import com.mojh.dailybudget.common.exception.DailyBudgetAppException;
import com.mojh.dailybudget.member.domain.Member;
import com.mojh.dailybudget.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mojh.dailybudget.common.exception.ErrorCode.INVALID_TOKEN;
import static com.mojh.dailybudget.common.exception.ErrorCode.LOGIN_FAILED;

@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public TokensResponse login(LoginRequest loginRequest) {
        Member member = memberRepository.findByAccountId(loginRequest.getAccountId())
                                        .orElseThrow(() -> new DailyBudgetAppException(LOGIN_FAILED));

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new DailyBudgetAppException(LOGIN_FAILED);
        }

        String accessToken = jwtService.generateAccessToken(member.getAccountId());
        String refreshToken = jwtService.generateRefreshToken(member.getAccountId());

        jwtService.saveRefreshToken(refreshToken);

        return TokensResponse.builder()
                             .accessToken(accessToken)
                             .refreshToken(refreshToken)
                             .build();
    }

    @Transactional
    public void logout(String accessTokenHeader, String refreshToken) {
        String accessToken = jwtService.extractAccessTokenFrom(accessTokenHeader);

        jwtService.validateTokensAccount(accessToken, refreshToken);
        jwtService.validateRefreshToken(refreshToken);
        jwtService.revokeRefreshTokenChain(refreshToken);
    }

    @Transactional
    public TokensResponse reissueTokens(String accessTokenHeader, String refreshToken) {
        String accessToken = jwtService.extractAccessTokenFrom(accessTokenHeader);

        String accountId = jwtService.parseRefreshTokenSubject(refreshToken);

        jwtService.validateTokensAccount(accessToken, refreshToken);
        jwtService.validateRefreshToken(refreshToken);

        // refresh token 재사용 감지
        if (jwtService.isReusingRefreshToken(refreshToken)) {
            jwtService.revokeRefreshTokenChain(refreshToken);
            throw new DailyBudgetAppException(INVALID_TOKEN);
        }

        // 재발급
        String reissuedAccessToken = jwtService.generateAccessToken(accountId);
        String reissuedRefreshToken = jwtService.generateRefreshTokenInSameChain(accountId, refreshToken);
        jwtService.saveRefreshToken(reissuedRefreshToken);

        return TokensResponse.builder()
                             .accessToken(reissuedAccessToken)
                             .refreshToken(reissuedRefreshToken)
                             .build();
    }
    
}

