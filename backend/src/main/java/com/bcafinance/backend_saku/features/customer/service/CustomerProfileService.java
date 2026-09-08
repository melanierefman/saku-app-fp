package com.bcafinance.backend_saku.features.customer.service;

import com.bcafinance.backend_saku.core.dto.AlamatDetailResponse;
import com.bcafinance.backend_saku.core.entity.AlamatCustomer;
import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.entity.DokumenCustomer;
import com.bcafinance.backend_saku.core.entity.ScoringCustomer;
import com.bcafinance.backend_saku.core.entity.VerifikasiCustomer;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.AlamatCustomerRepository;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.repository.DokumenCustomerRepository;
import com.bcafinance.backend_saku.core.repository.ScoringCustomerRepository;
import com.bcafinance.backend_saku.core.repository.VerifikasiCustomerRepository;
import com.bcafinance.backend_saku.features.customer.dto.ChangePasswordRequest;
import com.bcafinance.backend_saku.features.customer.dto.CustomerProfileResponse;
import com.bcafinance.backend_saku.features.customer.dto.UpdateDomisiliRequest;
import com.bcafinance.backend_saku.features.customer.dto.UpdatePekerjaanRequest;
import com.bcafinance.backend_saku.features.customer.dto.UpdateProfileRequest;
import com.bcafinance.backend_saku.features.customer.dto.UpdateRekeningRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final com.bcafinance.backend_saku.features.scoring.service.ScoringService scoringService;
    private final CustomerPlafondService customerPlafondService;


    @Transactional(readOnly = true)
    public CustomerProfileResponse getProfile(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));

        return buildProfileResponse(customer);
    }

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

    @Transactional
    public CustomerProfileResponse updateProfile(UUID customerId, UpdateProfileRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));

        boolean customerUpdated = false;
        if (request.getNoHp() != null && !request.getNoHp().isBlank()) {
            customer.setNoHp(request.getNoHp().trim());
            customerUpdated = true;
        }
        if (request.getNamaBank() != null && !request.getNamaBank().isBlank()) {
            customer.setNamaBank(request.getNamaBank().trim());
            customerUpdated = true;
        }
        if (request.getNoRekening() != null && !request.getNoRekening().isBlank()) {
            customer.setNoRekening(request.getNoRekening().trim());
            customerUpdated = true;
        }
        if (request.getNamaRekening() != null && !request.getNamaRekening().isBlank()) {
            customer.setNamaRekening(request.getNamaRekening().trim());
            customerUpdated = true;
        }
        if (customerUpdated) {
            customer.setUpdatedDate(LocalDateTime.now());
            customer = customerRepository.save(customer);
        }

        final Customer finalCustomer = customer;

        // Update Domisili jika ada
        if (request.getDomisiliAlamatLengkap() != null && !request.getDomisiliAlamatLengkap().isBlank()) {
            Optional<AlamatCustomer> domisiliOpt = alamatRepository.findByCustomer_IdAndJenisAlamat(customerId, "DOMISILI");
            AlamatCustomer domisili = domisiliOpt.orElseGet(() -> {
                AlamatCustomer a = new AlamatCustomer();
                a.setId(UUID.randomUUID());
                a.setCustomer(finalCustomer);
                a.setJenisAlamat("DOMISILI");
                a.setCreatedDate(LocalDateTime.now());
                return a;
            });

            domisili.setAlamatLengkap(request.getDomisiliAlamatLengkap().trim());
            if (request.getDomisiliRt() != null) domisili.setRt(request.getDomisiliRt().trim());
            if (request.getDomisiliRw() != null) domisili.setRw(request.getDomisiliRw().trim());
            if (request.getDomisiliKelurahan() != null) domisili.setKelurahan(request.getDomisiliKelurahan().trim());
            if (request.getDomisiliKecamatan() != null) domisili.setKecamatan(request.getDomisiliKecamatan().trim());
            if (request.getDomisiliKotaKabupaten() != null) domisili.setKotaKabupaten(request.getDomisiliKotaKabupaten().trim());
            if (request.getDomisiliProvinsi() != null) domisili.setProvinsi(request.getDomisiliProvinsi().trim());
            if (request.getDomisiliKodePos() != null) domisili.setKodePos(request.getDomisiliKodePos().trim());
            domisili.setUpdatedDate(LocalDateTime.now());
            alamatRepository.save(domisili);
        }

        // Update Pekerjaan jika ada
        if (request.getPekerjaan() != null && !request.getPekerjaan().isBlank()) {
            Optional<ScoringCustomer> scoringOpt = scoringRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId);
            ScoringCustomer scoring = scoringOpt.orElseGet(() -> {
                ScoringCustomer s = new ScoringCustomer();
                s.setId(UUID.randomUUID());
                s.setMstCustomerId(customerId);
                s.setCreatedDate(LocalDateTime.now());
                return s;
            });

            scoring.setPekerjaan(request.getPekerjaan().trim());
            if (request.getTempatKerja() != null) scoring.setTempatKerja(request.getTempatKerja().trim());
            if (request.getStatusPekerjaan() != null) scoring.setStatusPekerjaan(request.getStatusPekerjaan().trim());
            if (request.getPenghasilanBulanan() != null) scoring.setPenghasilanBulanan(request.getPenghasilanBulanan());
            if (request.getLamaBekerjaBulan() != null) scoring.setLamaBekerjaBulan(request.getLamaBekerjaBulan());
            if (request.getTotalCicilanLainBulanan() != null) scoring.setTotalCicilanLainBulanan(request.getTotalCicilanLainBulanan());

            recalculateAndSaveScoring(customer, scoring);
        }

        return buildProfileResponse(customer);
    }


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
                .namaBank(customer.getNamaBank())
                .noRekening(customer.getNoRekening())
                .namaRekening(customer.getNamaRekening())
                .status(customer.getStatus())
                .isKycVerified(isKycVerified)
                .statusVerifikasi(statusVerifikasi)
                .catatanVerifikasi(verifikasiOpt.map(VerifikasiCustomer::getCatatanVerifikasi).orElse(null))
                .tanggalVerifikasi(verifikasiOpt.map(VerifikasiCustomer::getCreatedDate).orElse(null))
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
                .createdDate(customer.getCreatedDate())
                .build();
    }

    private String determineStatusVerifikasi(Customer customer, Optional<VerifikasiCustomer> verifikasiOpt) {
        if (Boolean.TRUE.equals(customer.getStatus())) {
            return "TERVERIFIKASI";
        }
        if (verifikasiOpt.isPresent()) {
            return verifikasiOpt.get().getStatusVerifikasi();
        }
        return "MENUNGGU_VERIFIKASI";
    }

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

    private ScoringCustomer recalculateAndSaveScoring(Customer customer, ScoringCustomer scoring) {
        BigDecimal pendapatan = scoring.getPenghasilanBulanan() != null ? scoring.getPenghasilanBulanan() : BigDecimal.ZERO;
        BigDecimal cicilan = scoring.getTotalCicilanLainBulanan() != null ? scoring.getTotalCicilanLainBulanan() : BigDecimal.ZERO;
        int lamaBekerja = scoring.getLamaBekerjaBulan() != null ? scoring.getLamaBekerjaBulan() : 0;
        String statusPekerjaan = scoring.getStatusPekerjaan() != null ? scoring.getStatusPekerjaan() : "KARYAWAN_TETAP";

        if (pendapatan.compareTo(BigDecimal.ZERO) > 0) {
            com.bcafinance.backend_saku.features.scoring.service.ScoringService.ScoringResult res =
                    scoringService.calculateScore(cicilan, pendapatan, lamaBekerja, statusPekerjaan);
            scoring.setSkor((int) Math.round(res.score()));
            scoring.setStatusScoring(res.decision());
        }

        scoring.setUpdatedDate(LocalDateTime.now());
        return scoringRepository.save(scoring);
    }
}

