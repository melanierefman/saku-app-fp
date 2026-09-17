package com.bcafinance.backend_saku.features.backoffice.controller;

import com.bcafinance.backend_saku.core.dto.PageResponse;
import com.bcafinance.backend_saku.core.exception.GlobalExceptionHandler;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.backoffice.dto.*;
import com.bcafinance.backend_saku.features.backoffice.service.VerifikasiCustomerService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VerifikasiCustomerControllerIntegrationTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private VerifikasiCustomerService verifikasiService;

    @InjectMocks
    private VerifikasiCustomerController verifikasiCustomerController;

    private UUID testKaryawanId;
    private AppUser mockBackofficeUser;

    @BeforeEach
    void setUp() {
        testKaryawanId = UUID.randomUUID();
        mockBackofficeUser = new AppUser(testKaryawanId, "bo@bca.co.id", "bo_user", "pass", "BACKOFFICE", "KARYAWAN", List.of());

        HandlerMethodArgumentResolver principalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class)
                        && parameter.getParameterType().equals(AppUser.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return mockBackofficeUser;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(verifikasiCustomerController)
                .setCustomArgumentResolvers(principalResolver)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/backoffice/verifikasi-customer - should return paginated list")
    void findAll_ShouldReturnPaginatedList() throws Exception {
        VerifikasiCustomerItemResponse item = VerifikasiCustomerItemResponse.builder()
                .customerId(UUID.randomUUID())
                .namaCustomer("Budi Santoso")
                .statusVerifikasi("PENDING")
                .build();

        PageResponse<VerifikasiCustomerItemResponse> pageResponse = PageResponse.ofList(List.of(item), 0, 10);

        when(verifikasiService.findAllPaginated(anyInt(), anyInt(), any(), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/backoffice/verifikasi-customer")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].namaCustomer").value("Budi Santoso"));
    }

    @Test
    @DisplayName("GET /api/backoffice/verifikasi-customer/pending - should return pending list")
    void findPending_ShouldReturnList() throws Exception {
        PendingCustomerResponse pending = PendingCustomerResponse.builder()
                .id(UUID.randomUUID())
                .nama("Siti Aminah")
                .statusVerifikasi("PENDING")
                .build();

        when(verifikasiService.findPending()).thenReturn(List.of(pending));

        mockMvc.perform(get("/api/backoffice/verifikasi-customer/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nama").value("Siti Aminah"));
    }

    @Test
    @DisplayName("GET /api/backoffice/verifikasi-customer/{customerId} - should return detail")
    void getDetail_ShouldReturnDetail() throws Exception {
        UUID customerId = UUID.randomUUID();
        VerifikasiCustomerDetailResponse detail = VerifikasiCustomerDetailResponse.builder()
                .customerId(customerId)
                .namaLengkap("Budi Santoso")
                .statusVerifikasi("PENDING")
                .build();

        when(verifikasiService.getDetail(customerId)).thenReturn(detail);

        mockMvc.perform(get("/api/backoffice/verifikasi-customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.namaLengkap").value("Budi Santoso"));
    }

    @Test
    @DisplayName("PUT /api/backoffice/verifikasi-customer/{customerId} - should verify customer")
    void verify_ShouldReturnVerifikasiResponse() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID plafondId = UUID.randomUUID();

        VerifikasiCustomerRequest request = new VerifikasiCustomerRequest();
        request.setStatusVerifikasi("APPROVED");
        request.setCatatanVerifikasi("Semua data KTP dan KK cocok");

        VerifikasiCustomerResponse response = new VerifikasiCustomerResponse(
                customerId, "APPROVED", "Semua data KTP dan KK cocok", 80,
                "APPROVED", plafondId, new BigDecimal("20000000.00"), true);

        when(verifikasiService.verify(eq(customerId), eq(testKaryawanId), any(VerifikasiCustomerRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/backoffice/verifikasi-customer/{customerId}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.statusVerifikasi").value("APPROVED"));
    }
}
