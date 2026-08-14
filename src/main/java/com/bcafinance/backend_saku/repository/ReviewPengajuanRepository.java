package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.ReviewPengajuan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewPengajuanRepository extends JpaRepository<ReviewPengajuan, UUID> {
}
