package com.bcafinance.backend_saku.features.auth.service;

import com.bcafinance.backend_saku.features.auth.dto.AuthRequest;
import com.bcafinance.backend_saku.features.auth.dto.AuthResponse;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.core.security.JwtService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthCustomerService {

    private final JwtService jwtService;
    private final AuthenticationManager customerAuthenticationManager;

    public AuthCustomerService(
            JwtService jwtService,
            @Qualifier("customerAuthenticationManager") AuthenticationManager customerAuthenticationManager) {
        this.jwtService = jwtService;
        this.customerAuthenticationManager = customerAuthenticationManager;
    }

    // Login
    public AuthResponse login(AuthRequest request) {

        System.out.println(">>> CUSTOMER SERVICE");
        System.out.println(">>> Manager = " + customerAuthenticationManager);

        Authentication authentication;

        try {
            authentication = customerAuthenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getIdentifier(),
                            request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(
                    "Username/email atau password salah");
        }

        AppUser user = (AppUser) authentication.getPrincipal();

        String token = jwtService.issue(
                user,
                Instant.now());

        AuthResponse response = new AuthResponse();

        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setToken(token);

        return response;
    }
}
