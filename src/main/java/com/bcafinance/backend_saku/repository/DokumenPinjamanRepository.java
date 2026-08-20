package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.DokumenPinjaman;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DokumenPinjamanRepository extends JpaRepository<DokumenPinjaman, UUID> {

    List<DokumenPinjaman> findAllByTrxPengajuanPinjamanId(UUID trxPengajuanPinjamanId);

    void deleteByTrxPengajuanPinjamanId(UUID trxPengajuanPinjamanId);

    Optional<DokumenPinjaman> findByTrxPengajuanPinjamanIdAndDocType(UUID trxPengajuanPinjamanId, String docType);
}

