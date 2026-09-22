package com.bcafinance.backend_saku.features.customer.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.customer.dto.NotifikasiResponse;
import com.bcafinance.backend_saku.features.customer.dto.UnreadNotifikasiCountResponse;
import com.bcafinance.backend_saku.features.customer.service.NotifikasiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer/notifikasi")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@Tag(name = "Customer - Notifikasi", description = "Layanan notifikasi nasabah SAKU App (List, Unread Count, Detail, Mark as Read)")
public class NotifikasiController {

    private final NotifikasiService notifikasiService;

    // Ambil daftar seluruh notifikasi customer dengan filter status opsional
    @Operation(summary = "Daftar notifikasi nasabah", description = "Mengambil daftar seluruh notifikasi nasabah dengan filter status opsional (READ / UNREAD)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotifikasiResponse>>> getNotifications(
            @AuthenticationPrincipal AppUser customer,
            @RequestParam(required = false) String status) {
        List<NotifikasiResponse> responses = notifikasiService.getCustomerNotifications(
                customer.getIdKaryawan(), status);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // Ambil jumlah badge notifikasi yang belum dibaca oleh customer
    @Operation(summary = "Jumlah notifikasi belum dibaca", description = "Mengambil total badge hitungan notifikasi yang belum dibaca nasabah")
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadNotifikasiCountResponse>> getUnreadCount(
            @AuthenticationPrincipal AppUser customer) {
        UnreadNotifikasiCountResponse response = notifikasiService.getUnreadCount(
                customer.getIdKaryawan());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Ambil detail notifikasi dan otomatis tandai status sebagai sudah dibaca
    @Operation(summary = "Detail notifikasi", description = "Mengambil detail pesan notifikasi dan menandai statusnya sebagai telah dibaca")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotifikasiResponse>> getDetail(
            @AuthenticationPrincipal AppUser customer,
            @PathVariable UUID id) {
        NotifikasiResponse response = notifikasiService.getDetailAndMarkAsRead(
                id, customer.getIdKaryawan());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Tandai satu notifikasi tertentu sebagai sudah dibaca
    @Operation(summary = "Tandai notifikasi dibaca", description = "Mengubah status satu notifikasi spesifik menjadi sudah dibaca")
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotifikasiResponse>> markAsRead(
            @AuthenticationPrincipal AppUser customer,
            @PathVariable UUID id) {
        NotifikasiResponse response = notifikasiService.markAsRead(
                id, customer.getIdKaryawan());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Tandai seluruh notifikasi yang ada sebagai sudah dibaca
    @Operation(summary = "Tandai semua notifikasi dibaca", description = "Mengubah status seluruh notifikasi nasabah yang belum terbaca menjadi sudah dibaca")
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(
            @AuthenticationPrincipal AppUser customer) {
        notifikasiService.markAllAsRead(customer.getIdKaryawan());
        return ResponseEntity.ok(ApiResponse.success("Semua notifikasi ditandai sebagai dibaca"));
    }
}
