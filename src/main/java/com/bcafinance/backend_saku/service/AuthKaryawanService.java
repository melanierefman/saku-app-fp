package com.bcafinance.backend_saku.service;

import com.bcafinance.backend_saku.dto.*;
import com.bcafinance.backend_saku.entity.Karyawan;
import com.bcafinance.backend_saku.repository.KaryawanRepository;
import com.bcafinance.backend_saku.security.AppUser;
import com.bcafinance.backend_saku.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthKaryawanService {

    private final KaryawanRepository karyawanRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    //Login
    public AuthResponse login(AuthRequest request) {

        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getIdentifier(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(
                    "Username/email atau password salah"
            );
        }

        AppUser user = (AppUser) authentication.getPrincipal();

        String token = jwtService.issue(
                user,
                Instant.now()
        );

        AuthResponse response = new AuthResponse();

        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setToken(token);

        return response;
    }

    //Register
//    @Transactional
//    public KaryawanResponse register(KaryawanRequest request) {
//
//        Date now = new Date();
//
//        Karyawan karyawan = new Karyawan();
//        karyawan.setNama(request.getNama());
//        karyawan.setStatus(request.getStatus());
//        karyawan.setEmail(request.getEmail());
//        karyawan.setUsername(request.getUsername());
//        karyawan.setPassword(passwordEncoder.encode(request.getPassword()));
//        karyawan.setCreatedDate(now);
//        karyawan.setUpdatedDate(now);
//
//        Karyawan saved = karyawanRepository.save(karyawan);
//
//        return toResponseRegister(saved);
//    }
//
//    private KaryawanResponse toResponseRegister(Karyawan karyawan) {
//
//        KaryawanResponse response = new KaryawanResponse();
//        response.setId(karyawan.getId());
//        response.setNama(karyawan.getNama());
//        response.setStatus(karyawan.getStatus());
//        response.setEmail(karyawan.getEmail());
//        response.setUsername(karyawan.getUsername());
//
//        return response;
//    }
}
