package com.bcafinance.backend_saku.features.auth.controller;

import com.bcafinance.backend_saku.core.exception.GlobalExceptionHandler;
import com.bcafinance.backend_saku.features.auth.dto.AuthRequest;
import com.bcafinance.backend_saku.features.auth.dto.AuthResponse;
import com.bcafinance.backend_saku.features.auth.dto.ForgotPasswordRequest;
import com.bcafinance.backend_saku.features.auth.dto.SendOtpResponse;
import com.bcafinance.backend_saku.features.auth.dto.UserLoginProfile;
import com.bcafinance.backend_saku.features.auth.service.AuthCustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthCustomerControllerIntegrationTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthCustomerService authCustomerService;

    @InjectMocks
    private AuthCustomerController authCustomerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authCustomerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/customer/login: Berhasil login dan mengembalikan 200/201 dengan payload ApiResponse")
    void testLoginEndpointSuccess() throws Exception {
        AuthResponse mockResponse = AuthResponse.builder()
                .tokenType("Bearer")
                .accessToken("mock-token")
                .refreshToken("mock-refresh")
                .user(UserLoginProfile.builder()
                        .id(UUID.randomUUID())
                        .username("testcust")
                        .role("CUSTOMER")
                        .build())
                .build();

        when(authCustomerService.login(any(AuthRequest.class))).thenReturn(mockResponse);

        AuthRequest request = new AuthRequest("testcust", "password123");

        mockMvc.perform(post("/api/auth/customer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.accessToken").value("mock-token"))
                .andExpect(jsonPath("$.data.user.username").value("testcust"));
    }

    @Test
    @DisplayName("POST /api/auth/customer/forgot-password: Berhasil mengirim request OTP")
    void testForgotPasswordEndpoint() throws Exception {
        SendOtpResponse otpResponse = SendOtpResponse.builder()
                .message("OTP sent")
                .email("c***r@example.com")
                .build();

        when(authCustomerService.forgotPassword(any(ForgotPasswordRequest.class))).thenReturn(otpResponse);

        ForgotPasswordRequest request = new ForgotPasswordRequest("customer@example.com");

        mockMvc.perform(post("/api/auth/customer/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("OTP sent"));
    }

    @Test
    @DisplayName("POST /api/auth/customer/logout: Berhasil mengembalikan pesan sukses")
    void testLogoutEndpoint() throws Exception {
        mockMvc.perform(post("/api/auth/customer/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Berhasil logout"));
    }
}
