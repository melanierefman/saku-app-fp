package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.PengajuanPinjaman;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PengajuanPinjamanRepository extends JpaRepository<PengajuanPinjaman, UUID> {
}
