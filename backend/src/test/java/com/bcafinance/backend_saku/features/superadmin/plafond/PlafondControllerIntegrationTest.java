package com.bcafinance.backend_saku.features.superadmin.plafond;

import com.bcafinance.backend_saku.core.exception.GlobalExceptionHandler;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PlafondControllerIntegrationTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PlafondService plafondService;

    @InjectMocks
    private PlafondController plafondController;

    private UUID testPlafondId;
    private PlafondResponse testPlafondResponse;

    @BeforeEach
    void setUp() {
        testPlafondId = UUID.randomUUID();

        testPlafondResponse = new PlafondResponse();
        testPlafondResponse.setId(testPlafondId);
        testPlafondResponse.setNama("Plafond Silver");
        testPlafondResponse.setMinPendapatan(new BigDecimal("3000000.00"));
        testPlafondResponse.setPlafondMaksimal(new BigDecimal("10000000.00"));
        testPlafondResponse.setMinPlafond(new BigDecimal("1000000.00"));
        testPlafondResponse.setMaxPlafond(new BigDecimal("10000000.00"));
        testPlafondResponse.setMinSkor(50);
        testPlafondResponse.setMaxSkor(74);
        testPlafondResponse.setBunga(new BigDecimal("0.05"));
        testPlafondResponse.setBiayaAdmin(new BigDecimal("25000.00"));
        testPlafondResponse.setStatus(true);

        mockMvc = MockMvcBuilders.standaloneSetup(plafondController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/superadmin/plafond - should return list of plafonds")
    void findAll_ShouldReturnList() throws Exception {
        when(plafondService.findAll()).thenReturn(List.of(testPlafondResponse));

        mockMvc.perform(get("/api/superadmin/plafond"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nama").value("Plafond Silver"));
    }

    @Test
    @DisplayName("GET /api/superadmin/plafond/{id} - should return single plafond")
    void findById_ShouldReturnPlafond() throws Exception {
        when(plafondService.findById(testPlafondId)).thenReturn(testPlafondResponse);

        mockMvc.perform(get("/api/superadmin/plafond/{id}", testPlafondId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(testPlafondId.toString()))
                .andExpect(jsonPath("$.data.nama").value("Plafond Silver"));
    }

    @Test
    @DisplayName("POST /api/superadmin/plafond - should create new plafond")
    void create_ShouldCreatePlafond() throws Exception {
        PlafondRequest request = new PlafondRequest();
        request.setNama("Plafond Platinum");
        request.setMinPendapatan(new BigDecimal("10000000.00"));
        request.setPlafondMaksimal(new BigDecimal("50000000.00"));
        request.setMinPlafond(new BigDecimal("5000000.00"));
        request.setMaxPlafond(new BigDecimal("50000000.00"));
        request.setMinSkor(75);
        request.setMaxSkor(100);
        request.setBunga(new BigDecimal("0.03"));
        request.setBiayaAdmin(new BigDecimal("100000.00"));
        request.setStatus(true);

        when(plafondService.create(any(PlafondRequest.class))).thenReturn(testPlafondResponse);

        mockMvc.perform(post("/api/superadmin/plafond")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.nama").value("Plafond Silver"));
    }

    @Test
    @DisplayName("PUT /api/superadmin/plafond/{id} - should update existing plafond")
    void update_ShouldUpdatePlafond() throws Exception {
        PlafondRequest request = new PlafondRequest();
        request.setNama("Plafond Silver Updated");
        request.setMinPendapatan(new BigDecimal("3500000.00"));
        request.setPlafondMaksimal(new BigDecimal("12000000.00"));
        request.setMinPlafond(new BigDecimal("1000000.00"));
        request.setMaxPlafond(new BigDecimal("12000000.00"));
        request.setMinSkor(50);
        request.setMaxSkor(74);
        request.setBunga(new BigDecimal("0.05"));
        request.setBiayaAdmin(new BigDecimal("25000.00"));
        request.setStatus(true);

        testPlafondResponse.setNama("Plafond Silver Updated");
        when(plafondService.update(eq(testPlafondId), any(PlafondRequest.class))).thenReturn(testPlafondResponse);

        mockMvc.perform(put("/api/superadmin/plafond/{id}", testPlafondId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nama").value("Plafond Silver Updated"));
    }

    @Test
    @DisplayName("DELETE /api/superadmin/plafond/{id} - should delete plafond")
    void delete_ShouldDeletePlafond() throws Exception {
        doNothing().when(plafondService).delete(testPlafondId);

        mockMvc.perform(delete("/api/superadmin/plafond/{id}", testPlafondId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }
}
