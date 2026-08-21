package com.bcafinance.backend_saku.core.filter;

import com.bcafinance.backend_saku.core.exception.UnauthorizedHandler;
import com.bcafinance.backend_saku.core.security.AppUserDetailService;
import com.bcafinance.backend_saku.core.security.CustomerUserDetailService;
import com.bcafinance.backend_saku.core.security.JwtService;
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
        private final AppUserDetailService appUserDetailService;
        private final CustomerUserDetailService customerUserDetailService;
        private final UnauthorizedHandler unauthorizedHandler;

        @Override
        protected void doFilterInternal(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain chain)
                        throws ServletException, IOException {

                String header = request.getHeader(HttpHeaders.AUTHORIZATION);

                if (header == null || !header.startsWith(PREFIX)) {
                        chain.doFilter(request, response);
                        return;
                }

                try {
                        String token = header.substring(PREFIX.length());

                        Claims claims = jwtService.parse(token);

                        String username = claims.getSubject();
                        String userType = claims.get("tipe", String.class);

                        UserDetails user;

                        if ("CUSTOMER".equalsIgnoreCase(userType)) {
                                user = customerUserDetailService
                                                .loadUserByUsername(username);
                        } else if ("KARYAWAN".equalsIgnoreCase(userType)) {
                                user = appUserDetailService
                                                .loadUserByUsername(username);
                        } else {
                                throw new IllegalArgumentException(
                                                "Tipe user tidak valid");
                        }

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                        user,
                                        null,
                                        user.getAuthorities());

                        SecurityContextHolder
                                        .getContext()
                                        .setAuthentication(authentication);

                } catch (
                                JwtException | UsernameNotFoundException | IllegalArgumentException ex) {

                        SecurityContextHolder.clearContext();

                        unauthorizedHandler.response(
                                        response,
                                        PESAN_TOKEN_TIDAK_VALID);

                        return;
                }

                chain.doFilter(request, response);
        }
}