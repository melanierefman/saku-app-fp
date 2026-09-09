package com.bcafinance.backend_saku.features.marketing.controller;

import com.bcafinance.backend_saku.core.dto.PageResponse;
import com.bcafinance.backend_saku.core.exception.GlobalExceptionHandler;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.marketing.dto.MarketingPengajuanDetailResponse;
import com.bcafinance.backend_saku.features.marketing.dto.MarketingPengajuanItemResponse;
import com.bcafinance.backend_saku.features.marketing.dto.ReviewPengajuanRequest;
import com.bcafinance.backend_saku.features.marketing.dto.ReviewPengajuanResponse;
import com.bcafinance.backend_saku.features.marketing.service.MarketingReviewService;
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
class MarketingPengajuanControllerIntegrationTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MarketingReviewService marketingReviewService;

    @InjectMocks
    private MarketingPengajuanController marketingPengajuanController;

    private UUID testKaryawanId;
    private AppUser mockMarketingUser;

    @BeforeEach
    void setUp() {
        testKaryawanId = UUID.randomUUID();
        mockMarketingUser = new AppUser(testKaryawanId, "marketing@bca.co.id", "marketing1", "pass", "MARKETING", "KARYAWAN", List.of());

        HandlerMethodArgumentResolver principalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class)
                        && parameter.getParameterType().equals(AppUser.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return mockMarketingUser;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(marketingPengajuanController)
                .setCustomArgumentResolvers(principalResolver)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/marketing/pengajuan-pinjaman - should return paginated list of applications")
    void findAll_ShouldReturnPaginatedList() throws Exception {
        MarketingPengajuanItemResponse item = MarketingPengajuanItemResponse.builder()
                .pengajuanId(UUID.randomUUID())
                .noPengajuan("PJ-2026-001")
                .customer("John Doe")
                .jumlah(new BigDecimal("10000000.00"))
                .status("MENUNGGU_REVIEW")
                .build();

        PageResponse<MarketingPengajuanItemResponse> pageResponse = PageResponse.ofList(List.of(item), 0, 10);

        when(marketingReviewService.findAllPaginated(anyInt(), anyInt(), any(), any(), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/marketing/pengajuan-pinjaman")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].noPengajuan").value("PJ-2026-001"))
                .andExpect(jsonPath("$.data.content[0].customer").value("John Doe"));
    }

    @Test
    @DisplayName("GET /api/marketing/pengajuan-pinjaman/{pengajuanId} - should return detail")
    void getDetail_ShouldReturnDetail() throws Exception {
        UUID pengajuanId = UUID.randomUUID();
        MarketingPengajuanDetailResponse detail = MarketingPengajuanDetailResponse.builder()
                .pengajuanId(pengajuanId)
                .nomorPengajuan("PJ-2026-001")
                .namaLengkap("John Doe")
                .nik("3201234567890001")
                .statusPengajuan("MENUNGGU_REVIEW")
                .build();

        when(marketingReviewService.getDetail(pengajuanId)).thenReturn(detail);

        mockMvc.perform(get("/api/marketing/pengajuan-pinjaman/{pengajuanId}", pengajuanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pengajuanId").value(pengajuanId.toString()))
                .andExpect(jsonPath("$.data.nomorPengajuan").value("PJ-2026-001"));
    }

    @Test
    @DisplayName("PUT /api/marketing/pengajuan-pinjaman/{pengajuanId} - should submit review")
    void review_ShouldReturnReviewResponse() throws Exception {
        UUID pengajuanId = UUID.randomUUID();
        ReviewPengajuanRequest request = new ReviewPengajuanRequest();
        request.setHasilReview("DISETUJUI");
        request.setCatatan("Dokumen valid dan lengkap");

        ReviewPengajuanResponse response = ReviewPengajuanResponse.builder()
                .id(UUID.randomUUID())
                .pengajuanId(pengajuanId)
                .statusPengajuan("SELESAI_DIREVIEW")
                .hasilReview("DISETUJUI")
                .catatan("Dokumen valid dan lengkap")
                .build();

        when(marketingReviewService.review(eq(pengajuanId), eq(testKaryawanId), any(ReviewPengajuanRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/marketing/pengajuan-pinjaman/{pengajuanId}", pengajuanId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.statusPengajuan").value("SELESAI_DIREVIEW"))
                .andExpect(jsonPath("$.data.hasilReview").value("DISETUJUI"));
    }
}
