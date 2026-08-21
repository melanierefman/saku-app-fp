package com.bcafinance.backend_saku.core.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class SecurityExceptionHandler {

    public void handleAuthenticationException(
            HttpServletResponse response,
            AuthenticationException e
    ) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        response.getWriter().write("""
                {
                    "statusCode": 401,
                    "message": "Unauthorized"
                }
                """);
    }

    public void handleAccessDeniedException(
            HttpServletResponse response,
            AccessDeniedException e
    ) throws IOException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");

        response.getWriter().write("""
                {
                    "statusCode": 403,
                    "message": "Forbidden"
                }
                """);
    }
}
