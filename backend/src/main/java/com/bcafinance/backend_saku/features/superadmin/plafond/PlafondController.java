package com.bcafinance.backend_saku.features.superadmin.plafond;

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
@RequestMapping({"/api/superadmin/plafond", "/api/master/plafond", "/api/plafond"})
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
@Tag(name = "Superadmin - Konfigurasi Plafond", description = "Pengaturan batas pinjaman, tenor, suku bunga, dan biaya admin produk pinjaman")
public class PlafondController {

    private final PlafondService plafondService;

    // Buat konfigurasi tier plafond pinjaman baru
    @Operation(summary = "Tambah aturan plafond", description = "Membuat konfigurasi batas pinjaman, tenor, dan suku bunga baru")
    @PostMapping
    public ResponseEntity<ApiResponse<PlafondResponse>> create(
            @Valid @RequestBody PlafondRequest request) {
        return ResponseEntity.ok(ApiResponse.created(plafondService.create(request)));
    }

    // Ambil semua daftar tier plafond dan batas limit pinjaman
    @Operation(summary = "Daftar semua aturan plafond", description = "Mengambil seluruh konfigurasi paket plafond dan suku bunga yang terdaftar")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PlafondResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(plafondService.findAll()));
    }

    // Ambil detail konfigurasi plafond berdasarkan ID
    @Operation(summary = "Detail aturan plafond", description = "Mengambil rincian batas pinjaman, tenor, dan bunga berdasarkan ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlafondResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(plafondService.findById(id)));
    }

    // Perbarui konfigurasi limit/bunga/biaya tier plafond berdasarkan ID
    @Operation(summary = "Ubah aturan plafond", description = "Memperbarui nilai batas pinjaman, tenor, atau suku bunga plafond")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlafondResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody PlafondRequest request) {
        return ResponseEntity.ok(ApiResponse.updated(plafondService.update(id, request)));
    }

    // Hapus data tier plafond berdasarkan ID
    @Operation(summary = "Hapus aturan plafond", description = "Menghapus konfigurasi tier plafond dari sistem berdasarkan ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        plafondService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
