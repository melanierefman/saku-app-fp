package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.ReviewPengajuan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewPengajuanRepository extends JpaRepository<ReviewPengajuan, UUID> {

    List<ReviewPengajuan> findAllByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(UUID trxPengajuanPinjamanId);

    Optional<ReviewPengajuan> findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(UUID trxPengajuanPinjamanId);
}

