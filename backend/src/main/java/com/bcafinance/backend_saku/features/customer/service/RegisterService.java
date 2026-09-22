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
import com.bcafinance.backend_saku.core.realtime.RealTimeEmitterService;
import com.bcafinance.backend_saku.core.realtime.RealTimeEventDto;
import com.bcafinance.backend_saku.core.storage.FileStorageService;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep1KtpRequest;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep2PersonalRequest;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep5CompleteRequest;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStepResponse;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
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
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final RealTimeEmitterService realTimeEmitterService;

    // Validasi ketersediaan NIK customer
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

    // Validasi ketersediaan nomor telepon customer
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

    // Step 1 KYC: Simpan data e-KTP hasil scan OCR dan upload foto dokumen
    @Transactional
    public RegisterStepResponse registerStep1Ktp(UUID customerId, MultipartFile ktpFile, RegisterStep1KtpRequest req) {
        Customer customer = getCustomerOrThrow(customerId);

        if (req != null) {
            if (req.nik() != null && !req.nik().isBlank() && customerRepository.existsByNikAndIdNot(req.nik(), customerId)) {
                throw new BussinessRuleException("NIK sudah terdaftar dalam sistem");
            }

            if (req.nik() != null && !req.nik().isBlank()) customer.setNik(req.nik());
            if (req.namaLengkap() != null && !req.namaLengkap().isBlank()) customer.setNama(req.namaLengkap());
            customer.setUpdatedDate(LocalDateTime.now());
            customerRepository.save(customer);

            if (req.alamatKtp() != null) {
                alamatRepository.deleteByCustomer_IdAndJenisAlamat(customerId, "KTP");
                alamatRepository.save(toEntity(req.alamatKtp(), "KTP", customer));
            }
        }

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

    // Step 2 KYC: Simpan data pribadi, pekerjaan, finansial, dan rekening bank
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

    // Step 3 KYC: Simpan dan verifikasi foto selfie wajah nasabah
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

    // Step 4 KYC: Konfirmasi persetujuan syarat dan ketentuan SAKU
    @Transactional
    public RegisterStepResponse registerStep4Tnc(UUID customerId) {
        getCustomerOrThrow(customerId);
        return new RegisterStepResponse(customerId, 4, "Syarat & Ketentuan disetujui, lanjut ke pembuatan password");
    }

    // Step 5 KYC: Pembuatan password akun dan finalisasi pendaftaran nasabah
    @Transactional
    public RegisterStepResponse registerStep5Complete(UUID customerId, RegisterStep5CompleteRequest req) {
        Customer customer = getCustomerOrThrow(customerId);

        if (!req.password().equals(req.confirmPassword())) {
            throw new BussinessRuleException("Password dan konfirmasi password tidak sama");
        }

        if (req.password().length() < 8) {
            throw new BussinessRuleException("Password minimal 8 karakter");
        }

        customer.setUsername(customer.getEmail());
        customer.setPassword(passwordEncoder.encode(req.password()));
        customer.setStatus(false);
        customer.setUpdatedDate(LocalDateTime.now());
        customerRepository.save(customer);

        verifikasiRepository.deleteByMstCustomerId(customerId);

        if (realTimeEmitterService != null) {
            realTimeEmitterService.broadcast(RealTimeEventDto.builder()
                    .eventType("KYC_SUBMITTED")
                    .referenceId(customer.getId().toString())
                    .customerName(customer.getNama())
                    .title("Pendaftaran Nasabah Baru")
                    .message("Nasabah baru " + customer.getNama() + " telah menyelesaikan registrasi dan menunggu verifikasi KYC.")
                    .targetRoles(List.of("ROLE_BACKOFFICE", "ROLE_SUPERADMIN"))
                    .timestamp(LocalDateTime.now())
                    .build());
        }

        return new RegisterStepResponse(customerId, 5,
                "Pendaftaran berhasil! Akun Anda sedang dalam proses verifikasi Backoffice");
    }

    // Ambil entitas customer berdasarkan ID atau lempar exception
    private Customer getCustomerOrThrow(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));
    }

    // Konversi DTO alamat customer ke entitas AlamatCustomer
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

    // Buat entitas DokumenCustomer baru
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
