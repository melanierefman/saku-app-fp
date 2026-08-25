package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.Pencairan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PencairanRepository extends JpaRepository<Pencairan, UUID> {

    Optional<Pencairan> findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(UUID trxPengajuanPinjamanId);

    Optional<Pencairan> findByTrxPengajuanPinjamanId(UUID trxPengajuanPinjamanId);

    boolean existsByTrxPengajuanPinjamanIdAndStatusPencairan(UUID trxPengajuanPinjamanId, String statusPencairan);
}

