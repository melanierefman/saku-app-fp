package com.bcafinance.backend_saku.features.superadmin.cabang;

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
class CabangControllerIntegrationTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CabangService cabangService;

    @InjectMocks
    private CabangController cabangController;

    private UUID testCabangId;
    private CabangResponse testCabangResponse;

    @BeforeEach
    void setUp() {
        testCabangId = UUID.randomUUID();

        testCabangResponse = new CabangResponse();
        testCabangResponse.setId(testCabangId);
        testCabangResponse.setNama("KC Jakarta Pusat");
        testCabangResponse.setKota("Jakarta Pusat");
        testCabangResponse.setIsDefault(false);
        testCabangResponse.setStatus(true);

        mockMvc = MockMvcBuilders.standaloneSetup(cabangController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/superadmin/cabang - should return list of all branches")
    void findAll_ShouldReturnList() throws Exception {
        when(cabangService.findAll()).thenReturn(List.of(testCabangResponse));

        mockMvc.perform(get("/api/superadmin/cabang"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nama").value("KC Jakarta Pusat"))
                .andExpect(jsonPath("$.data[0].kota").value("Jakarta Pusat"));
    }

    @Test
    @DisplayName("GET /api/superadmin/cabang/{id} - should return single branch")
    void findById_ShouldReturnBranch() throws Exception {
        when(cabangService.findById(testCabangId)).thenReturn(testCabangResponse);

        mockMvc.perform(get("/api/superadmin/cabang/{id}", testCabangId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(testCabangId.toString()))
                .andExpect(jsonPath("$.data.nama").value("KC Jakarta Pusat"));
    }

    @Test
    @DisplayName("POST /api/superadmin/cabang - should create new branch")
    void create_ShouldCreateBranch() throws Exception {
        CabangRequest request = new CabangRequest();
        request.setNama("KC Surabaya Gubeng");
        request.setKota("Surabaya");
        request.setIsDefault(false);
        request.setStatus(true);

        CabangResponse created = new CabangResponse();
        created.setId(UUID.randomUUID());
        created.setNama("KC Surabaya Gubeng");
        created.setKota("Surabaya");
        created.setIsDefault(false);
        created.setStatus(true);

        when(cabangService.create(any(CabangRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/superadmin/cabang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.nama").value("KC Surabaya Gubeng"));
    }

    @Test
    @DisplayName("PUT /api/superadmin/cabang/{id} - should update existing branch")
    void update_ShouldUpdateBranch() throws Exception {
        CabangRequest request = new CabangRequest();
        request.setNama("KC Jakarta Pusat Updated");
        request.setKota("Jakarta Pusat");
        request.setIsDefault(false);
        request.setStatus(true);

        testCabangResponse.setNama("KC Jakarta Pusat Updated");
        when(cabangService.update(eq(testCabangId), any(CabangRequest.class))).thenReturn(testCabangResponse);

        mockMvc.perform(put("/api/superadmin/cabang/{id}", testCabangId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nama").value("KC Jakarta Pusat Updated"));
    }

    @Test
    @DisplayName("DELETE /api/superadmin/cabang/{id} - should delete branch")
    void delete_ShouldDeleteBranch() throws Exception {
        doNothing().when(cabangService).delete(testCabangId);

        mockMvc.perform(delete("/api/superadmin/cabang/{id}", testCabangId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }
}
