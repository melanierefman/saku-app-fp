package com.bcafinance.backend_saku.service;

import com.bcafinance.backend_saku.dto.AlamatDetailResponse;
import com.bcafinance.backend_saku.dto.PendingCustomerResponse;
import com.bcafinance.backend_saku.dto.PlafondCalculationResponse;
import com.bcafinance.backend_saku.dto.VerifikasiCustomerDetailResponse;
import com.bcafinance.backend_saku.dto.VerifikasiCustomerItemResponse;
import com.bcafinance.backend_saku.dto.VerifikasiCustomerRequest;
import com.bcafinance.backend_saku.dto.VerifikasiCustomerResponse;
import com.bcafinance.backend_saku.entity.AlamatCustomer;
import com.bcafinance.backend_saku.entity.Customer;
import com.bcafinance.backend_saku.entity.DokumenCustomer;
import com.bcafinance.backend_saku.entity.ScoringCustomer;
import com.bcafinance.backend_saku.entity.VerifikasiCustomer;
import com.bcafinance.backend_saku.exception.BussinessRuleException;
import com.bcafinance.backend_saku.repository.AlamatCustomerRepository;
import com.bcafinance.backend_saku.repository.CustomerRepository;
import com.bcafinance.backend_saku.repository.DokumenCustomerRepository;
import com.bcafinance.backend_saku.repository.ScoringCustomerRepository;
import com.bcafinance.backend_saku.repository.VerifikasiCustomerRepository;
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

    public List<VerifikasiCustomerItemResponse> findAll(String statusFilter) {
        List<Customer> customers = customerRepository.findAllByOrderByCreatedDateDesc();

        return customers.stream()
                .map(customer -> {
                    Optional<VerifikasiCustomer> latestVerification =
                            verifikasiRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customer.getId());
                    String status = determineStatus(customer, latestVerification);
                    String catatan = latestVerification.map(VerifikasiCustomer::getCatatanVerifikasi).orElse(null);
                    LocalDateTime tglVerifikasi = latestVerification.map(VerifikasiCustomer::getCreatedDate).orElse(null);

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
        return customerRepository.findAllByStatusFalseOrderByCreatedDateAsc().stream()
                .map(customer -> {
                    Optional<VerifikasiCustomer> latestVerification =
                            verifikasiRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customer.getId());
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

    public VerifikasiCustomerDetailResponse getDetail(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));

        Optional<ScoringCustomer> scoringOpt = scoringRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId);
        Optional<VerifikasiCustomer> verifikasiOpt = verifikasiRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId);

        Optional<AlamatCustomer> alamatKtpOpt = alamatRepository.findByCustomer_IdAndJenisAlamat(customerId, "KTP");
        Optional<AlamatCustomer> alamatDomisiliOpt = alamatRepository.findByCustomer_IdAndJenisAlamat(customerId, "DOMISILI");

        Optional<DokumenCustomer> dokumenKtpOpt = dokumenRepository.findByCustomer_IdAndDocType(customerId, "KTP");
        Optional<DokumenCustomer> dokumenSelfieOpt = dokumenRepository.findByCustomer_IdAndDocType(customerId, "SELFIE");

        String statusVerifikasi = determineStatus(customer, verifikasiOpt);

        return VerifikasiCustomerDetailResponse.builder()
                .customerId(customer.getId())
                .namaLengkap(customer.getNama())
                .nik(customer.getNik())
                .email(customer.getEmail())
                .noHp(customer.getNoHp())
                .username(customer.getUsername())

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

        String status = request.getStatusVerifikasi().toUpperCase();
        UUID plafondId = null;
        java.math.BigDecimal approvedAmount = java.math.BigDecimal.ZERO;
        String keputusan = scoring.getStatusScoring();

        if ("APPROVED".equals(status)) {
            if (scoring.getSkor() < 60) {
                throw new BussinessRuleException("Customer tidak memenuhi skor minimum untuk approval");
            }

            PlafondCalculationResponse calculation = plafondService.calculateApprovedAmount(
                    scoring.getPenghasilanBulanan(), scoring.getSkor());
            plafondId = calculation.getPlafondId();
            approvedAmount = calculation.getApprovedAmount();
            keputusan = calculation.getKeputusan();
            scoring.setMstPlafondId(plafondId);
            customer.setStatus(true);
        } else {
            scoring.setMstPlafondId(null);
            customer.setStatus(false);
        }

        scoring.setStatusScoring(keputusan);
        scoring.setUpdatedDate(LocalDateTime.now());
        scoringRepository.save(scoring);

        customer.setUpdatedDate(LocalDateTime.now());
        customerRepository.save(customer);

        VerifikasiCustomer verification = new VerifikasiCustomer();
        verification.setId(UUID.randomUUID());
        verification.setStatusVerifikasi(status);
        verification.setCatatanVerifikasi(request.getCatatanVerifikasi());
        verification.setCreatedDate(LocalDateTime.now());
        verification.setUpdatedDate(LocalDateTime.now());
        verification.setMstCustomerId(customerId);
        verification.setMstKaryawanId(karyawanId);
        verifikasiRepository.save(verification);

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
