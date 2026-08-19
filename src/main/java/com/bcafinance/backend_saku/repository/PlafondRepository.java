package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.Plafond;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlafondRepository extends JpaRepository<Plafond, UUID> {

    List<Plafond> findAllByStatusTrue();

    Optional<Plafond> findFirstByStatusTrueOrderByMinSkorAsc();

    Optional<Plafond> findTopByMinPendapatanLessThanEqualAndStatusTrueOrderByMinPendapatanDesc(
            java.math.BigDecimal pendapatan);
}

