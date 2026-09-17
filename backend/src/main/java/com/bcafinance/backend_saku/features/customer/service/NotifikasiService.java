package com.bcafinance.backend_saku.features.customer.service;

import com.bcafinance.backend_saku.core.entity.Notifikasi;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.NotifikasiRepository;
import com.bcafinance.backend_saku.features.customer.dto.NotifikasiResponse;
import com.bcafinance.backend_saku.features.customer.dto.UnreadNotifikasiCountResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotifikasiService {

    public static final String STATUS_BELUM_DIBACA = "BELUM_DIBACA";
    public static final String STATUS_SUDAH_DIBACA = "SUDAH_DIBACA";

    private final NotifikasiRepository notifikasiRepository;
    private final com.bcafinance.backend_saku.core.repository.CustomerRepository customerRepository;
    private final FcmPushService fcmPushService;

    @Transactional(readOnly = true)
    public List<NotifikasiResponse> getCustomerNotifications(UUID customerId, String statusFilter) {
        List<Notifikasi> notifications;

        if (statusFilter != null && !statusFilter.isBlank()) {
            String filter = statusFilter.trim().toUpperCase();
            if ("UNREAD".equals(filter) || "BELUM_DIBACA".equals(filter)) {
                notifications = notifikasiRepository.findAllByMstCustomerIdAndStatusOrderByCreatedDateDesc(customerId, STATUS_BELUM_DIBACA);
            } else if ("READ".equals(filter) || "SUDAH_DIBACA".equals(filter)) {
                notifications = notifikasiRepository.findAllByMstCustomerIdAndStatusOrderByCreatedDateDesc(customerId, STATUS_SUDAH_DIBACA);
            } else {
                notifications = notifikasiRepository.findAllByMstCustomerIdAndStatusOrderByCreatedDateDesc(customerId, filter);
            }
        } else {
            notifications = notifikasiRepository.findAllByMstCustomerIdOrderByCreatedDateDesc(customerId);
        }

        return notifications.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UnreadNotifikasiCountResponse getUnreadCount(UUID customerId) {
        long unread = notifikasiRepository.countByMstCustomerIdAndStatus(customerId, STATUS_BELUM_DIBACA);
        return new UnreadNotifikasiCountResponse(unread);
    }

    @Transactional
    public NotifikasiResponse getDetailAndMarkAsRead(UUID notifikasiId, UUID customerId) {
        Notifikasi notifikasi = notifikasiRepository.findByIdAndMstCustomerId(notifikasiId, customerId)
                .orElseThrow(() -> new BussinessRuleException("Notifikasi tidak ditemukan"));

        if (STATUS_BELUM_DIBACA.equals(notifikasi.getStatus())) {
            notifikasi.setStatus(STATUS_SUDAH_DIBACA);
            notifikasi.setUpdatedDate(LocalDateTime.now());
            notifikasi = notifikasiRepository.save(notifikasi);
        }

        return mapToResponse(notifikasi);
    }

    @Transactional
    public NotifikasiResponse markAsRead(UUID notifikasiId, UUID customerId) {
        Notifikasi notifikasi = notifikasiRepository.findByIdAndMstCustomerId(notifikasiId, customerId)
                .orElseThrow(() -> new BussinessRuleException("Notifikasi tidak ditemukan"));


        notifikasi.setStatus(STATUS_SUDAH_DIBACA);
        notifikasi.setUpdatedDate(LocalDateTime.now());
        notifikasi = notifikasiRepository.save(notifikasi);

        return mapToResponse(notifikasi);
    }

    @Transactional
    public void markAllAsRead(UUID customerId) {
        List<Notifikasi> unreadList = notifikasiRepository.findAllByMstCustomerIdAndStatus(customerId, STATUS_BELUM_DIBACA);
        LocalDateTime now = LocalDateTime.now();
        for (Notifikasi n : unreadList) {
            n.setStatus(STATUS_SUDAH_DIBACA);
            n.setUpdatedDate(now);
        }
        notifikasiRepository.saveAll(unreadList);
        log.info("Marked {} notifications as read for customer {}", unreadList.size(), customerId);
    }

    @Transactional
    public void createNotification(UUID customerId, UUID pengajuanId, String type, String channel, String judul, String pesan) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Notifikasi notifikasi = new Notifikasi();
            notifikasi.setId(UUID.randomUUID());
            notifikasi.setMstCustomerId(customerId);
            notifikasi.setTrxPengajuanPinjamanId(pengajuanId != null ? pengajuanId : new UUID(0L, 0L));
            notifikasi.setType(type != null ? type : "INFO");
            notifikasi.setChannel(channel != null ? channel : "IN_APP");
            notifikasi.setJudul(judul);
            notifikasi.setPesan(pesan);
            notifikasi.setStatus(STATUS_BELUM_DIBACA);
            notifikasi.setCreatedDate(now);
            notifikasi.setUpdatedDate(now);

            notifikasiRepository.save(notifikasi);
            log.info("🔔 Created in-app notification for customer {}: [{}] {}", customerId, type, judul);

            // Push Notification via FCM
            try {
                customerRepository.findById(customerId).ifPresent(customer -> {
                    if (customer.getFcmToken() != null && !customer.getFcmToken().isBlank()) {
                        java.util.Map<String, String> data = new java.util.HashMap<>();
                        String safeType = type != null ? type : "INFO";
                        data.put("type", safeType);
                        data.put("notifId", notifikasi.getId().toString());
                        if (pengajuanId != null) {
                            data.put("pengajuanId", pengajuanId.toString());
                            data.put("targetRoute", "loan_detail/" + pengajuanId);
                        } else if ("KYC".equalsIgnoreCase(safeType) || "VERIFIKASI_AKUN".equalsIgnoreCase(safeType)) {
                            data.put("targetRoute", "kyc_pending");
                        } else {
                            data.put("targetRoute", "home");
                        }
                        fcmPushService.sendPush(customer.getFcmToken(), judul, pesan, data);
                    }
                });
            } catch (Exception fcmEx) {
                log.warn("FCM push notification failed: {}", fcmEx.getMessage());
            }
        } catch (Exception e) {
            log.error("Failed to create notification for customer {}: {}", customerId, e.getMessage());
        }
    }

    private NotifikasiResponse mapToResponse(Notifikasi n) {
        UUID loanId = n.getTrxPengajuanPinjamanId();
        if (loanId != null && (loanId.equals(new UUID(0L, 0L)) || "KYC".equalsIgnoreCase(n.getType()) || "INFO".equalsIgnoreCase(n.getType()))) {
            loanId = null;
        }

        return NotifikasiResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .channel(n.getChannel())
                .judul(n.getJudul())
                .pesan(n.getPesan())
                .status(n.getStatus())
                .isRead(STATUS_SUDAH_DIBACA.equalsIgnoreCase(n.getStatus()))
                .pengajuanPinjamanId(loanId)
                .createdDate(n.getCreatedDate())
                .build();
    }
}
