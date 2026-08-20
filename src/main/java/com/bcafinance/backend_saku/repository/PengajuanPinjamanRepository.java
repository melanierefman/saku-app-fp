package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.PengajuanPinjaman;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PengajuanPinjamanRepository extends JpaRepository<PengajuanPinjaman, UUID> {

    List<PengajuanPinjaman> findAllByMstCustomerIdOrderByCreatedDateDesc(UUID mstCustomerId);

    List<PengajuanPinjaman> findAllByOrderByCreatedDateDesc();

    List<PengajuanPinjaman> findAllByMstBranchIdOrderByCreatedDateDesc(UUID mstBranchId);

    Optional<PengajuanPinjaman> findByIdAndMstCustomerId(UUID id, UUID mstCustomerId);

    boolean existsByNomorPengajuan(String nomorPengajuan);

}


