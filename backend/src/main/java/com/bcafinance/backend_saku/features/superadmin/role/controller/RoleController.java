package com.bcafinance.backend_saku.features.superadmin.role.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.superadmin.role.dto.AssignPermissionsRequest;
import com.bcafinance.backend_saku.features.superadmin.role.dto.RoleDetailResponse;
import com.bcafinance.backend_saku.features.superadmin.role.dto.RoleRequest;
import com.bcafinance.backend_saku.features.superadmin.role.dto.RoleResponse;
import com.bcafinance.backend_saku.features.superadmin.role.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/superadmin/role", "/api/master/role", "/api/role"})
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
@Tag(name = "Superadmin - Role & Hak Akses", description = "Manajemen data role dan penugasan izin (permissions) pada SAKU App")
public class RoleController {

    private final RoleService roleService;

    // Ambil daftar seluruh role pengguna di sistem
    @Operation(summary = "Daftar semua role", description = "Mengambil seluruh data role hak akses pengguna dalam sistem")
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(roleService.findAll()));
    }

    // Ambil detail data role beserta daftar permission yang terpasang
    @Operation(summary = "Detail role & permissions", description = "Mengambil detail satu role beserta seluruh permission hak akses yang terhubung")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDetailResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(roleService.findById(id)));
    }

    // Buat data role pengguna baru
    @Operation(summary = "Tambah role baru", description = "Mendaftarkan data role pengguna baru ke dalam sistem")
    @PostMapping
    public ResponseEntity<ApiResponse<RoleDetailResponse>> create(
            @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(ApiResponse.created(roleService.create(request)));
    }

    // Perbarui nama & deskripsi role berdasarkan ID
    @Operation(summary = "Ubah data role", description = "Memperbarui nama role atau deskripsi peran pengguna")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDetailResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(roleService.update(id, request)));
    }

    // Pasang daftar hak akses permissions ke role tertentu
    @Operation(summary = "Assign permissions ke role", description = "Menghubungkan sekumpulan ID permission ke role pengguna")
    @PutMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<RoleDetailResponse>> assignPermissions(
            @PathVariable UUID id,
            @Valid @RequestBody AssignPermissionsRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                roleService.assignPermissions(id, request.getPermissionIds())));
    }

    // Hapus data role pengguna berdasarkan ID
    @Operation(summary = "Hapus role", description = "Menghapus role pengguna dari sistem berdasarkan ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        roleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
