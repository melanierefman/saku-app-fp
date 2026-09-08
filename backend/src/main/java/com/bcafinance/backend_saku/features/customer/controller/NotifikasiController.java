package com.bcafinance.backend_saku.features.customer.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.customer.dto.NotifikasiResponse;
import com.bcafinance.backend_saku.features.customer.dto.UnreadNotifikasiCountResponse;
import com.bcafinance.backend_saku.features.customer.service.NotifikasiService;
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
public class NotifikasiController {

    private final NotifikasiService notifikasiService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotifikasiResponse>>> getNotifications(
            @AuthenticationPrincipal AppUser customer,
            @RequestParam(required = false) String status) {
        List<NotifikasiResponse> responses = notifikasiService.getCustomerNotifications(
                customer.getIdKaryawan(), status);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadNotifikasiCountResponse>> getUnreadCount(
            @AuthenticationPrincipal AppUser customer) {
        UnreadNotifikasiCountResponse response = notifikasiService.getUnreadCount(
                customer.getIdKaryawan());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotifikasiResponse>> getDetail(
            @AuthenticationPrincipal AppUser customer,
            @PathVariable UUID id) {
        NotifikasiResponse response = notifikasiService.getDetailAndMarkAsRead(
                id, customer.getIdKaryawan());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotifikasiResponse>> markAsRead(
            @AuthenticationPrincipal AppUser customer,
            @PathVariable UUID id) {
        NotifikasiResponse response = notifikasiService.markAsRead(
                id, customer.getIdKaryawan());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(
            @AuthenticationPrincipal AppUser customer) {
        notifikasiService.markAllAsRead(customer.getIdKaryawan());
        return ResponseEntity.ok(ApiResponse.success("Semua notifikasi berhasil ditandai sebagai sudah dibaca"));
    }
}
