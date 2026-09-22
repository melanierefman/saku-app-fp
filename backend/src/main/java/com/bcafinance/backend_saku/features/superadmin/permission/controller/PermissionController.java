package com.bcafinance.backend_saku.features.superadmin.permission.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.superadmin.permission.dto.PermissionRequest;
import com.bcafinance.backend_saku.features.superadmin.permission.dto.PermissionResponse;
import com.bcafinance.backend_saku.features.superadmin.permission.service.PermissionService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/superadmin/permission", "/api/master/permission", "/api/permission"})
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
@Tag(name = "Superadmin - Kelola Permission", description = "Manajemen hak akses granular permission pada fitur-fitur SAKU App")
public class PermissionController {

    private final PermissionService permissionService;

    // Ambil daftar hak akses permission dengan filter menuId opsional
    @Operation(summary = "Daftar permission", description = "Mengambil seluruh data permission hak akses dengan filter menuId opsional")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> findAll(
            @RequestParam(required = false) UUID menuId) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.findAll(menuId)));
    }

    // Ambil detail data permission berdasarkan ID
    @Operation(summary = "Detail permission", description = "Mengambil rincian informasi satu permission berdasarkan ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.findById(id)));
    }

    // Tambah data permission hak akses baru
    @Operation(summary = "Tambah permission baru", description = "Mendaftarkan izin hak akses baru ke dalam sistem")
    @PostMapping
    public ResponseEntity<ApiResponse<PermissionResponse>> create(
            @Valid @RequestBody PermissionRequest request) {
        return ResponseEntity.ok(ApiResponse.created(permissionService.create(request)));
    }

    // Perbarui data nama & deskripsi permission berdasarkan ID
    @Operation(summary = "Ubah data permission", description = "Memperbarui nama atau deskripsi permission hak akses")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody PermissionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.update(id, request)));
    }

    // Hapus data permission berdasarkan ID
    @Operation(summary = "Hapus permission", description = "Menghapus permission hak akses dari sistem berdasarkan ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        permissionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
