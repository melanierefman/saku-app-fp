package com.bcafinance.backend_saku.features.superadmin.karyawan;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping({"/api/superadmin/karyawan", "/api/master/karyawan", "/api/karyawan"})
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
@Tag(name = "Superadmin - Kelola Karyawan", description = "Manajemen data akun staf internal, role, dan cabang penempatan SAKU App")
public class KaryawanController {

    private final KaryawanService karyawanService;

    // Tambah akun data karyawan baru
    @Operation(summary = "Tambah karyawan baru", description = "Mendaftarkan akun staf internal baru beserta role dan cabang penempatan")
    @PostMapping
    public ResponseEntity<ApiResponse<KaryawanResponse>> create(
            @Valid @RequestBody KaryawanRequest request) {
        return ResponseEntity.ok(ApiResponse.created(karyawanService.create(request)));
    }

    // Ambil daftar karyawan dengan paginasi dan filter role/cabang/status
    @Operation(summary = "Daftar semua karyawan", description = "Mengambil daftar seluruh karyawan internal dengan filter cabang, role, pencarian, dan pagination")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<KaryawanResponse>>> findAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "roleId", required = false) UUID roleId,
            @RequestParam(name = "branchId", required = false) UUID branchId,
            @RequestParam(name = "status", required = false) Boolean status) {
        return ResponseEntity.ok(ApiResponse.success(
                karyawanService.findAllPaginated(page, size, search, roleId, branchId, status)));
    }

    // Ambil detail data karyawan berdasarkan ID
    @Operation(summary = "Detail karyawan", description = "Mengambil rincian data akun karyawan, cabang, dan role berdasarkan ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KaryawanResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(karyawanService.findById(id)));
    }

    // Perbarui data profil & jabatan karyawan berdasarkan ID
    @Operation(summary = "Ubah data karyawan", description = "Memperbarui informasi profil, kontak, role, atau cabang karyawan")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<KaryawanResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody KaryawanRequest request) {
        return ResponseEntity.ok(ApiResponse.updated(karyawanService.update(id, request)));
    }

    // Hapus data karyawan secara soft delete / hapus akun
    @Operation(summary = "Hapus data karyawan", description = "Menghapus akun staf internal dari sistem")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        karyawanService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
