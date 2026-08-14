package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OtpRepository extends JpaRepository<Otp, UUID> {
}
