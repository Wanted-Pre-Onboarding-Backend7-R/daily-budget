package com.mojh.dailybudget.auth.controller;

import com.mojh.dailybudget.auth.dto.LoginRequest;
import com.mojh.dailybudget.auth.dto.LogoutRequest;
import com.mojh.dailybudget.auth.dto.TokenReissueRequest;
import com.mojh.dailybudget.auth.dto.TokensResponse;
import com.mojh.dailybudget.auth.service.AuthService;
import com.mojh.dailybudget.common.web.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody final LoginRequest loginRequest) {
        return ApiResponse.succeed(authService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout(@RequestHeader(AUTHORIZATION) String accessToken,
                                 @Valid @RequestBody final LogoutRequest logoutRequest) {
        authService.logout(accessToken, logoutRequest.getRefreshToken());
        return ApiResponse.succeed();
    }

    @PostMapping("/reissue")
    public ApiResponse<TokensResponse> reissueAccessToken(@RequestHeader(AUTHORIZATION) String accessToken,
                                                          @Valid @RequestBody final TokenReissueRequest reissueTokenRequest) {
        return ApiResponse.succeed(authService.reissueTokens(accessToken, reissueTokenRequest.getRefreshToken()));
    }

}
