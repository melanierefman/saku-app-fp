package com.bcafinance.backend_saku.features.customer.controller;

import com.bcafinance.backend_saku.core.exception.GlobalExceptionHandler;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep1Request;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStepResponse;
import com.bcafinance.backend_saku.features.customer.service.RegisterService;
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
class RegisterControllerIntegrationTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RegisterService registerService;

    @InjectMocks
    private RegisterController registerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(registerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/customer/register/step1: Berhasil mendaftarkan step 1 dan mengembalikan HTTP 200")
    void testRegisterStep1Success() throws Exception {
        UUID customerId = UUID.randomUUID();
        RegisterStepResponse mockResponse = new RegisterStepResponse(
                customerId,
                2,
                "Registrasi tahap 1 berhasil."
        );

        when(registerService.registerStep1(any(RegisterStep1Request.class))).thenReturn(mockResponse);

        RegisterStep1Request req = new RegisterStep1Request(
                "customer@example.com",
                "testuser",
                "081234567890",
                "password123",
                "password123"
        );

        mockMvc.perform(post("/api/auth/customer/register/step1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.data.step").value(2));
    }
}
