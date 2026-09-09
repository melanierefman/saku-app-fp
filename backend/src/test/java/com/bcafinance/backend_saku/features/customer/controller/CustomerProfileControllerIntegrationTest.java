package com.bcafinance.backend_saku.features.customer.controller;

import com.bcafinance.backend_saku.core.exception.GlobalExceptionHandler;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.customer.dto.*;
import com.bcafinance.backend_saku.features.customer.service.CustomerProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomerProfileControllerIntegrationTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CustomerProfileService customerProfileService;

    @InjectMocks
    private CustomerProfileController customerProfileController;

    private UUID testCustomerId;
    private AppUser mockCustomerUser;
    private CustomerProfileResponse testProfileResponse;

    @BeforeEach
    void setUp() {
        testCustomerId = UUID.randomUUID();
        mockCustomerUser = new AppUser(testCustomerId, "cust@example.com", "customer1", "pass", "CUSTOMER", "CUSTOMER", List.of());

        testProfileResponse = CustomerProfileResponse.builder()
                .id(testCustomerId)
                .nama("Melanie Sabrina")
                .nik("3201234567890001")
                .email("cust@example.com")
                .noHp("081234567890")
                .namaBank("BCA")
                .noRekening("1234567890")
                .namaRekening("Melanie Sabrina")
                .statusVerifikasi("VERIFIED")
                .totalPlafond(new BigDecimal("20000000.00"))
                .availablePlafond(new BigDecimal("15000000.00"))
                .build();

        HandlerMethodArgumentResolver principalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class)
                        && parameter.getParameterType().equals(AppUser.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return mockCustomerUser;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(customerProfileController)
                .setCustomArgumentResolvers(principalResolver)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/customer/profile - should return customer profile")
    void getProfile_ShouldReturnProfile() throws Exception {
        when(customerProfileService.getProfile(testCustomerId)).thenReturn(testProfileResponse);

        mockMvc.perform(get("/api/customer/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nama").value("Melanie Sabrina"))
                .andExpect(jsonPath("$.data.nik").value("3201234567890001"))
                .andExpect(jsonPath("$.data.statusVerifikasi").value("VERIFIED"));
    }

    @Test
    @DisplayName("PUT /api/customer/profile/rekening - should update bank account info")
    void updateRekening_ShouldUpdateBankInfo() throws Exception {
        UpdateRekeningRequest request = new UpdateRekeningRequest();
        request.setNamaBank("BCA");
        request.setNoRekening("9876543210");
        request.setNamaRekening("Melanie S");

        testProfileResponse.setNoRekening("9876543210");
        when(customerProfileService.updateRekening(eq(testCustomerId), any(UpdateRekeningRequest.class)))
                .thenReturn(testProfileResponse);

        mockMvc.perform(put("/api/customer/profile/rekening")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.noRekening").value("9876543210"));
    }

    @Test
    @DisplayName("PUT /api/customer/profile/change-password - should change password successfully")
    void changePassword_ShouldChangePassword() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("OldPass123!", "NewPass123!", "NewPass123!");

        doNothing().when(customerProfileService).changePassword(eq(testCustomerId), any(ChangePasswordRequest.class));

        mockMvc.perform(put("/api/customer/profile/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Password berhasil diubah"));
    }
}
