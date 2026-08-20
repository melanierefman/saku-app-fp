package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.Persetujuan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersetujuanRepository extends JpaRepository<Persetujuan, UUID> {

    Optional<Persetujuan> findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(UUID trxPengajuanPinjamanId);

    List<Persetujuan> findAllByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(UUID trxPengajuanPinjamanId);

    void deleteByTrxPengajuanPinjamanId(UUID trxPengajuanPinjamanId);
}

