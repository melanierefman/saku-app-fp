package com.bcafinance.backend_saku.features.backoffice.controller;

import com.bcafinance.backend_saku.core.dto.PageResponse;
import com.bcafinance.backend_saku.core.exception.GlobalExceptionHandler;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.backoffice.dto.AngsuranItemResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.PencairanDetailResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.PencairanItemResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.PencairanRequest;
import com.bcafinance.backend_saku.features.backoffice.dto.PencairanResponse;
import com.bcafinance.backend_saku.features.backoffice.service.PencairanService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PencairanControllerIntegrationTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PencairanService pencairanService;

    @InjectMocks
    private PencairanController pencairanController;

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

        mockMvc = MockMvcBuilders.standaloneSetup(pencairanController)
                .setCustomArgumentResolvers(principalResolver)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/backoffice/pencairan - should return paginated list")
    void findAll_ShouldReturnPaginatedList() throws Exception {
        PencairanItemResponse item = PencairanItemResponse.builder()
                .pengajuanId(UUID.randomUUID())
                .noPengajuan("PJ-2026-001")
                .namaCustomer("Ahmad Dahlan")
                .statusPengajuan("MENUNGGU_PENCAIRAN")
                .build();

        PageResponse<PencairanItemResponse> pageResponse = PageResponse.ofList(List.of(item), 0, 10);

        when(pencairanService.findAllPaginated(anyInt(), anyInt(), any(), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/backoffice/pencairan")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].noPengajuan").value("PJ-2026-001"));
    }

    @Test
    @DisplayName("GET /api/backoffice/pencairan/{pengajuanId} - should return detail")
    void getDetail_ShouldReturnDetail() throws Exception {
        UUID pengajuanId = UUID.randomUUID();
        PencairanDetailResponse detail = PencairanDetailResponse.builder()
                .pengajuanId(pengajuanId)
                .nomorPengajuan("PJ-2026-001")
                .namaLengkap("Ahmad Dahlan")
                .statusPengajuan("MENUNGGU_PENCAIRAN")
                .build();

        when(pencairanService.getDetail(pengajuanId)).thenReturn(detail);

        mockMvc.perform(get("/api/backoffice/pencairan/{pengajuanId}", pengajuanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nomorPengajuan").value("PJ-2026-001"));
    }

    @Test
    @DisplayName("POST /api/backoffice/pencairan/{pengajuanId}/cairkan - should process disbursement")
    void cairkan_ShouldProcessDisbursement() throws Exception {
        UUID pengajuanId = UUID.randomUUID();
        PencairanRequest request = new PencairanRequest();
        request.setCatatan("Transfer dana via BCA VA berhasil");

        PencairanResponse response = PencairanResponse.builder()
                .pengajuanId(pengajuanId)
                .statusPengajuan("DICAIRKAN")
                .statusPencairan("BERHASIL")
                .jumlahPencairan(new BigDecimal("10000000.00"))
                .build();

        when(pencairanService.cairkanPinjaman(eq(pengajuanId), eq(testKaryawanId), any(PencairanRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/backoffice/pencairan/{pengajuanId}/cairkan", pengajuanId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.statusPengajuan").value("DICAIRKAN"));
    }

    @Test
    @DisplayName("GET /api/backoffice/pencairan/{pengajuanId}/angsuran - should return repayment schedule")
    void getJadwalAngsuran_ShouldReturnScheduleList() throws Exception {
        UUID pengajuanId = UUID.randomUUID();
        AngsuranItemResponse scheduleItem = AngsuranItemResponse.builder()
                .id(UUID.randomUUID())
                .cicilanKe(1)
                .jumlahAngsuran(new BigDecimal("1000000.00"))
                .jatuhTempo(LocalDate.now().plusMonths(1))
                .statusBayar("BELUM_DIBAYAR")
                .build();

        when(pencairanService.getJadwalAngsuran(pengajuanId)).thenReturn(List.of(scheduleItem));

        mockMvc.perform(get("/api/backoffice/pencairan/{pengajuanId}/angsuran", pengajuanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].cicilanKe").value(1))
                .andExpect(jsonPath("$.data[0].statusBayar").value("BELUM_DIBAYAR"));
    }
}
