package com.mojh.daliybudget.auth.filter;

import com.mojh.daliybudget.auth.service.JwtService;
import com.mojh.daliybudget.auth.service.UserDetailsServiceImpl;
import com.mojh.daliybudget.common.exception.DailyBudgetAppException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.mojh.daliybudget.auth.SecurityConstants.ACCOUNT_ID_CLAIM_NAME;
import static com.mojh.daliybudget.auth.SecurityConstants.AUTH_EXCEPTION_INFO;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtService = jwtService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String accessToken = jwtService.extractAccessTokenFrom(request.getHeader(HttpHeaders.AUTHORIZATION));
            jwtService.validateAccessToken(accessToken);
            String accountId = jwtService.parseClaimAccessToken(accessToken, ACCOUNT_ID_CLAIM_NAME);

            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(accountId);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (DailyBudgetAppException ex) {
            request.setAttribute(AUTH_EXCEPTION_INFO, ex);
        }

        filterChain.doFilter(request, response);
    }

}
