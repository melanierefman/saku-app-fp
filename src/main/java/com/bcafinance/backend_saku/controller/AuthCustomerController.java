package com.bcafinance.backend_saku.controller;

import com.bcafinance.backend_saku.dto.ApiResponse;
import com.bcafinance.backend_saku.dto.AuthRequest;
import com.bcafinance.backend_saku.dto.AuthResponse;
import com.bcafinance.backend_saku.service.AuthCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/customer")
@RequiredArgsConstructor
public class AuthCustomerController {

    private final AuthCustomerService authCustomerService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody AuthRequest request) {
        return ApiResponse.created(authCustomerService.login(request));
    }
}
