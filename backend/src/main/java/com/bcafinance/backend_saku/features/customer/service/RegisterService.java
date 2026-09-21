package com.bcafinance.backend_saku.features.customer.service;

import com.bcafinance.backend_saku.core.entity.AlamatCustomer;
import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.entity.DokumenCustomer;
import com.bcafinance.backend_saku.core.entity.ScoringCustomer;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.AlamatCustomerRepository;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.repository.DokumenCustomerRepository;
import com.bcafinance.backend_saku.core.repository.ScoringCustomerRepository;
import com.bcafinance.backend_saku.core.repository.VerifikasiCustomerRepository;
import com.bcafinance.backend_saku.core.storage.FileStorageService;
import com.bcafinance.backend_saku.features.auth.dto.VerifyOtpRequest;
import com.bcafinance.backend_saku.features.auth.service.OtpService;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep1KtpRequest;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep1Request;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep2PersonalRequest;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep2Request;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep3Request;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep5CompleteRequest;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStepResponse;
import com.bcafinance.backend_saku.features.scoring.service.ScoringService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final CustomerRepository customerRepository;
    private final AlamatCustomerRepository alamatRepository;
    private final DokumenCustomerRepository dokumenRepository;
    private final ScoringCustomerRepository scoringRepository;
    private final VerifikasiCustomerRepository verifikasiRepository;
    private final com.bcafinance.backend_saku.core.repository.PlafondRepository plafondRepository;
    private final ScoringService scoringService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    public boolean checkNik(String nik, UUID customerId) {
        if (nik == null || !nik.matches("^[0-9]{16}$")) {
            throw new BussinessRuleException("NIK harus terdiri dari 16 digit angka");
        }
        boolean exists = (customerId != null)
                ? customerRepository.existsByNikAndIdNot(nik, customerId)
                : customerRepository.existsByNik(nik);
        if (exists) {
            throw new BussinessRuleException("NIK sudah terdaftar pada akun SAKU lain");
        }
        return true;
    }

    public boolean checkPhone(String phone, UUID customerId) {
        String clean = phone != null ? phone.replace("+", "").trim() : "";
        if (!clean.matches("^(08|628)[0-9]{8,12}$")) {
            throw new BussinessRuleException("Nomor handphone tidak valid (wajib diawali 08)");
        }
        boolean exists = (customerId != null)
                ? customerRepository.existsByNoHpAndIdNot(phone, customerId)
                : customerRepository.existsByNoHp(phone);
        if (exists) {
            throw new BussinessRuleException("Nomor handphone sudah terdaftar pada akun SAKU lain");
        }
        return true;
    }
    private final FileStorageService fileStorageService;

    // Alur Registrasi Baru (5-Step KYC & Email Only)
    /**
     * Step 1: Scan e-KTP & Konfirmasi Data OCR
     */
    @Transactional
    public RegisterStepResponse registerStep1Ktp(UUID customerId, MultipartFile ktpFile, RegisterStep1KtpRequest req) {
        Customer customer = getCustomerOrThrow(customerId);

        if (customerRepository.existsByNikAndIdNot(req.nik(), customerId)) {
            throw new BussinessRuleException("NIK sudah terdaftar dalam sistem");
        }

        customer.setNik(req.nik());
        customer.setNama(req.namaLengkap());
        customer.setUpdatedDate(LocalDateTime.now());
        customerRepository.save(customer);

        // Simpan / update alamat KTP
        alamatRepository.deleteByCustomer_IdAndJenisAlamat(customerId, "KTP");
        alamatRepository.save(toEntity(req.alamatKtp(), "KTP", customer));

        // Simpan dokumen foto e-KTP jika diunggah
        if (ktpFile != null && !ktpFile.isEmpty()) {
            String ktpUrl = fileStorageService.store(ktpFile, "ktp/" + customerId);
            Optional<DokumenCustomer> existingKtpOpt = dokumenRepository.findByCustomer_IdAndDocType(customerId, "KTP");
            DokumenCustomer docKtp = existingKtpOpt.orElseGet(() -> buildDokumen("KTP", ktpUrl, customer));
            docKtp.setFileUrl(ktpUrl);
            docKtp.setUpdatedDate(LocalDateTime.now());
            dokumenRepository.save(docKtp);
        }

        return new RegisterStepResponse(customerId, 1, "Data e-KTP berhasil disimpan, lanjut ke data pribadi");
    }

    /**
     * Step 2: Data Pribadi, Pekerjaan & Rekening Bank
     */
    @Transactional
    public RegisterStepResponse registerStep2Personal(UUID customerId, RegisterStep2PersonalRequest req) {
        Customer customer = getCustomerOrThrow(customerId);

        if (customerRepository.existsByNoHpAndIdNot(req.noHp(), customerId)) {
            throw new BussinessRuleException("Nomor handphone sudah terdaftar");
        }

        customer.setNoHp(req.noHp());
        customer.setNamaBank(req.namaBank());
        customer.setNoRekening(req.noRekening());
        customer.setNamaRekening(req.namaRekening());
        customer.setNamaIbuKandung(req.namaIbuKandung());
        customer.setUpdatedDate(LocalDateTime.now());
        customerRepository.save(customer);

        // Simpan Alamat Domisili
        alamatRepository.deleteByCustomer_IdAndJenisAlamat(customerId, "DOMISILI");
        Optional<AlamatCustomer> ktpAlamatOpt = alamatRepository.findByCustomer_IdAndJenisAlamat(customerId, "KTP");
        if (Boolean.TRUE.equals(req.sameAsKtp()) || req.alamatDomisili() == null) {
            if (ktpAlamatOpt.isPresent()) {
                AlamatCustomer ktp = ktpAlamatOpt.get();
                AlamatCustomer domisili = new AlamatCustomer();
                domisili.setId(UUID.randomUUID());
                domisili.setJenisAlamat("DOMISILI");
                domisili.setAlamatLengkap(ktp.getAlamatLengkap());
                domisili.setRt(ktp.getRt());
                domisili.setRw(ktp.getRw());
                domisili.setKelurahan(ktp.getKelurahan());
                domisili.setKecamatan(ktp.getKecamatan());
                domisili.setKotaKabupaten(ktp.getKotaKabupaten());
                domisili.setProvinsi(ktp.getProvinsi());
                domisili.setKodePos(ktp.getKodePos());
                domisili.setCustomer(customer);
                domisili.setCreatedDate(LocalDateTime.now());
                domisili.setUpdatedDate(LocalDateTime.now());
                alamatRepository.save(domisili);
            } else if (req.alamatDomisili() != null) {
                alamatRepository.save(toEntity(req.alamatDomisili(), "DOMISILI", customer));
            }
        } else {
            alamatRepository.save(toEntity(req.alamatDomisili(), "DOMISILI", customer));
        }

        // Simpan data finansial & pekerjaan untuk scoring (Akan dihitung saat verifikasi Backoffice)
        scoringRepository.deleteByMstCustomerId(customer.getId());

        ScoringCustomer scoring = new ScoringCustomer();
        scoring.setId(UUID.randomUUID());
        scoring.setPenghasilanBulanan(req.pendapatan());
        scoring.setStatusPekerjaan(req.statusPekerjaan());
        scoring.setLamaBekerjaBulan(req.lamaBekerjaBulan());
        scoring.setTotalCicilanLainBulanan(req.totalCicilanLainnya());
        scoring.setMstCustomerId(customer.getId());
        scoring.setPekerjaan(req.pekerjaan());
        scoring.setTempatKerja(req.tempatKerja());
        scoring.setSkor(0);
        scoring.setStatusScoring("PENDING_VERIFIKASI");
        scoring.setMstPlafondId(null);
        scoring.setCreatedDate(LocalDateTime.now());
        scoring.setUpdatedDate(LocalDateTime.now());
        scoringRepository.save(scoring);

        return new RegisterStepResponse(customerId, 2, "Data pribadi & pekerjaan tersimpan, lanjut ke verifikasi wajah");
    }

    /**
     * Step 3: Verifikasi Wajah (Liveness Selfie)
     */
    @Transactional
    public RegisterStepResponse registerStep3Liveness(UUID customerId, MultipartFile selfieFile) {
        Customer customer = getCustomerOrThrow(customerId);

        if (selfieFile == null || selfieFile.isEmpty()) {
            Optional<DokumenCustomer> existingSelfieOpt = dokumenRepository.findByCustomer_IdAndDocType(customerId, "SELFIE");
            if (existingSelfieOpt.isEmpty()) {
                throw new BussinessRuleException("Foto selfie wajah wajib diunggah");
            }
        } else {
            String selfieUrl = fileStorageService.store(selfieFile, "selfie/" + customerId);
            Optional<DokumenCustomer> existingSelfieOpt = dokumenRepository.findByCustomer_IdAndDocType(customerId, "SELFIE");
            DokumenCustomer docSelfie = existingSelfieOpt.orElseGet(() -> buildDokumen("SELFIE", selfieUrl, customer));
            docSelfie.setFileUrl(selfieUrl);
            docSelfie.setUpdatedDate(LocalDateTime.now());
            dokumenRepository.save(docSelfie);
        }

        return new RegisterStepResponse(customerId, 3, "Verifikasi wajah berhasil, lanjut ke Syarat & Ketentuan");
    }

    /**
     * Step 4: Syarat & Ketentuan
     */
    @Transactional
    public RegisterStepResponse registerStep4Tnc(UUID customerId) {
        getCustomerOrThrow(customerId);
        return new RegisterStepResponse(customerId, 4, "Syarat & Ketentuan disetujui, lanjut ke pembuatan password");
    }

    /**
     * Step 5: Buat Kredensial Password (Final Step — Email Only)
     */
    @Transactional
    public RegisterStepResponse registerStep5Complete(UUID customerId, RegisterStep5CompleteRequest req) {
        Customer customer = getCustomerOrThrow(customerId);

        if (!req.password().equals(req.confirmPassword())) {
            throw new BussinessRuleException("Password dan konfirmasi password tidak sama");
        }

        if (req.password().length() < 8) {
            throw new BussinessRuleException("Password minimal 8 karakter");
        }

        // Email digunakan sebagai username utama akun nasabah
        customer.setUsername(customer.getEmail());
        customer.setPassword(passwordEncoder.encode(req.password()));
        customer.setStatus(false); // Menunggu verifikasi Backoffice
        customer.setUpdatedDate(LocalDateTime.now());
        customerRepository.save(customer);

        // Hapus catatan verifikasi lama jika pernah revisi
        verifikasiRepository.deleteByMstCustomerId(customerId);

        return new RegisterStepResponse(customerId, 5,
                "Pendaftaran berhasil! Akun Anda sedang dalam proses verifikasi Backoffice");
    }

    // Alur Registrasi Legacy (Backward Compatibility)
    @Transactional
    public RegisterStepResponse registerStep1(RegisterStep1Request req) {
        if (!req.password().equals(req.confirmPassword()))
            throw new BussinessRuleException("Password dan konfirmasi password tidak sama");

        Optional<Customer> existingCustOpt = customerRepository.findByEmail(req.email());
        Customer customer;
        if (existingCustOpt.isPresent()) {
            Customer existing = existingCustOpt.get();
            if (Boolean.TRUE.equals(existing.getStatus())) {
                throw new BussinessRuleException("Email sudah terdaftar dan akun sudah aktif");
            }
            if (customerRepository.existsByUsernameAndIdNot(req.username(), existing.getId())) {
                throw new BussinessRuleException("Username sudah terdaftar");
            }
            if (customerRepository.existsByNoHpAndIdNot(req.noHp(), existing.getId())) {
                throw new BussinessRuleException("No HP sudah terdaftar");
            }
            customer = existing;
        } else {
            if (customerRepository.existsByEmail(req.email()))
                throw new BussinessRuleException("Email sudah terdaftar");
            if (customerRepository.existsByUsername(req.username()))
                throw new BussinessRuleException("Username sudah terdaftar");
            if (customerRepository.existsByNoHp(req.noHp()))
                throw new BussinessRuleException("No HP sudah terdaftar");

            customer = new Customer();
            customer.setId(UUID.randomUUID());
        }

        if (req.otpCode() != null && !req.otpCode().isBlank()) {
            otpService.verifyOtp(new VerifyOtpRequest(req.email(), req.otpCode(), "REGISTRATION"));
        }

        customer.setNik("PENDING");
        customer.setNama(req.username());
        customer.setEmail(req.email());
        customer.setUsername(req.username());
        customer.setPassword(passwordEncoder.encode(req.password()));
        customer.setNoHp(req.noHp());
        customer.setNamaRekening("PENDING");
        customer.setNamaBank("PENDING");
        customer.setNoRekening("PENDING");
        customer.setStatus(false);
        if (customer.getCreatedDate() == null) {
            customer.setCreatedDate(LocalDateTime.now());
        }
        customer.setUpdatedDate(LocalDateTime.now());
        customerRepository.save(customer);

        return new RegisterStepResponse(customer.getId(), 1, "Akun berhasil dibuat, lanjut ke data diri");
    }

    @Transactional
    public RegisterStepResponse registerStep2(UUID customerId, RegisterStep2Request req) {
        Customer customer = getCustomerOrThrow(customerId);

        if (req.nik() == null || !req.nik().matches("^[0-9]{16}$")) {
            throw new BussinessRuleException("NIK harus terdiri dari 16 digit angka");
        }
        if (customerRepository.existsByNikAndIdNot(req.nik(), customerId)) {
            throw new BussinessRuleException("NIK sudah terdaftar pada akun SAKU lain");
        }
        if (req.namaLengkap() == null || req.namaLengkap().trim().length() < 3) {
            throw new BussinessRuleException("Nama lengkap minimal 3 karakter");
        }
        if (req.noRekening() == null || !req.noRekening().matches("^[0-9]{8,20}$")) {
            throw new BussinessRuleException("Nomor rekening harus berupa 8-20 digit angka");
        }
        if (req.pendapatan() == null || req.pendapatan().compareTo(java.math.BigDecimal.valueOf(1000000)) < 0) {
            throw new BussinessRuleException("Penghasilan bulanan minimal Rp 1.000.000");
        }
        if (req.totalCicilanLainnya() != null && req.totalCicilanLainnya().compareTo(req.pendapatan()) > 0) {
            throw new BussinessRuleException("Total cicilan lain tidak boleh melebihi pendapatan bulanan");
        }

        customer.setNik(req.nik());
        customer.setNama(req.namaLengkap());
        customer.setNamaBank(req.namaBank());
        customer.setNoRekening(req.noRekening());
        customer.setNamaRekening(req.namaRekening());
        customer.setUpdatedDate(LocalDateTime.now());
        customerRepository.save(customer);

        scoringRepository.deleteByMstCustomerId(customer.getId());

        ScoringCustomer scoring = new ScoringCustomer();
        scoring.setId(UUID.randomUUID());
        scoring.setPenghasilanBulanan(req.pendapatan());
        scoring.setStatusPekerjaan(req.statusPekerjaan());
        scoring.setLamaBekerjaBulan(req.lamaBekerjaBulan());
        scoring.setTotalCicilanLainBulanan(req.totalCicilanLainnya());
        scoring.setMstCustomerId(customer.getId());
        scoring.setPekerjaan(req.pekerjaan());
        scoring.setTempatKerja(req.tempatKerja());
        ScoringService.ScoringResult result = scoringService.calculateScore(
                req.totalCicilanLainnya(),
                req.pendapatan(),
                req.lamaBekerjaBulan(),
                req.statusPekerjaan());
        int calculatedScore = (int) Math.round(result.score());
        scoring.setSkor(calculatedScore);
        scoring.setStatusScoring(result.decision());

        if (plafondRepository != null) {
            var activePlafonds = plafondRepository.findAllByStatusTrue();
            activePlafonds.stream()
                    .filter(p -> p.getMinSkor() != null && p.getMaxSkor() != null
                            && calculatedScore >= p.getMinSkor() && calculatedScore <= p.getMaxSkor())
                    .findFirst()
                    .ifPresent(p -> scoring.setMstPlafondId(p.getId()));
        }

        scoring.setCreatedDate(LocalDateTime.now());
        scoring.setUpdatedDate(LocalDateTime.now());
        scoringRepository.save(scoring);

        return new RegisterStepResponse(customerId, 2, "Data diri & keuangan tersimpan, lanjut ke alamat");
    }

    @Transactional
    public RegisterStepResponse registerStep3(UUID customerId, RegisterStep3Request req) {
        Customer customer = getCustomerOrThrow(customerId);

        alamatRepository.deleteByCustomer_Id(customerId);

        alamatRepository.save(toEntity(req.alamatKtp(), "KTP", customer));
        alamatRepository.save(toEntity(req.alamatDomisili(), "DOMISILI", customer));

        return new RegisterStepResponse(customerId, 3, "Alamat tersimpan, lanjut ke verifikasi identitas");
    }

    @Transactional
    public RegisterStepResponse registerStep4(UUID customerId, MultipartFile ktp, MultipartFile selfie) {
        Customer customer = getCustomerOrThrow(customerId);

        Optional<DokumenCustomer> existingKtpOpt = dokumenRepository.findByCustomer_IdAndDocType(customerId, "KTP");
        Optional<DokumenCustomer> existingSelfieOpt = dokumenRepository.findByCustomer_IdAndDocType(customerId, "SELFIE");

        boolean hasKtp = ktp != null && !ktp.isEmpty();
        boolean hasSelfie = selfie != null && !selfie.isEmpty();

        if (existingKtpOpt.isEmpty() && !hasKtp) {
            throw new BussinessRuleException("Dokumen KTP wajib diunggah");
        }
        if (existingSelfieOpt.isEmpty() && !hasSelfie) {
            throw new BussinessRuleException("Dokumen Selfie wajib diunggah");
        }
        if (!hasKtp && !hasSelfie) {
            throw new BussinessRuleException("Silakan pilih minimal satu dokumen untuk diunggah");
        }

        if (hasKtp) {
            String ktpUrl = fileStorageService.store(ktp, "ktp/" + customerId);
            DokumenCustomer docKtp = existingKtpOpt.orElseGet(() -> buildDokumen("KTP", ktpUrl, customer));
            docKtp.setFileUrl(ktpUrl);
            docKtp.setUpdatedDate(LocalDateTime.now());
            dokumenRepository.save(docKtp);
        }

        if (hasSelfie) {
            String selfieUrl = fileStorageService.store(selfie, "selfie/" + customerId);
            DokumenCustomer docSelfie = existingSelfieOpt.orElseGet(() -> buildDokumen("SELFIE", selfieUrl, customer));
            docSelfie.setFileUrl(selfieUrl);
            docSelfie.setUpdatedDate(LocalDateTime.now());
            dokumenRepository.save(docSelfie);
        }

        verifikasiRepository.deleteByMstCustomerId(customerId);

        customer.setUpdatedDate(LocalDateTime.now());
        customerRepository.save(customer);

        return new RegisterStepResponse(customerId, 4,
                "Dokumen berhasil diunggah, registrasi selesai — menunggu verifikasi Backoffice");
    }

    // Helper Methods
    private Customer getCustomerOrThrow(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));
    }

    private AlamatCustomer toEntity(com.bcafinance.backend_saku.features.customer.dto.AlamatCustomer dto,
            String jenis, Customer customer) {
        if (dto == null) return null;
        AlamatCustomer a = new AlamatCustomer();
        a.setId(UUID.randomUUID());
        a.setJenisAlamat(jenis);
        a.setAlamatLengkap(dto.alamatLengkap());
        a.setRt(dto.rt());
        a.setRw(dto.rw());
        a.setKelurahan(dto.kelurahan());
        a.setKecamatan(dto.kecamatan());
        a.setKotaKabupaten(dto.kotaKabupaten());
        a.setProvinsi(dto.provinsi());
        a.setKodePos(dto.kodePos());
        a.setCustomer(customer);
        a.setCreatedDate(LocalDateTime.now());
        a.setUpdatedDate(LocalDateTime.now());
        return a;
    }

    private DokumenCustomer buildDokumen(String type, String url, Customer customer) {
        DokumenCustomer d = new DokumenCustomer();
        d.setId(UUID.randomUUID());
        d.setDocType(type);
        d.setFileUrl(url);
        d.setCustomer(customer);
        d.setCreatedDate(LocalDateTime.now());
        d.setUpdatedDate(LocalDateTime.now());
        return d;
    }
}
