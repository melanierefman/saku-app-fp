package com.bcafinance.backend_saku.features.customer.service;

import com.bcafinance.backend_saku.core.dto.AlamatDetailResponse;
import com.bcafinance.backend_saku.core.entity.AlamatCustomer;
import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.entity.DokumenCustomer;
import com.bcafinance.backend_saku.core.entity.Plafond;
import com.bcafinance.backend_saku.core.entity.ScoringCustomer;
import com.bcafinance.backend_saku.core.entity.VerifikasiCustomer;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.AlamatCustomerRepository;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.repository.DokumenCustomerRepository;
import com.bcafinance.backend_saku.core.realtime.RealTimeEmitterService;
import com.bcafinance.backend_saku.core.realtime.RealTimeEventDto;
import com.bcafinance.backend_saku.core.repository.ScoringCustomerRepository;
import com.bcafinance.backend_saku.core.repository.VerifikasiCustomerRepository;
import com.bcafinance.backend_saku.core.storage.FileStorageService;
import com.bcafinance.backend_saku.features.customer.dto.ChangePasswordRequest;
import com.bcafinance.backend_saku.features.customer.dto.CustomerProfileResponse;
import com.bcafinance.backend_saku.features.customer.dto.UpdateDomisiliRequest;
import com.bcafinance.backend_saku.features.customer.dto.UpdatePekerjaanRequest;
import com.bcafinance.backend_saku.features.customer.dto.UpdateRekeningRequest;
import com.bcafinance.backend_saku.features.scoring.service.ScoringService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerProfileService {

    private final CustomerRepository customerRepository;
    private final AlamatCustomerRepository alamatRepository;
    private final DokumenCustomerRepository dokumenCustomerRepository;
    private final ScoringCustomerRepository scoringRepository;
    private final VerifikasiCustomerRepository verifikasiRepository;
    private final PasswordEncoder passwordEncoder;
    private final ScoringService scoringService;
    private final CustomerPlafondService customerPlafondService;
    private final FileStorageService fileStorageService;
    private final RealTimeEmitterService realTimeEmitterService;

    // Ambil data profil nasabah lengkap dengan alamat, dokumen, dan plafond
    @Transactional(readOnly = true)
    public CustomerProfileResponse getProfile(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));
        return buildProfileResponse(customer);
    }

    // Perbarui nomor rekening bank pencairan customer
    @Transactional
    public CustomerProfileResponse updateRekening(UUID customerId, UpdateRekeningRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));

        customer.setNamaBank(request.getNamaBank().trim());
        customer.setNoRekening(request.getNoRekening().trim());
        customer.setNamaRekening(request.getNamaRekening().trim());
        customer.setUpdatedDate(LocalDateTime.now());

        Customer saved = customerRepository.save(customer);
        log.info("Updated bank account for customer {}", customerId);
        return buildProfileResponse(saved);
    }

    // Perbarui alamat domisili customer
    @Transactional
    public CustomerProfileResponse updateDomisili(UUID customerId, UpdateDomisiliRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));

        Optional<AlamatCustomer> domisiliOpt = alamatRepository.findByCustomer_IdAndJenisAlamat(customerId, "DOMISILI");
        AlamatCustomer domisili = domisiliOpt.orElseGet(() -> {
            AlamatCustomer a = new AlamatCustomer();
            a.setId(UUID.randomUUID());
            a.setCustomer(customer);
            a.setJenisAlamat("DOMISILI");
            a.setCreatedDate(LocalDateTime.now());
            return a;
        });

        domisili.setAlamatLengkap(request.getAlamatLengkap().trim());
        domisili.setRt(request.getRt().trim());
        domisili.setRw(request.getRw().trim());
        domisili.setKelurahan(request.getKelurahan().trim());
        domisili.setKecamatan(request.getKecamatan().trim());
        domisili.setKotaKabupaten(request.getKotaKabupaten().trim());
        domisili.setProvinsi(request.getProvinsi().trim());
        domisili.setKodePos(request.getKodePos().trim());
        domisili.setUpdatedDate(LocalDateTime.now());

        alamatRepository.save(domisili);
        log.info("Updated domisili address for customer {}", customerId);
        return buildProfileResponse(customer);
    }

    // Perbarui informasi pekerjaan nasabah dan kalkulasi ulang skor kredit
    @Transactional
    public CustomerProfileResponse updatePekerjaan(UUID customerId, UpdatePekerjaanRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));

        Optional<ScoringCustomer> scoringOpt = scoringRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId);
        ScoringCustomer scoring = scoringOpt.orElseGet(() -> {
            ScoringCustomer s = new ScoringCustomer();
            s.setId(UUID.randomUUID());
            s.setMstCustomerId(customerId);
            s.setCreatedDate(LocalDateTime.now());
            return s;
        });

        scoring.setPekerjaan(request.getPekerjaan().trim());
        scoring.setTempatKerja(request.getTempatKerja().trim());
        scoring.setStatusPekerjaan(request.getStatusPekerjaan().trim());
        scoring.setPenghasilanBulanan(request.getPenghasilanBulanan());
        scoring.setLamaBekerjaBulan(request.getLamaBekerjaBulan());
        if (request.getTotalCicilanLainBulanan() != null) {
            scoring.setTotalCicilanLainBulanan(request.getTotalCicilanLainBulanan());
        }

        recalculateAndSaveScoring(customer, scoring);
        log.info("Updated employment info and recalculated score for customer {}", customerId);
        return buildProfileResponse(customer);
    }

    // Ubah password akun nasabah dengan validasi password lama
    @Transactional
    public void changePassword(UUID customerId, ChangePasswordRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));

        if (!passwordEncoder.matches(request.getOldPassword(), customer.getPassword())) {
            throw new BussinessRuleException("Password lama yang Anda masukkan salah");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BussinessRuleException("Password baru dan konfirmasi password tidak cocok");
        }

        if (passwordEncoder.matches(request.getNewPassword(), customer.getPassword())) {
            throw new BussinessRuleException("Password baru tidak boleh sama dengan password saat ini");
        }

        customer.setPassword(passwordEncoder.encode(request.getNewPassword()));
        customer.setUpdatedDate(LocalDateTime.now());
        customerRepository.save(customer);
        log.info("Password changed successfully for customer {}", customerId);
    }

    // Upload ulang dokumen KYC (KTP & Selfie) untuk verifikasi ulang
    @Transactional
    public CustomerProfileResponse updateKycDocuments(
            UUID customerId,
            MultipartFile ktpFile,
            MultipartFile selfieFile) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));

        boolean hasKtp = ktpFile != null && !ktpFile.isEmpty();
        boolean hasSelfie = selfieFile != null && !selfieFile.isEmpty();

        if (!hasKtp && !hasSelfie) {
            throw new BussinessRuleException("Silakan pilih minimal satu dokumen KTP atau Selfie untuk diunggah");
        }

        if (hasKtp) {
            String ktpUrl = fileStorageService.store(ktpFile, "ktp/" + customerId);
            Optional<DokumenCustomer> existingKtpOpt = dokumenCustomerRepository.findByCustomer_IdAndDocType(customerId, "KTP");
            DokumenCustomer docKtp = existingKtpOpt.orElseGet(() -> {
                DokumenCustomer d = new DokumenCustomer();
                d.setId(UUID.randomUUID());
                d.setDocType("KTP");
                d.setCustomer(customer);
                d.setCreatedDate(LocalDateTime.now());
                return d;
            });
            docKtp.setFileUrl(ktpUrl);
            docKtp.setUpdatedDate(LocalDateTime.now());
            dokumenCustomerRepository.save(docKtp);
        }

        if (hasSelfie) {
            String selfieUrl = fileStorageService.store(selfieFile, "selfie/" + customerId);
            Optional<DokumenCustomer> existingSelfieOpt = dokumenCustomerRepository.findByCustomer_IdAndDocType(customerId, "SELFIE");
            DokumenCustomer docSelfie = existingSelfieOpt.orElseGet(() -> {
                DokumenCustomer d = new DokumenCustomer();
                d.setId(UUID.randomUUID());
                d.setDocType("SELFIE");
                d.setCustomer(customer);
                d.setCreatedDate(LocalDateTime.now());
                return d;
            });
            docSelfie.setFileUrl(selfieUrl);
            docSelfie.setUpdatedDate(LocalDateTime.now());
            dokumenCustomerRepository.save(docSelfie);
        }

        verifikasiRepository.deleteByMstCustomerId(customerId);
        customer.setStatus(false);
        customer.setUpdatedDate(LocalDateTime.now());
        customerRepository.save(customer);

        log.info("Customer {} re-uploaded KYC documents for revision", customerId);

        if (realTimeEmitterService != null) {
            realTimeEmitterService.broadcast(RealTimeEventDto.builder()
                    .eventType("KYC_REVISED")
                    .referenceId(customerId.toString())
                    .customerName(customer.getNama())
                    .title("Revisi Dokumen KYC")
                    .message("Nasabah " + customer.getNama() + " telah mengunggah revisi dokumen identitas.")
                    .targetRoles(List.of("ROLE_BACKOFFICE", "ROLE_SUPERADMIN"))
                    .timestamp(LocalDateTime.now())
                    .build());
        }

        return buildProfileResponse(customer);
    }

    // Perbarui token FCM customer untuk push notification
    @Transactional
    public void updateFcmToken(UUID customerId, String fcmToken) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));
        customer.setFcmToken(fcmToken);
        customer.setUpdatedDate(LocalDateTime.now());
        customerRepository.save(customer);
        log.info("FCM token updated successfully for customer id: {}", customerId);
    }

    // Susun DTO response data profil nasabah
    private CustomerProfileResponse buildProfileResponse(Customer customer) {
        UUID customerId = customer.getId();

        Optional<AlamatCustomer> alamatKtpOpt = alamatRepository.findByCustomer_IdAndJenisAlamat(customerId, "KTP");
        Optional<AlamatCustomer> alamatDomisiliOpt = alamatRepository.findByCustomer_IdAndJenisAlamat(customerId, "DOMISILI");

        Optional<DokumenCustomer> ktpDocOpt = dokumenCustomerRepository.findByCustomer_IdAndDocType(customerId, "KTP");
        Optional<DokumenCustomer> selfieDocOpt = dokumenCustomerRepository.findByCustomer_IdAndDocType(customerId, "SELFIE");

        Optional<ScoringCustomer> scoringOpt = scoringRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId);
        Optional<VerifikasiCustomer> verifikasiOpt = verifikasiRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId);

        String statusVerifikasi = determineStatusVerifikasi(customer, verifikasiOpt);
        boolean isKycVerified = "TERVERIFIKASI".equals(statusVerifikasi) || Boolean.TRUE.equals(customer.getStatus());

        CustomerPlafondService.CustomerPlafondSummary plafondSummary = customerPlafondService.calculatePlafondSummary(customerId);

        return CustomerProfileResponse.builder()
                .id(customer.getId())
                .nik(customer.getNik())
                .nama(customer.getNama())
                .username(customer.getUsername())
                .email(customer.getEmail())
                .noHp(customer.getNoHp())
                .namaIbuKandung(customer.getNamaIbuKandung())
                .namaBank(customer.getNamaBank())
                .noRekening(customer.getNoRekening())
                .namaRekening(customer.getNamaRekening())
                .status(customer.getStatus())
                .isKycVerified(isKycVerified)
                .statusVerifikasi(statusVerifikasi)
                .catatanVerifikasi(verifikasiOpt.map(VerifikasiCustomer::getCatatanVerifikasi).orElse(null))
                .tanggalVerifikasi(verifikasiOpt.map(v -> v.getUpdatedDate() != null ? v.getUpdatedDate() : v.getCreatedDate()).orElse(null))
                .alamatKtp(alamatKtpOpt.map(this::mapAlamat).orElse(null))
                .alamatDomisili(alamatDomisiliOpt.map(this::mapAlamat).orElse(null))
                .fotoKtp(ktpDocOpt.map(DokumenCustomer::getFileUrl).orElse(null))
                .fotoSelfie(selfieDocOpt.map(DokumenCustomer::getFileUrl).orElse(null))
                .pekerjaan(scoringOpt.map(ScoringCustomer::getPekerjaan).orElse(null))
                .tempatKerja(scoringOpt.map(ScoringCustomer::getTempatKerja).orElse(null))
                .statusPekerjaan(scoringOpt.map(ScoringCustomer::getStatusPekerjaan).orElse(null))
                .penghasilanBulanan(scoringOpt.map(ScoringCustomer::getPenghasilanBulanan).orElse(null))
                .lamaBekerjaBulan(scoringOpt.map(ScoringCustomer::getLamaBekerjaBulan).orElse(null))
                .totalCicilanLainBulanan(scoringOpt.map(ScoringCustomer::getTotalCicilanLainBulanan).orElse(null))
                .skorKredit(scoringOpt.map(ScoringCustomer::getSkor).orElse(null))
                .statusScoring(scoringOpt.map(ScoringCustomer::getStatusScoring).orElse(null))
                .totalPlafond(plafondSummary.totalPlafond())
                .usedPlafond(plafondSummary.usedPlafond())
                .availablePlafond(plafondSummary.availablePlafond())
                .tierPlafond(plafondSummary.tierName())
                .sukuBunga(plafondSummary.sukuBunga())
                .biayaAdmin(plafondSummary.biayaAdmin())
                .createdDate(customer.getCreatedDate())
                .build();
    }

    // Tentukan status verifikasi KYC customer
    private String determineStatusVerifikasi(Customer customer, Optional<VerifikasiCustomer> verifikasiOpt) {
        if (Boolean.TRUE.equals(customer.getStatus())) {
            return "TERVERIFIKASI";
        }
        if (verifikasiOpt.isPresent()) {
            return verifikasiOpt.get().getStatusVerifikasi();
        }
        return "MENUNGGU_VERIFIKASI";
    }

    // Pemetaan entitas alamat ke format DTO respons
    private AlamatDetailResponse mapAlamat(AlamatCustomer alamat) {
        if (alamat == null) return null;

        StringBuilder formatted = new StringBuilder();
        if (alamat.getAlamatLengkap() != null) formatted.append(alamat.getAlamatLengkap());
        if (alamat.getRt() != null || alamat.getRw() != null) {
            formatted.append(", RT ").append(alamat.getRt() != null ? alamat.getRt() : "-")
                    .append("/RW ").append(alamat.getRw() != null ? alamat.getRw() : "-");
        }
        if (alamat.getKelurahan() != null) formatted.append(", Kel. ").append(alamat.getKelurahan());
        if (alamat.getKecamatan() != null) formatted.append(", Kec. ").append(alamat.getKecamatan());
        if (alamat.getKotaKabupaten() != null) formatted.append(", ").append(alamat.getKotaKabupaten());
        if (alamat.getProvinsi() != null) formatted.append(", ").append(alamat.getProvinsi());
        if (alamat.getKodePos() != null) formatted.append(" ").append(alamat.getKodePos());

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

    // Hitung ulang skor kredit dan simpan hasil scoring
    private ScoringCustomer recalculateAndSaveScoring(Customer customer, ScoringCustomer scoring) {
        BigDecimal pendapatan = scoring.getPenghasilanBulanan() != null ? scoring.getPenghasilanBulanan() : BigDecimal.ZERO;
        BigDecimal cicilan = scoring.getTotalCicilanLainBulanan() != null ? scoring.getTotalCicilanLainBulanan() : BigDecimal.ZERO;
        int lamaBekerja = scoring.getLamaBekerjaBulan() != null ? scoring.getLamaBekerjaBulan() : 0;
        String statusPekerjaan = scoring.getStatusPekerjaan() != null ? scoring.getStatusPekerjaan() : "KARYAWAN_TETAP";

        if (pendapatan.compareTo(BigDecimal.ZERO) > 0) {
            ScoringService.ScoringResult res =
                    scoringService.calculateScore(cicilan, pendapatan, lamaBekerja, statusPekerjaan);
            scoring.setSkor((int) Math.round(res.score()));
            scoring.setStatusScoring(res.decision());

            Plafond p = customerPlafondService.resolveCustomerPlafond(scoring);
            if (p != null) {
                scoring.setMstPlafondId(p.getId());
            }
        }

        scoring.setUpdatedDate(LocalDateTime.now());
        return scoringRepository.save(scoring);
    }
}
