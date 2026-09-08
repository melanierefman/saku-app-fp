package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<Otp, UUID> {

    Optional<Otp> findFirstByMstCustomerIdAndOtpCodeAndPurposeAndIsUsedFalseOrderByCreatedDateDesc(
            UUID mstCustomerId, String otpCode, String purpose);

    Optional<Otp> findFirstByMstCustomerIdAndPurposeAndIsUsedFalseOrderByCreatedDateDesc(
            UUID mstCustomerId, String purpose);

    List<Otp> findAllByMstCustomerIdAndPurposeAndIsUsedFalse(
            UUID mstCustomerId, String purpose);
}

