package com.bcafinance.backend_saku.features.backoffice.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.dto.PageResponse;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.backoffice.dto.PendingCustomerResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.VerifikasiCustomerDetailResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.VerifikasiCustomerItemResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.VerifikasiCustomerRequest;
import com.bcafinance.backend_saku.features.backoffice.dto.VerifikasiCustomerResponse;
import com.bcafinance.backend_saku.features.backoffice.service.VerifikasiCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/backoffice/verifikasi-customer")
@RequiredArgsConstructor
@Tag(name = "Backoffice - Verifikasi Customer", description = "Verifikasi identitas dan berkas KYC nasabah baru oleh tim Backoffice")
public class VerifikasiCustomerController {

    private final VerifikasiCustomerService verifikasiService;

    // Ambil daftar verifikasi customer dengan paginasi dan filter pencarian/status
    @Operation(summary = "Daftar verifikasi customer", description = "Mengambil daftar verifikasi nasabah dengan pagination, pencarian nama/NIK, dan filter status")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<VerifikasiCustomerItemResponse>>> findAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(
                verifikasiService.findAllPaginated(page, size, search, status)));
    }

    // Ambil daftar customer yang sedang menunggu verifikasi (pending)
    @Operation(summary = "Daftar customer pending", description = "Mengambil daftar nasabah baru yang sedang menunggu antrean verifikasi KYC")
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<PendingCustomerResponse>>> findPending() {
        return ResponseEntity.ok(ApiResponse.success(verifikasiService.findPending()));
    }

    // Ambil detail data lengkap customer untuk proses verifikasi
    @Operation(summary = "Detail data verifikasi customer", description = "Mengambil rincian data e-KTP, swafoto selfie liveness, data pekerjaan, dan rekening nasabah")
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<VerifikasiCustomerDetailResponse>> getDetail(
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(verifikasiService.getDetail(customerId)));
    }

    // Simpan hasil review verifikasi customer (PUT)
    @Operation(summary = "Keputusan verifikasi (PUT)", description = "Menyimpan hasil review verifikasi KYC nasabah (VERIFIED / REJECTED) beserta catatan perbaikan")
    @PutMapping({"/{customerId}", "/{customerId}/verifikasi"})
    public ResponseEntity<ApiResponse<VerifikasiCustomerResponse>> verify(
            @PathVariable UUID customerId,
            @Valid @RequestBody VerifikasiCustomerRequest request,
            @AuthenticationPrincipal AppUser karyawan) {
        return ResponseEntity.ok(ApiResponse.success(
                verifikasiService.verify(customerId, karyawan.getIdKaryawan(), request)));
    }

    // Simpan hasil review verifikasi customer (POST)
    @Operation(summary = "Keputusan verifikasi (POST)", description = "Menyimpan hasil review verifikasi KYC nasabah (VERIFIED / REJECTED) via POST")
    @PostMapping({"/{customerId}", "/{customerId}/verifikasi"})
    public ResponseEntity<ApiResponse<VerifikasiCustomerResponse>> verifyPost(
            @PathVariable UUID customerId,
            @Valid @RequestBody VerifikasiCustomerRequest request,
            @AuthenticationPrincipal AppUser karyawan) {
        return ResponseEntity.ok(ApiResponse.success(
                verifikasiService.verify(customerId, karyawan.getIdKaryawan(), request)));
    }
}
