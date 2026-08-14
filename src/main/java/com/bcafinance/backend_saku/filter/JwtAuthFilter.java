package com.bcafinance.backend_saku.filter;

import com.bcafinance.backend_saku.exception.UnauthorizedHandler;
import com.bcafinance.backend_saku.security.AppUser;
import com.bcafinance.backend_saku.security.AppUserDetailService;
import com.bcafinance.backend_saku.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String PREFIX = "Bearer ";
    private static final String PESAN_TOKEN_TIDAK_VALID = "Token tidak valid";

    private final JwtService jwtService;
    private final AppUserDetailService userDetailsService;
    private final UnauthorizedHandler unauthorizedHandler;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtService.parse(header.substring(PREFIX.length()));
//            AppUser user = userDetailsService.loadUserByUsername(claims.getSubject());
            UserDetails user = userDetailsService.loadUserByUsername(claims.getSubject());
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException | UsernameNotFoundException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();
            unauthorizedHandler.response(response, PESAN_TOKEN_TIDAK_VALID);
            return;
        }

        chain.doFilter(request, response);
    }
}
