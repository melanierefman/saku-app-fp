package com.bcafinance.backend_saku.features.backoffice.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.dto.PageResponse;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.backoffice.dto.AngsuranItemResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.PencairanDetailResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.PencairanItemResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.PencairanRequest;
import com.bcafinance.backend_saku.features.backoffice.dto.PencairanResponse;
import com.bcafinance.backend_saku.features.backoffice.service.PencairanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backoffice/pencairan")
@RequiredArgsConstructor
@Tag(name = "Backoffice - Pencairan Pinjaman", description = "Pencairan dana kredit yang telah disetujui BM ke rekening nasabah")
public class PencairanController {

    private final PencairanService pencairanService;

    // Ambil daftar pengajuan pinjaman siap cair dengan paginasi dan filter pencarian/status
    @Operation(summary = "Daftar pinjaman siap cair", description = "Mengambil daftar pengajuan pinjaman siap dicairkan dengan pagination dan filter status")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PencairanItemResponse>>> findAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(
                pencairanService.findAllPaginated(page, size, search, status)));
    }

    // Ambil daftar pengajuan yang sedang berstatus menunggu pencairan dana
    @Operation(summary = "Daftar antrean pencairan pending", description = "Mengambil daftar pinjaman berstatus MENUNGGU_PENCAIRAN")
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<PencairanItemResponse>>> findPending() {
        return ResponseEntity.ok(ApiResponse.success(pencairanService.findAll("MENUNGGU_PENCAIRAN")));
    }

    // Ambil detail lengkap pengajuan pinjaman untuk proses pencairan dana
    @Operation(summary = "Detail pencairan pinjaman", description = "Mengambil detail rekening bank tujuan, rincian biaya admin, dan total nominal pencairan")
    @GetMapping("/{pengajuanId}")
    public ResponseEntity<ApiResponse<PencairanDetailResponse>> getDetail(
            @PathVariable UUID pengajuanId) {
        return ResponseEntity.ok(ApiResponse.success(pencairanService.getDetail(pengajuanId)));
    }

    // Proses pencairan dana pinjaman ke rekening nasabah (POST)
    @Operation(summary = "Eksekusi pencairan dana (POST)", description = "Memproses pencairan dana pinjaman dan mencatat nomor referensi transfer")
    @PostMapping({"/{pengajuanId}", "/{pengajuanId}/cairkan"})
    public ResponseEntity<ApiResponse<PencairanResponse>> cairkan(
            @PathVariable UUID pengajuanId,
            @RequestBody(required = false) PencairanRequest request,
            @AuthenticationPrincipal AppUser karyawan) {
        return ResponseEntity.ok(ApiResponse.success(
                pencairanService.cairkanPinjaman(pengajuanId, karyawan.getIdKaryawan(), request)));
    }

    // Proses pencairan dana pinjaman ke rekening nasabah (PUT)
    @Operation(summary = "Eksekusi pencairan dana (PUT)", description = "Memproses pencairan dana pinjaman via PUT")
    @PutMapping("/{pengajuanId}")
    public ResponseEntity<ApiResponse<PencairanResponse>> cairkanPut(
            @PathVariable UUID pengajuanId,
            @RequestBody(required = false) PencairanRequest request,
            @AuthenticationPrincipal AppUser karyawan) {
        return ResponseEntity.ok(ApiResponse.success(
                pencairanService.cairkanPinjaman(pengajuanId, karyawan.getIdKaryawan(), request)));
    }

    // Ambil rincian simulasi/jadwal tabel angsuran bulanan pinjaman
    @Operation(summary = "Jadwal angsuran pencairan", description = "Mengambil rincian tabel jadwal angsuran kredit yang terbentuk setelah pencairan")
    @GetMapping("/{pengajuanId}/angsuran")
    public ResponseEntity<ApiResponse<List<AngsuranItemResponse>>> getJadwalAngsuran(
            @PathVariable UUID pengajuanId) {
        return ResponseEntity.ok(ApiResponse.success(pencairanService.getJadwalAngsuran(pengajuanId)));
    }
}
