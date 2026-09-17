package com.bcafinance.backend_saku.features.branchmanager.controller;

import com.bcafinance.backend_saku.core.dto.PageResponse;
import com.bcafinance.backend_saku.core.exception.GlobalExceptionHandler;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.branchmanager.dto.BranchManagerPengajuanDetailResponse;
import com.bcafinance.backend_saku.features.branchmanager.dto.BranchManagerPengajuanItemResponse;
import com.bcafinance.backend_saku.features.branchmanager.dto.PersetujuanPinjamanRequest;
import com.bcafinance.backend_saku.features.branchmanager.dto.PersetujuanPinjamanResponse;
import com.bcafinance.backend_saku.features.branchmanager.service.BranchManagerPersetujuanService;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BranchManagerPersetujuanControllerIntegrationTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private BranchManagerPersetujuanService branchManagerPersetujuanService;

    @InjectMocks
    private BranchManagerPersetujuanController branchManagerPersetujuanController;

    private UUID testKaryawanId;
    private AppUser mockBmUser;

    @BeforeEach
    void setUp() {
        testKaryawanId = UUID.randomUUID();
        mockBmUser = new AppUser(testKaryawanId, "bm@bca.co.id", "bm_user", "pass", "BRANCH_MANAGER", "KARYAWAN", List.of());

        HandlerMethodArgumentResolver principalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class)
                        && parameter.getParameterType().equals(AppUser.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return mockBmUser;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(branchManagerPersetujuanController)
                .setCustomArgumentResolvers(principalResolver)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/branch-manager/persetujuan - should return paginated list")
    void findAll_ShouldReturnPaginatedList() throws Exception {
        BranchManagerPengajuanItemResponse item = BranchManagerPengajuanItemResponse.builder()
                .pengajuanId(UUID.randomUUID())
                .noPengajuan("PJ-BM-001")
                .customer("Dewi Lestari")
                .status("MENUNGGU_PERSETUJUAN")
                .build();

        PageResponse<BranchManagerPengajuanItemResponse> pageResponse = PageResponse.ofList(List.of(item), 0, 10);

        when(branchManagerPersetujuanService.findAllPaginated(anyInt(), anyInt(), any(), any(), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/branch-manager/persetujuan")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].noPengajuan").value("PJ-BM-001"));
    }

    @Test
    @DisplayName("GET /api/branch-manager/persetujuan/{pengajuanId} - should return detail")
    void getDetail_ShouldReturnDetail() throws Exception {
        UUID pengajuanId = UUID.randomUUID();
        BranchManagerPengajuanDetailResponse detail = BranchManagerPengajuanDetailResponse.builder()
                .pengajuanId(pengajuanId)
                .nomorPengajuan("PJ-BM-001")
                .namaLengkap("Dewi Lestari")
                .statusPengajuan("MENUNGGU_PERSETUJUAN")
                .build();

        when(branchManagerPersetujuanService.getDetail(pengajuanId)).thenReturn(detail);

        mockMvc.perform(get("/api/branch-manager/persetujuan/{pengajuanId}", pengajuanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nomorPengajuan").value("PJ-BM-001"));
    }

    @Test
    @DisplayName("PUT /api/branch-manager/persetujuan/{pengajuanId} - should approve application")
    void persetujuan_ShouldReturnPersetujuanResponse() throws Exception {
        UUID pengajuanId = UUID.randomUUID();
        PersetujuanPinjamanRequest request = new PersetujuanPinjamanRequest();
        request.setHasilPersetujuan("DISETUJUI");
        request.setCatatan("Persetujuan Branch Manager valid");

        PersetujuanPinjamanResponse response = PersetujuanPinjamanResponse.builder()
                .id(UUID.randomUUID())
                .pengajuanId(pengajuanId)
                .statusPengajuan("MENUNGGU_PENCAIRAN")
                .hasilPersetujuan("DISETUJUI")
                .catatan("Persetujuan Branch Manager valid")
                .build();

        when(branchManagerPersetujuanService.persetujuan(eq(pengajuanId), eq(testKaryawanId), any(PersetujuanPinjamanRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/branch-manager/persetujuan/{pengajuanId}", pengajuanId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.statusPengajuan").value("MENUNGGU_PENCAIRAN"))
                .andExpect(jsonPath("$.data.hasilPersetujuan").value("DISETUJUI"));
    }
}
