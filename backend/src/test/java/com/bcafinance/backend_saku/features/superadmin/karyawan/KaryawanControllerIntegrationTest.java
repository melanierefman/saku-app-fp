package com.bcafinance.backend_saku.features.superadmin.karyawan;

import com.bcafinance.backend_saku.core.dto.PageResponse;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class KaryawanControllerIntegrationTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private KaryawanService karyawanService;

    @InjectMocks
    private KaryawanController karyawanController;

    private UUID testKaryawanId;
    private KaryawanResponse testKaryawanResponse;

    @BeforeEach
    void setUp() {
        testKaryawanId = UUID.randomUUID();

        testKaryawanResponse = new KaryawanResponse();
        testKaryawanResponse.setId(testKaryawanId);
        testKaryawanResponse.setNama("Agus Setiawan");
        testKaryawanResponse.setEmail("agus@bca.co.id");
        testKaryawanResponse.setUsername("agus.setiawan");
        testKaryawanResponse.setRoleNama("MARKETING");
        testKaryawanResponse.setCabangNama("KC Jakarta");
        testKaryawanResponse.setStatus(true);

        mockMvc = MockMvcBuilders.standaloneSetup(karyawanController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/superadmin/karyawan - should return paginated list")
    void findAll_ShouldReturnPaginatedList() throws Exception {
        PageResponse<KaryawanResponse> pageResponse = PageResponse.ofList(List.of(testKaryawanResponse), 0, 10);

        when(karyawanService.findAllPaginated(anyInt(), anyInt(), any(), any(), any(), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/superadmin/karyawan")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].username").value("agus.setiawan"))
                .andExpect(jsonPath("$.data.content[0].nama").value("Agus Setiawan"));
    }

    @Test
    @DisplayName("GET /api/superadmin/karyawan/{id} - should return single employee")
    void findById_ShouldReturnEmployee() throws Exception {
        when(karyawanService.findById(testKaryawanId)).thenReturn(testKaryawanResponse);

        mockMvc.perform(get("/api/superadmin/karyawan/{id}", testKaryawanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(testKaryawanId.toString()))
                .andExpect(jsonPath("$.data.nama").value("Agus Setiawan"));
    }

    @Test
    @DisplayName("POST /api/superadmin/karyawan - should create employee")
    void create_ShouldCreateEmployee() throws Exception {
        KaryawanRequest request = new KaryawanRequest();
        request.setNama("Budi Officer");
        request.setEmail("budi.officer@bca.co.id");
        request.setUsername("budi.officer");
        request.setPassword("Password123!");
        request.setMstRoleId(UUID.randomUUID());
        request.setMstBranchId(UUID.randomUUID());
        request.setStatus(true);

        when(karyawanService.create(any(KaryawanRequest.class))).thenReturn(testKaryawanResponse);

        mockMvc.perform(post("/api/superadmin/karyawan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.username").value("agus.setiawan"));
    }

    @Test
    @DisplayName("PUT /api/superadmin/karyawan/{id} - should update employee")
    void update_ShouldUpdateEmployee() throws Exception {
        KaryawanRequest request = new KaryawanRequest();
        request.setNama("Agus Setiawan Updated");
        request.setEmail("agus.updated@bca.co.id");
        request.setUsername("agus.updated");
        request.setPassword("NewPassword123!");
        request.setMstRoleId(UUID.randomUUID());
        request.setMstBranchId(UUID.randomUUID());
        request.setStatus(true);

        testKaryawanResponse.setNama("Agus Setiawan Updated");
        when(karyawanService.update(eq(testKaryawanId), any(KaryawanRequest.class))).thenReturn(testKaryawanResponse);

        mockMvc.perform(put("/api/superadmin/karyawan/{id}", testKaryawanId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nama").value("Agus Setiawan Updated"));
    }

    @Test
    @DisplayName("DELETE /api/superadmin/karyawan/{id} - should delete employee")
    void delete_ShouldDeleteEmployee() throws Exception {
        doNothing().when(karyawanService).delete(testKaryawanId);

        mockMvc.perform(delete("/api/superadmin/karyawan/{id}", testKaryawanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }
}
