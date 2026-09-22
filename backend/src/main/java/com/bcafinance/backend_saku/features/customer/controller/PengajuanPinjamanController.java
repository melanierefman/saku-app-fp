package com.bcafinance.backend_saku.features.customer.controller;

import com.bcafinance.backend_saku.core.dto.AngsuranItemResponse;
import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.customer.dto.PengajuanPinjamanRequest;
import com.bcafinance.backend_saku.features.customer.dto.PengajuanPinjamanResponse;
import com.bcafinance.backend_saku.features.customer.dto.PengajuanStepResponse;
import com.bcafinance.backend_saku.features.customer.service.PengajuanPinjamanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/customer/pengajuan-pinjaman")
@RequiredArgsConstructor
@Tag(name = "Customer - Pengajuan Pinjaman", description = "Alur pengajuan kredit nasabah SAKU App (Step 1-2, Riwayat, Detail, Jadwal Angsuran)")
public class PengajuanPinjamanController {

    private final PengajuanPinjamanService pengajuanService;

    // Step 1 Pengajuan: Input nominal pinjaman, tenor, dan tujuan penggunaan dana
    @Operation(summary = "Pengajuan pinjaman Step 1", description = "Input nominal pengajuan pinjaman, pilihan tenor bulan, dan tujuan penggunaan pinjaman")
    @PostMapping("/step1")
    public ResponseEntity<ApiResponse<PengajuanStepResponse>> step1(
            @Valid @RequestBody PengajuanPinjamanRequest request,
            @AuthenticationPrincipal AppUser customer) {
        return ResponseEntity.ok(ApiResponse.success(
                pengajuanService.step1(customer.getIdKaryawan(), request)));
    }

    // Step 2 Pengajuan: Upload dokumen pendukung (Slip Gaji, Rekening Koran, NPWP)
    @Operation(summary = "Pengajuan pinjaman Step 2 (Upload Dokumen)", description = "Upload berkas dokumen pendukung berupa slip gaji, rekening koran 3 bulan, dan foto NPWP")
    @PostMapping(value = "/step2/{pengajuanId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PengajuanStepResponse>> step2(
            @PathVariable UUID pengajuanId,
            @RequestPart(value = "slipGaji", required = false) MultipartFile slipGaji,
            @RequestPart(value = "rekeningKoran", required = false) MultipartFile rekeningKoran,
            @RequestPart(value = "npwp", required = false) MultipartFile npwp,
            @AuthenticationPrincipal AppUser customer) {
        return ResponseEntity.ok(ApiResponse.success(
                pengajuanService.step2(customer.getIdKaryawan(), pengajuanId, slipGaji, rekeningKoran, npwp)));
    }

    // Ambil daftar riwayat seluruh pengajuan pinjaman milik nasabah yang sedang login
    @Operation(summary = "Riwayat pengajuan pinjaman nasabah", description = "Mengambil seluruh riwayat pengajuan pinjaman milik nasabah yang sedang aktif login")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<PengajuanPinjamanResponse>>> findMyLoans(
            @AuthenticationPrincipal AppUser customer) {
        return ResponseEntity.ok(ApiResponse.success(
                pengajuanService.findMyLoans(customer.getIdKaryawan())));
    }

    // Ambil detail rincian status & data pengajuan pinjaman tertentu berdasarkan ID
    @Operation(summary = "Detail pengajuan pinjaman nasabah", description = "Mengambil rincian status, tahapan approval, dan data pengajuan pinjaman berdasarkan ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PengajuanPinjamanResponse>> findById(
            @PathVariable UUID id,
            @AuthenticationPrincipal AppUser customer) {
        return ResponseEntity.ok(ApiResponse.success(
                pengajuanService.findById(id, customer.getIdKaryawan())));
    }

    // Ambil jadwal rincian simulasi tabel cicilan bulanan untuk pengajuan pinjaman
    @Operation(summary = "Jadwal angsuran pinjaman", description = "Mengambil rincian simulasi jadwal angsuran pokok dan bunga pinjaman per bulan")
    @GetMapping("/{pengajuanId}/angsuran")
    public ResponseEntity<ApiResponse<List<AngsuranItemResponse>>> getJadwalAngsuran(
            @PathVariable UUID pengajuanId,
            @AuthenticationPrincipal AppUser customer) {
        return ResponseEntity.ok(ApiResponse.success(
                pengajuanService.getJadwalAngsuran(pengajuanId, customer.getIdKaryawan())));
    }
}
