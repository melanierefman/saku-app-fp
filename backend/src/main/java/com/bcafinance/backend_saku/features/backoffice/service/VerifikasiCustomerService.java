package com.bcafinance.backend_saku.features.backoffice.service;

import com.bcafinance.backend_saku.core.dto.AlamatDetailResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.PendingCustomerResponse;
import com.bcafinance.backend_saku.core.dto.PageResponse;
import com.bcafinance.backend_saku.features.superadmin.plafond.PlafondCalculationResponse;
import com.bcafinance.backend_saku.features.superadmin.plafond.PlafondService;
import com.bcafinance.backend_saku.features.backoffice.dto.VerifikasiCustomerDetailResponse;

import com.bcafinance.backend_saku.features.backoffice.dto.VerifikasiCustomerItemResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.VerifikasiCustomerRequest;
import com.bcafinance.backend_saku.features.backoffice.dto.VerifikasiCustomerResponse;
import com.bcafinance.backend_saku.core.entity.AlamatCustomer;
import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.entity.DokumenCustomer;
import com.bcafinance.backend_saku.core.entity.Karyawan;
import com.bcafinance.backend_saku.core.entity.Plafond;
import com.bcafinance.backend_saku.core.entity.ScoringCustomer;
import com.bcafinance.backend_saku.core.entity.VerifikasiCustomer;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.AlamatCustomerRepository;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.repository.DokumenCustomerRepository;
import com.bcafinance.backend_saku.core.repository.ScoringCustomerRepository;
import com.bcafinance.backend_saku.core.repository.VerifikasiCustomerRepository;
import com.bcafinance.backend_saku.features.scoring.service.ScoringService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerifikasiCustomerService {

    private final CustomerRepository customerRepository;
    private final ScoringCustomerRepository scoringRepository;
    private final VerifikasiCustomerRepository verifikasiRepository;
    private final AlamatCustomerRepository alamatRepository;
    private final DokumenCustomerRepository dokumenRepository;
    private final PlafondService plafondService;
    private final com.bcafinance.backend_saku.core.repository.KaryawanRepository karyawanRepository;
    private final com.bcafinance.backend_saku.core.repository.PlafondRepository plafondRepository;
    private final com.bcafinance.backend_saku.features.scoring.service.ScoringService scoringService;
    private final com.bcafinance.backend_saku.features.superadmin.auditlog.service.AuditLogService auditLogService;
    private final com.bcafinance.backend_saku.features.customer.service.NotifikasiService notifikasiService;
    private final com.bcafinance.backend_saku.core.repository.NotifikasiRepository notifikasiRepository;

    public PageResponse<VerifikasiCustomerItemResponse> findAllPaginated(int page, int size, String search, String statusFilter) {
        List<VerifikasiCustomerItemResponse> all = findAll(statusFilter);

        if (search != null && !search.trim().isEmpty()) {
            String s = search.trim().toLowerCase();
            all = all.stream().filter(item ->
                    (item.getNamaCustomer() != null && item.getNamaCustomer().toLowerCase().contains(s)) ||
                    (item.getNik() != null && item.getNik().toLowerCase().contains(s)) ||
                    (item.getEmail() != null && item.getEmail().toLowerCase().contains(s)) ||
                    (item.getNoHp() != null && item.getNoHp().toLowerCase().contains(s))
            ).toList();
        }

        return PageResponse.ofList(all, page, size);
    }


    public List<VerifikasiCustomerItemResponse> findAll(String statusFilter) {
        List<Customer> customers = customerRepository.findAllByOrderByCreatedDateDesc();

        return customers.stream()
                .filter(this::isRegistrationCompleted)
                .map(customer -> {
                    Optional<VerifikasiCustomer> latestVerification = verifikasiRepository
                            .findFirstByMstCustomerIdOrderByCreatedDateDesc(customer.getId());
                    String status = determineStatus(customer, latestVerification);
                    String catatan = latestVerification.map(VerifikasiCustomer::getCatatanVerifikasi).orElse(null);
                    LocalDateTime tglVerifikasi = latestVerification.map(VerifikasiCustomer::getCreatedDate)
                            .orElse(null);

                    return VerifikasiCustomerItemResponse.builder()
                            .customerId(customer.getId())
                            .namaCustomer(customer.getNama())
                            .nik(customer.getNik())
                            .email(customer.getEmail())
                            .noHp(customer.getNoHp())
                            .tanggalPengajuan(customer.getCreatedDate())
                            .statusVerifikasi(status)
                            .catatanVerifikasi(catatan)
                            .tanggalVerifikasi(tglVerifikasi)
                            .build();
                })
                .filter(item -> {
                    if (statusFilter == null || statusFilter.isBlank() || "ALL".equalsIgnoreCase(statusFilter)) {
                        return true;
                    }
                    return statusFilter.equalsIgnoreCase(item.getStatusVerifikasi());
                })
                .toList();
    }

    public List<PendingCustomerResponse> findPending() {
        return customerRepository.findAllByOrderByCreatedDateDesc().stream()
                .filter(this::isRegistrationCompleted)
                .filter(customer -> {
                    Optional<VerifikasiCustomer> latestVerification = verifikasiRepository
                            .findFirstByMstCustomerIdOrderByCreatedDateDesc(customer.getId());
                    String status = determineStatus(customer, latestVerification);
                    return "PENDING".equalsIgnoreCase(status) || "PERLU_REVISI".equalsIgnoreCase(status);
                })
                .map(customer -> {
                    Optional<VerifikasiCustomer> latestVerification = verifikasiRepository
                            .findFirstByMstCustomerIdOrderByCreatedDateDesc(customer.getId());
                    String statusVerifikasi = determineStatus(customer, latestVerification);

                    return PendingCustomerResponse.builder()
                            .id(customer.getId())
                            .nama(customer.getNama())
                            .email(customer.getEmail())
                            .username(customer.getUsername())
                            .nik(customer.getNik())
                            .noHp(customer.getNoHp())
                            .status(customer.getStatus())
                            .statusVerifikasi(statusVerifikasi)
                            .tanggalPengajuan(customer.getCreatedDate())
                            .build();
                })
                .toList();
    }

    private boolean isRegistrationCompleted(Customer customer) {
        if (customer == null) return false;
        if ("PENDING".equalsIgnoreCase(customer.getNik()) ||
            "PENDING".equalsIgnoreCase(customer.getPassword()) ||
            "PENDING".equalsIgnoreCase(customer.getNama()) ||
            "PENDING".equalsIgnoreCase(customer.getNoHp()) ||
            customer.getNik() == null || customer.getNik().isBlank() ||
            customer.getPassword() == null || customer.getPassword().isBlank()) {
            return false;
        }
        return true;
    }

    public VerifikasiCustomerDetailResponse getDetail(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));

        Optional<ScoringCustomer> scoringOpt = scoringRepository
                .findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId);
        Optional<VerifikasiCustomer> verifikasiOpt = verifikasiRepository
                .findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId);

        Optional<AlamatCustomer> alamatKtpOpt = alamatRepository.findByCustomer_IdAndJenisAlamat(customerId, "KTP");
        Optional<AlamatCustomer> alamatDomisiliOpt = alamatRepository.findByCustomer_IdAndJenisAlamat(customerId,
                "DOMISILI");

        Optional<DokumenCustomer> dokumenKtpOpt = dokumenRepository.findByCustomer_IdAndDocType(customerId, "KTP");
        Optional<DokumenCustomer> dokumenSelfieOpt = dokumenRepository.findByCustomer_IdAndDocType(customerId,
                "SELFIE");

        String statusVerifikasi = determineStatus(customer, verifikasiOpt);

        return VerifikasiCustomerDetailResponse.builder()
                .customerId(customer.getId())
                .namaLengkap(customer.getNama())
                .nik(customer.getNik())
                .email(customer.getEmail())
                .noHp(customer.getNoHp())
                .username(customer.getUsername())
                .namaIbuKandung(customer.getNamaIbuKandung())

                // Rekening
                .namaBank(customer.getNamaBank())
                .noRekening(customer.getNoRekening())
                .namaRekening(customer.getNamaRekening())

                // Pekerjaan & Keuangan
                .pekerjaan(scoringOpt.map(ScoringCustomer::getPekerjaan).orElse(null))
                .tempatKerja(scoringOpt.map(ScoringCustomer::getTempatKerja).orElse(null))
                .statusPekerjaan(scoringOpt.map(ScoringCustomer::getStatusPekerjaan).orElse(null))
                .pendapatan(scoringOpt.map(ScoringCustomer::getPenghasilanBulanan).orElse(null))
                .penghasilanBulanan(scoringOpt.map(ScoringCustomer::getPenghasilanBulanan).orElse(null))

                // Alamat
                .alamatKtp(alamatKtpOpt.map(this::mapAlamat).orElse(null))
                .alamatDomisili(alamatDomisiliOpt.map(this::mapAlamat).orElse(null))

                // Dokumen Foto (Untuk verifikasi KTP dan Selfie oleh Backoffice)
                .fotoKtp(dokumenKtpOpt.map(DokumenCustomer::getFileUrl).orElse(null))
                .fotoSelfie(dokumenSelfieOpt.map(DokumenCustomer::getFileUrl).orElse(null))

                // Status Verifikasi
                .statusVerifikasi(statusVerifikasi)
                .catatanVerifikasi(verifikasiOpt.map(VerifikasiCustomer::getCatatanVerifikasi).orElse(null))
                .tanggalPengajuan(customer.getCreatedDate())
                .tanggalVerifikasi(verifikasiOpt.map(VerifikasiCustomer::getCreatedDate).orElse(null))
                .verifiedByKaryawanId(verifikasiOpt.map(VerifikasiCustomer::getMstKaryawanId).orElse(null))
                .build();
    }

    @Transactional
    public VerifikasiCustomerResponse verify(
            UUID customerId, UUID karyawanId, VerifikasiCustomerRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));
        ScoringCustomer scoring = scoringRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId)
                .orElseThrow(() -> new BussinessRuleException("Data scoring customer belum tersedia"));

        String rawStatus = request.getStatusVerifikasi().trim().toUpperCase();
        String status;
        UUID plafondId = null;
        java.math.BigDecimal approvedAmount = java.math.BigDecimal.ZERO;
        String keputusan = scoring.getStatusScoring();

        if (rawStatus.contains("APPROV") || rawStatus.contains("SETUJU")) {
            status = "APPROVED";

            // Hitung skor kredit resmi dari data pekerjaan & finansial nasabah yang telah diverifikasi
            ScoringService.ScoringResult scoringResult = scoringService.calculateScore(
                    scoring.getTotalCicilanLainBulanan() != null ? scoring.getTotalCicilanLainBulanan() : java.math.BigDecimal.ZERO,
                    scoring.getPenghasilanBulanan() != null ? scoring.getPenghasilanBulanan() : java.math.BigDecimal.ZERO,
                    scoring.getLamaBekerjaBulan() != null ? scoring.getLamaBekerjaBulan() : 0,
                    scoring.getStatusPekerjaan());
            int calculatedScore = (int) Math.round(scoringResult.score());
            scoring.setSkor(calculatedScore);

            PlafondCalculationResponse calculation = plafondService.calculateApprovedAmount(
                    scoring.getPenghasilanBulanan(), calculatedScore);
            plafondId = calculation.getPlafondId();
            approvedAmount = calculation.getApprovedAmount();
            keputusan = calculation.getKeputusan();
            if (plafondId != null && !plafondRepository.existsById(plafondId)) {
                plafondId = plafondRepository.findAll().stream()
                        .filter(p -> Boolean.TRUE.equals(p.getStatus()))
                        .map(Plafond::getId)
                        .findFirst()
                        .orElse(null);
            }
            scoring.setMstPlafondId(plafondId);
            customer.setStatus(true);

        } else if (rawStatus.contains("REVISI") || rawStatus.contains("REVISION")) {
            status = "PERLU_REVISI";
            keputusan = "PERLU_REVISI";
            scoring.setSkor(0);
            scoring.setMstPlafondId(null);
            customer.setStatus(false);
        } else {
            status = "REJECTED";
            keputusan = "REJECTED";
            scoring.setSkor(0);
            scoring.setMstPlafondId(null);
            customer.setStatus(false);
        }

        scoring.setStatusScoring(keputusan);
        scoring.setUpdatedDate(LocalDateTime.now());
        scoringRepository.save(scoring);

        customer.setUpdatedDate(LocalDateTime.now());
        customerRepository.save(customer);

        UUID effectiveKaryawanId = karyawanId;
        if (effectiveKaryawanId != null && karyawanRepository != null) {
            boolean exists = false;
            try {
                exists = karyawanRepository.existsById(effectiveKaryawanId);
            } catch (Exception ignored) {
            }
            if (!exists) {
                effectiveKaryawanId = karyawanRepository.findAll().stream()
                        .filter(k -> Boolean.TRUE.equals(k.getStatus()))
                        .map(Karyawan::getId)
                        .findFirst()
                        .orElseGet(() -> karyawanRepository.findAll().stream()
                                .map(Karyawan::getId)
                                .findFirst()
                                .orElse(karyawanId));
            }
        } else if (effectiveKaryawanId == null && karyawanRepository != null) {
            effectiveKaryawanId = karyawanRepository.findAll().stream()
                    .filter(k -> Boolean.TRUE.equals(k.getStatus()))
                    .map(Karyawan::getId)
                    .findFirst()
                    .orElseGet(() -> karyawanRepository.findAll().stream()
                            .map(Karyawan::getId)
                            .findFirst()
                            .orElse(null));
        }

        if (effectiveKaryawanId == null) {
            effectiveKaryawanId = karyawanId != null ? karyawanId : UUID.randomUUID();
        }

        String finalCatatan = request.getCatatanVerifikasi();
        if (finalCatatan == null || finalCatatan.trim().isBlank()) {
            finalCatatan = "APPROVED".equalsIgnoreCase(status)
                    ? "Dokumen identitas (KTP & Foto Selfie) telah diverifikasi dan disetujui."
                    : "PERLU_REVISI".equalsIgnoreCase(status)
                            ? "Dokumen identitas (KTP & Foto Selfie) perlu diperbaiki/diunggah ulang."
                            : "Dokumen identitas (KTP & Foto Selfie) tidak memenuhi syarat verifikasi.";
        }

        VerifikasiCustomer verification = verifikasiRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId)
                .orElseGet(() -> {
                    VerifikasiCustomer v = new VerifikasiCustomer();
                    v.setId(UUID.randomUUID());
                    v.setCreatedDate(LocalDateTime.now());
                    v.setMstCustomerId(customerId);
                    return v;
                });
        verification.setStatusVerifikasi(status);
        verification.setCatatanVerifikasi(finalCatatan.trim());
        verification.setUpdatedDate(LocalDateTime.now());
        verification.setMstKaryawanId(effectiveKaryawanId);
        verifikasiRepository.save(verification);

        if (auditLogService != null) {
            String desc = "Backoffice memverifikasi KYC customer " + customer.getNama() + " (" + customer.getEmail()
                    + ") dengan status: " + status;
            auditLogService.recordLog(effectiveKaryawanId, "VERIFIKASI_KYC", "CUSTOMER", desc);
        }

        if (notifikasiService != null) {
            if ("APPROVED".equalsIgnoreCase(status)) {
                if (notifikasiRepository != null) {
                    notifikasiRepository.deleteByMstCustomerIdAndTrxPengajuanPinjamanIdIsNull(customerId);
                }
                notifikasiService.createNotification(
                        customerId,
                        null,
                        "WELCOME",
                        "IN_APP",
                        "Selamat Datang di SAKU! 🎉",
                        "Akun Anda telah aktif dan terverifikasi. Nikmati kemudahan pengajuan pinjaman cepat dan aman bersama SAKU."
                );
            } else if ("PERLU_REVISI".equalsIgnoreCase(status)) {
                String catatan = (request.getCatatanVerifikasi() != null && !request.getCatatanVerifikasi().isBlank())
                        ? request.getCatatanVerifikasi()
                        : "Dokumen identitas (KTP/Selfie) perlu diperbaiki.";
                notifikasiService.createNotification(
                        customerId,
                        null,
                        "KYC",
                        "IN_APP",
                        "Perlu Revisi Dokumen Identitas",
                        "Dokumen verifikasi akun Anda perlu diperbaiki. Catatan: " + catatan
                );
            } else if ("REJECTED".equalsIgnoreCase(status)) {
                notifikasiService.createNotification(
                        customerId,
                        null,
                        "KYC",
                        "IN_APP",
                        "Verifikasi Akun Belum Berhasil",
                        "Mohon maaf, pengajuan pendaftaran akun Anda saat ini belum memenuhi kriteria kelayakan layanan SAKU."
                );
            }
        }

        return new VerifikasiCustomerResponse(

                customerId, status, request.getCatatanVerifikasi(), scoring.getSkor(),
                keputusan, plafondId, approvedAmount, customer.getStatus());
    }

    private String determineStatus(Customer customer, Optional<VerifikasiCustomer> latestVerificationOpt) {
        if (latestVerificationOpt.isPresent()) {
            return latestVerificationOpt.get().getStatusVerifikasi();
        }
        if (Boolean.TRUE.equals(customer.getStatus())) {
            return "APPROVED";
        }
        return "PENDING";
    }

    private AlamatDetailResponse mapAlamat(AlamatCustomer alamat) {
        if (alamat == null) {
            return null;
        }

        StringBuilder formatted = new StringBuilder();
        if (alamat.getAlamatLengkap() != null) {
            formatted.append(alamat.getAlamatLengkap());
        }
        if (alamat.getRt() != null || alamat.getRw() != null) {
            formatted.append(", RT ").append(alamat.getRt() != null ? alamat.getRt() : "-")
                    .append("/RW ").append(alamat.getRw() != null ? alamat.getRw() : "-");
        }
        if (alamat.getKelurahan() != null) {
            formatted.append(", Kel. ").append(alamat.getKelurahan());
        }
        if (alamat.getKecamatan() != null) {
            formatted.append(", Kec. ").append(alamat.getKecamatan());
        }
        if (alamat.getKotaKabupaten() != null) {
            formatted.append(", ").append(alamat.getKotaKabupaten());
        }
        if (alamat.getProvinsi() != null) {
            formatted.append(", ").append(alamat.getProvinsi());
        }
        if (alamat.getKodePos() != null) {
            formatted.append(" ").append(alamat.getKodePos());
        }

        return AlamatDetailResponse.builder()
                .jenisAlamat(alamat.getJenisAlamat())
                .alamatLengkap(alamat.getAlamatLengkap())
                .rt(alamat.getRt())
                .rw(alamat.getRw())
                .kelurahan(alamat.getKelurahan())
                .kecamatan(alamat.getKecamatan())
                .kotaKabupaten(alamat.getKotaKabupaten())
                .provinsi(alamat.getProvinsi())
                .kodePos(alamat.getKodePos())
                .formattedAddress(formatted.toString().trim())
                .build();
    }
}
