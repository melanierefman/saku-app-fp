package com.bcafinance.backend_saku.service;

import com.bcafinance.backend_saku.dto.register.RegisterStep1Request;
import com.bcafinance.backend_saku.dto.register.RegisterStep2Request;
import com.bcafinance.backend_saku.dto.register.RegisterStep3Request;
import com.bcafinance.backend_saku.dto.register.RegisterStepResponse;
import com.bcafinance.backend_saku.entity.AlamatCustomer;
import com.bcafinance.backend_saku.entity.Customer;
import com.bcafinance.backend_saku.entity.DokumenCustomer;
import com.bcafinance.backend_saku.entity.ScoringCustomer;
import com.bcafinance.backend_saku.exception.BussinessRuleException;
import com.bcafinance.backend_saku.repository.AlamatCustomerRepository;
import com.bcafinance.backend_saku.repository.CustomerRepository;
import com.bcafinance.backend_saku.repository.DokumenCustomerRepository;
import com.bcafinance.backend_saku.repository.ScoringCustomerRepository;
import com.bcafinance.backend_saku.repository.VerifikasiCustomerRepository;
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
    private final ScoringService scoringService;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    @Transactional
    public RegisterStepResponse registerStep1(RegisterStep1Request req) {
        if (!req.password().equals(req.confirmPassword()))
            throw new BussinessRuleException("Password dan konfirmasi password tidak sama");

        if (customerRepository.existsByEmail(req.email()))
            throw new BussinessRuleException("Email sudah terdaftar");
        if (customerRepository.existsByUsername(req.username()))
            throw new BussinessRuleException("Username sudah terdaftar");
        if (customerRepository.existsByNoHp(req.noHp()))
            throw new BussinessRuleException("No HP sudah terdaftar");

        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
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
        customer.setCreatedDate(LocalDateTime.now());
        customer.setUpdatedDate(LocalDateTime.now());
        customerRepository.save(customer);

        return new RegisterStepResponse(customer.getId(), 1, "Akun berhasil dibuat, lanjut ke data diri");
    }

    @Transactional
    public RegisterStepResponse registerStep2(UUID customerId, RegisterStep2Request req) {
        Customer customer = getCustomerOrThrow(customerId);

        if (customerRepository.existsByNikAndIdNot(req.nik(), customerId))
            throw new BussinessRuleException("NIK sudah terdaftar");

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
        scoring.setLamaJadiNasabahBulan(req.lamaJadiNasabahBulan());
        scoring.setTotalCicilanLainBulanan(req.totalCicilanLainnya());
        scoring.setMstCustomerId(customer.getId());
        scoring.setPekerjaan(req.pekerjaan());
        scoring.setTempatKerja(req.tempatKerja());
        ScoringService.ScoringResult result = scoringService.calculateScore(
                req.totalCicilanLainnya(),
                req.pendapatan(),
                req.lamaBekerjaBulan(),
                req.statusPekerjaan(),
                req.lamaJadiNasabahBulan());
        scoring.setSkor((int) Math.round(result.score()));
        scoring.setStatusScoring(result.decision());
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
        Optional<DokumenCustomer> existingSelfieOpt = dokumenRepository.findByCustomer_IdAndDocType(customerId,
                "SELFIE");

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

        // Hapus catatan verifikasi lama (jika status sebelumnya PERLU_REVISI) sehingga
        // status otomatis kembali PENDING
        verifikasiRepository.deleteByMstCustomerId(customerId);

        customer.setUpdatedDate(LocalDateTime.now());
        customerRepository.save(customer);

        return new RegisterStepResponse(customerId, 4,
                "Dokumen berhasil diunggah, registrasi selesai — menunggu verifikasi Backoffice");
    }

    private Customer getCustomerOrThrow(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));
    }

    private AlamatCustomer toEntity(com.bcafinance.backend_saku.dto.register.AlamatCustomer dto,
            String jenis, Customer customer) {
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
