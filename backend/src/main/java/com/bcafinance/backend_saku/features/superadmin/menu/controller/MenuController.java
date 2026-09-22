package com.bcafinance.backend_saku.features.superadmin.menu.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.superadmin.menu.dto.MenuRequest;
import com.bcafinance.backend_saku.features.superadmin.menu.dto.MenuResponse;
import com.bcafinance.backend_saku.features.superadmin.menu.service.MenuService;
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
@RequestMapping({"/api/superadmin/menu", "/api/master/menu", "/api/menu"})
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
@Tag(name = "Superadmin - Kelola Menu", description = "Manajemen struktur hierarki dan navigasi menu sistem SAKU App")
public class MenuController {

    private final MenuService menuService;

    // Ambil daftar seluruh menu navigasi sistem
    @Operation(summary = "Daftar semua menu", description = "Mengambil seluruh struktur data menu navigasi sistem")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(menuService.findAll()));
    }

    // Ambil detail data menu navigasi berdasarkan ID
    @Operation(summary = "Detail menu", description = "Mengambil rincian data menu navigasi berdasarkan ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(menuService.findById(id)));
    }

    // Tambah menu navigasi baru
    @Operation(summary = "Tambah menu baru", description = "Mendaftarkan item menu navigasi baru ke dalam sistem")
    @PostMapping
    public ResponseEntity<ApiResponse<MenuResponse>> create(@Valid @RequestBody MenuRequest request) {
        return ResponseEntity.ok(ApiResponse.created(menuService.create(request)));
    }

    // Perbarui konfigurasi menu navigasi berdasarkan ID
    @Operation(summary = "Ubah data menu", description = "Memperbarui nama menu, rute URL, ikon, atau urutan tampilan menu")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody MenuRequest request) {
        return ResponseEntity.ok(ApiResponse.success(menuService.update(id, request)));
    }

    // Hapus menu navigasi berdasarkan ID
    @Operation(summary = "Hapus menu", description = "Menghapus item menu navigasi dari sistem berdasarkan ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        menuService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
