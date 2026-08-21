package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OtpRepository extends JpaRepository<Otp, UUID> {
}
