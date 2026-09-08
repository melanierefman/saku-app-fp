package com.bcafinance.backend_saku.features.auth.service;

import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.entity.Karyawan;
import com.bcafinance.backend_saku.core.entity.Otp;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.notification.EmailService;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.repository.KaryawanRepository;
import com.bcafinance.backend_saku.core.repository.OtpRepository;
import com.bcafinance.backend_saku.features.auth.dto.SendOtpRequest;
import com.bcafinance.backend_saku.features.auth.dto.SendOtpResponse;
import com.bcafinance.backend_saku.features.auth.dto.VerifyOtpRequest;
import com.bcafinance.backend_saku.features.auth.dto.VerifyOtpResponse;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpService {

    @Value("${app.otp.expiry-minutes:5}")
    private int otpExpiryMinutes;

    private static final SecureRandom RANDOM = new SecureRandom();

    private final OtpRepository otpRepository;
    private final CustomerRepository customerRepository;
    private final KaryawanRepository karyawanRepository;
    private final EmailService emailService;

    @Transactional
    public SendOtpResponse sendOtp(SendOtpRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String purpose = request.getPurpose().trim().toUpperCase();

        UUID targetId = resolveUserId(email, purpose);

        // 1. Invalidate OTP lama yang belum terpakai untuk user & purpose yang sama
        List<Otp> activeOtps = otpRepository.findAllByMstCustomerIdAndPurposeAndIsUsedFalse(targetId, purpose);
        for (Otp oldOtp : activeOtps) {
            oldOtp.setIsUsed(true);
            oldOtp.setUpdatedDate(LocalDateTime.now());
            otpRepository.save(oldOtp);
        }

        // 2. Generate 6-digit random OTP
        int number = RANDOM.nextInt(1_000_000);
        String otpCode = String.format("%06d", number);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = now.plusMinutes(otpExpiryMinutes);

        // 3. Simpan ke database trx_otp
        Otp otp = new Otp();
        otp.setId(UUID.randomUUID());
        otp.setOtpCode(otpCode);
        otp.setPurpose(purpose);
        otp.setIsUsed(false);
        otp.setExpiredAt(expiredAt);
        otp.setCreatedDate(now);
        otp.setUpdatedDate(now);
        otp.setMstCustomerId(targetId);

        otpRepository.save(otp);

        // 4. Kirim email OTP
        emailService.sendOtpEmail(email, otpCode, purpose, otpExpiryMinutes);

        return SendOtpResponse.builder()
                .email(maskEmail(email))
                .purpose(purpose)
                .expiresInMinutes(otpExpiryMinutes)
                .expiredAt(expiredAt)
                .message("Kode OTP berhasil dikirim ke email " + maskEmail(email))
                .build();
    }

    @Transactional
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String otpCode = request.getOtpCode().trim();
        String purpose = request.getPurpose().trim().toUpperCase();

        UUID targetId = resolveUserId(email, purpose);

        // 1. Cari OTP aktif sesuai targetId, otpCode, dan purpose
        Otp otp = otpRepository
                .findFirstByMstCustomerIdAndOtpCodeAndPurposeAndIsUsedFalseOrderByCreatedDateDesc(
                        targetId, otpCode, purpose)
                .orElseThrow(() -> new BussinessRuleException("Kode OTP tidak valid atau sudah pernah digunakan"));

        // 2. Validasi Expiry
        if (LocalDateTime.now().isAfter(otp.getExpiredAt())) {
            otp.setIsUsed(true);
            otp.setUpdatedDate(LocalDateTime.now());
            otpRepository.save(otp);
            throw new BussinessRuleException("Kode OTP telah kedaluwarsa. Silakan minta kode OTP baru.");
        }

        // 3. Tandai OTP sebagai sudah digunakan
        otp.setIsUsed(true);
        otp.setUpdatedDate(LocalDateTime.now());
        otpRepository.save(otp);

        return VerifyOtpResponse.builder()
                .valid(true)
                .message("Verifikasi OTP berhasil.")
                .customerId(targetId)
                .email(email)
                .purpose(purpose)
                .verifiedAt(LocalDateTime.now())
                .build();
    }

    private UUID resolveUserId(String email, String purpose) {
        if ("REGISTRATION".equalsIgnoreCase(purpose)) {
            Optional<Customer> existingOpt = customerRepository.findByEmail(email);
            if (existingOpt.isPresent()) {
                Customer cust = existingOpt.get();
                if (!"PENDING".equals(cust.getPassword())) {
                    throw new BussinessRuleException("Email sudah terdaftar. Silakan gunakan email lain atau login.");
                }
                return cust.getId();
            }

            // Simpan placeholder customer untuk memenuhi foreign key trx_otp ->
            // mst_customer
            Customer placeholder = new Customer();
            placeholder.setId(UUID.randomUUID());
            placeholder.setEmail(email);
            placeholder.setUsername(email);
            placeholder.setNama("PENDING");
            placeholder.setNik("PENDING");
            placeholder.setNoHp("PENDING");
            placeholder.setPassword("PENDING");
            placeholder.setNamaRekening("PENDING");
            placeholder.setNamaBank("PENDING");
            placeholder.setNoRekening("PENDING");
            placeholder.setStatus(false);
            placeholder.setCreatedDate(LocalDateTime.now());
            placeholder.setUpdatedDate(LocalDateTime.now());
            placeholder = customerRepository.save(placeholder);
            return placeholder.getId();
        }

        Optional<Customer> custOpt = customerRepository.findByEmail(email);
        if (custOpt.isPresent()) {
            return custOpt.get().getId();
        }

        Optional<Karyawan> karyOpt = karyawanRepository.findByEmail(email);
        if (karyOpt.isPresent()) {
            // Jika karyawan meminta OTP reset password, pastikan ada entri di mst_customer
            // untuk FK
            Customer placeholder = new Customer();
            placeholder.setId(UUID.randomUUID());
            placeholder.setEmail(email);
            placeholder.setUsername("KARYAWAN_" + karyOpt.get().getUsername());
            placeholder.setNama(karyOpt.get().getNama());
            placeholder.setNik("PENDING");
            placeholder.setNoHp("PENDING");
            placeholder.setPassword("PENDING");
            placeholder.setNamaRekening("PENDING");
            placeholder.setNamaBank("PENDING");
            placeholder.setNoRekening("PENDING");
            placeholder.setStatus(false);
            placeholder.setCreatedDate(LocalDateTime.now());
            placeholder.setUpdatedDate(LocalDateTime.now());
            placeholder = customerRepository.save(placeholder);
            return placeholder.getId();
        }

        throw new BussinessRuleException("Email tidak ditemukan dalam sistem SAKU");
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@"))
            return email;
        int atIndex = email.indexOf("@");
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (username.length() <= 2) {
            return username.charAt(0) + "***" + domain;
        }
        return username.charAt(0) + "***" + username.charAt(username.length() - 1) + domain;
    }
}
