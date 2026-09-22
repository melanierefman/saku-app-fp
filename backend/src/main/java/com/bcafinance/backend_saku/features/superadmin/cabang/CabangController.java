package com.bcafinance.backend_saku.features.superadmin.cabang;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
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
@RequestMapping({"/api/superadmin/cabang", "/api/master/cabang", "/api/cabang"})
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
@Tag(name = "Superadmin - Master Cabang", description = "Manajemen data master kantor cabang SAKU App (CRUD & Status Aktif)")
public class CabangController {

    private final CabangService cabangService;

    // Buat data kantor cabang operasional baru
    @Operation(summary = "Tambah cabang baru", description = "Mendaftarkan kantor cabang operasional baru ke dalam sistem")
    @PostMapping
    public ResponseEntity<ApiResponse<CabangResponse>> create(
            @Valid @RequestBody CabangRequest request) {
        return ResponseEntity.ok(ApiResponse.created(cabangService.create(request)));
    }

    // Ambil daftar seluruh kantor cabang
    @Operation(summary = "Daftar semua cabang", description = "Mengambil seluruh daftar kantor cabang yang terdaftar")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CabangResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(cabangService.findAll()));
    }

    // Ambil daftar kantor cabang yang sedang berstatus aktif
    @Operation(summary = "Daftar cabang aktif", description = "Mengambil daftar kantor cabang yang berstatus aktif beroperasi")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CabangResponse>>> findAllActive() {
        return ResponseEntity.ok(ApiResponse.success(cabangService.findAllActive()));
    }

    // Ambil detail data kantor cabang berdasarkan ID
    @Operation(summary = "Detail cabang", description = "Mengambil informasi rincian satu kantor cabang berdasarkan ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CabangResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(cabangService.findById(id)));
    }

    // Perbarui informasi data kantor cabang berdasarkan ID
    @Operation(summary = "Ubah data cabang", description = "Memperbarui nama, kode, alamat, atau status kantor cabang")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CabangResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody CabangRequest request) {
        return ResponseEntity.ok(ApiResponse.updated(cabangService.update(id, request)));
    }

    // Hapus data kantor cabang berdasarkan ID
    @Operation(summary = "Hapus cabang", description = "Menghapus kantor cabang dari sistem berdasarkan ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        cabangService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
