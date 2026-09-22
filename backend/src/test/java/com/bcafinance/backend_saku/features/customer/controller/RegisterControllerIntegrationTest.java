package com.bcafinance.backend_saku.features.customer.controller;

import com.bcafinance.backend_saku.core.exception.GlobalExceptionHandler;
import com.bcafinance.backend_saku.features.customer.dto.AlamatCustomer;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep1KtpRequest;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep2PersonalRequest;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep5CompleteRequest;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStepResponse;
import com.bcafinance.backend_saku.features.customer.service.RegisterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.UUID;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    @DisplayName("POST /step1-ktp/{customerId} (JSON): Berhasil simpan data e-KTP")
    void testRegisterStep1KtpJsonSuccess() throws Exception {
        UUID customerId = UUID.randomUUID();
        RegisterStepResponse mockResponse = new RegisterStepResponse(customerId, 1, "Data e-KTP berhasil disimpan");

        when(registerService.registerStep1Ktp(eq(customerId), any(), any(RegisterStep1KtpRequest.class)))
                .thenReturn(mockResponse);

        AlamatCustomer alamat = new AlamatCustomer(
                "Jl. Sudirman No 1", "001", "002", "Menteng", "Menteng", "Jakarta Pusat", "DKI Jakarta", "10310"
        );
        RegisterStep1KtpRequest req = new RegisterStep1KtpRequest("3171012345678901", "Budi Santoso", alamat);

        mockMvc.perform(post("/api/auth/customer/register/step1-ktp/{customerId}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.step").value(1))
                .andExpect(jsonPath("$.data.customerId").value(customerId.toString()));
    }

    @Test
    @DisplayName("PUT /step2-personal/{customerId}: Berhasil simpan data pribadi")
    void testRegisterStep2PersonalSuccess() throws Exception {
        UUID customerId = UUID.randomUUID();
        RegisterStepResponse mockResponse = new RegisterStepResponse(customerId, 2, "Data pribadi tersimpan");

        when(registerService.registerStep2Personal(eq(customerId), any(RegisterStep2PersonalRequest.class)))
                .thenReturn(mockResponse);

        RegisterStep2PersonalRequest req = new RegisterStep2PersonalRequest(
                "081234567890", "Budi Santoso", "BCA", "1234567890",
                "IT", "PT SAKU", "TETAP",
                new BigDecimal("10000000"), 24, new BigDecimal("500000"),
                null, true, "Ibu Siti"
        );

        mockMvc.perform(put("/api/auth/customer/register/step2-personal/{customerId}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.step").value(2));
    }

    @Test
    @DisplayName("POST /step5-complete/{customerId}: Berhasil buat kredensial password")
    void testRegisterStep5CompleteSuccess() throws Exception {
        UUID customerId = UUID.randomUUID();
        RegisterStepResponse mockResponse = new RegisterStepResponse(customerId, 5, "Pendaftaran berhasil");

        when(registerService.registerStep5Complete(eq(customerId), any(RegisterStep5CompleteRequest.class)))
                .thenReturn(mockResponse);

        RegisterStep5CompleteRequest req = new RegisterStep5CompleteRequest("password123", "password123");

        mockMvc.perform(post("/api/auth/customer/register/step5-complete/{customerId}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.step").value(5));
    }
}
