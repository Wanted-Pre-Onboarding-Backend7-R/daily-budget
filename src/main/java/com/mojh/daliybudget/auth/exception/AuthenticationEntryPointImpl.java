package com.mojh.daliybudget.auth.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojh.daliybudget.common.exception.DailyBudgetAppException;
import com.mojh.daliybudget.common.web.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.mojh.daliybudget.auth.SecurityConstants.AUTH_EXCEPTION_INFO;

@Slf4j
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    public AuthenticationEntryPointImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        DailyBudgetAppException ex = (DailyBudgetAppException) request.getAttribute(AUTH_EXCEPTION_INFO);
        String responseBody;

        if (ex != null) {
            log.error(ex.getClass().getSimpleName(), ex);
            responseBody = objectMapper.writeValueAsString(ApiResponse.error(ex));
        } else {
            log.error(authException.getClass().getSimpleName(), authException);
            responseBody = objectMapper.writeValueAsString(ApiResponse.error(authException.getMessage()));
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().print(responseBody);
    }
    
}