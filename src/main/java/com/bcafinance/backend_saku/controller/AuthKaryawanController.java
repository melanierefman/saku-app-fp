package com.bcafinance.backend_saku.controller;

import com.bcafinance.backend_saku.dto.ApiResponse;
import com.bcafinance.backend_saku.dto.AuthRequest;
import com.bcafinance.backend_saku.dto.AuthResponse;
import com.bcafinance.backend_saku.service.AuthKaryawanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/karyawan")
@RequiredArgsConstructor
public class AuthKaryawanController {

    private final AuthKaryawanService authKaryawanService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody AuthRequest request) {
        return ApiResponse.created(authKaryawanService.login(request));
    }
}
