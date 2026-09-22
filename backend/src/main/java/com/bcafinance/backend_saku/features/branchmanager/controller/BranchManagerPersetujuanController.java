package com.bcafinance.backend_saku.features.branchmanager.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.dto.PageResponse;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.branchmanager.dto.BranchManagerPengajuanDetailResponse;
import com.bcafinance.backend_saku.features.branchmanager.dto.BranchManagerPengajuanItemResponse;
import com.bcafinance.backend_saku.features.branchmanager.dto.PersetujuanPinjamanRequest;
import com.bcafinance.backend_saku.features.branchmanager.dto.PersetujuanPinjamanResponse;
import com.bcafinance.backend_saku.features.branchmanager.service.BranchManagerPersetujuanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/branch-manager/persetujuan", "/api/bm/persetujuan", "/api/branchmanager/persetujuan"})
@RequiredArgsConstructor
@Tag(name = "Branch Manager - Persetujuan", description = "Persetujuan akhir pengajuan pinjaman kredit oleh Branch Manager")
public class BranchManagerPersetujuanController {

    private final BranchManagerPersetujuanService branchManagerPersetujuanService;

    // Ambil daftar pengajuan pinjaman untuk persetujuan Branch Manager (paginated)
    @Operation(summary = "Daftar pengajuan persetujuan BM", description = "Mengambil daftar pengajuan pinjaman yang membutuhkan persetujuan final BM dengan filter & paginasi")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BranchManagerPengajuanItemResponse>>> findAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status,
            @AuthenticationPrincipal AppUser karyawan) {
        UUID karyawanId = karyawan != null ? karyawan.getIdKaryawan() : null;
        return ResponseEntity.ok(ApiResponse.success(
                branchManagerPersetujuanService.findAllPaginated(page, size, search, status, karyawanId)));
    }

    // Ambil detail lengkap pengajuan pinjaman untuk dianalisis oleh Branch Manager
    @Operation(summary = "Detail pengajuan pinjaman BM", description = "Mengambil rincian berkas, rekomendasi marketing, dan riwayat scoring untuk dianalisis Branch Manager")
    @GetMapping("/{pengajuanId}")
    public ResponseEntity<ApiResponse<BranchManagerPengajuanDetailResponse>> getDetail(
            @PathVariable UUID pengajuanId) {
        return ResponseEntity.ok(ApiResponse.success(branchManagerPersetujuanService.getDetail(pengajuanId)));
    }

    // Simpan keputusan persetujuan akhir pengajuan pinjaman oleh Branch Manager
    @Operation(summary = "Keputusan persetujuan BM", description = "Menyimpan keputusan akhir persetujuan (APPROVED) atau penolakan (REJECTED) pengajuan pinjaman oleh BM")
    @PutMapping({"/{pengajuanId}", "/{pengajuanId}/approve"})
    public ResponseEntity<ApiResponse<PersetujuanPinjamanResponse>> persetujuan(
            @PathVariable UUID pengajuanId,
            @Valid @RequestBody PersetujuanPinjamanRequest request,
            @AuthenticationPrincipal AppUser karyawan) {
        return ResponseEntity.ok(ApiResponse.success(
                branchManagerPersetujuanService.persetujuan(pengajuanId, karyawan.getIdKaryawan(), request)));
    }
}
