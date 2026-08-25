package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.Angsuran;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AngsuranRepository extends JpaRepository<Angsuran, UUID> {

    List<Angsuran> findAllByTrxPengajuanPinjamanIdOrderByCicilanKeAsc(UUID trxPengajuanPinjamanId);

    void deleteByTrxPengajuanPinjamanId(UUID trxPengajuanPinjamanId);
}

