package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.Cabang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CabangRepository extends JpaRepository<Cabang, UUID> {

    List<Cabang> findAllByStatusTrueOrderByNamaAsc();

    Optional<Cabang> findFirstByIsDefaultTrueAndStatusTrue();

    Optional<Cabang> findFirstByStatusTrue();

    List<Cabang> findAllByKotaIgnoreCaseAndStatusTrue(String kota);

    List<Cabang> findAllByKotaContainingIgnoreCaseAndStatusTrue(String kota);
}


