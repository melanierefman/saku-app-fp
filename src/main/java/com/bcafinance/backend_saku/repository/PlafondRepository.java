package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.Plafond;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlafondRepository extends JpaRepository<Plafond, UUID> {

    Optional<Plafond> findFirstByStatusTrueOrderByMinSkorAsc();

    Optional<Plafond> findTopByMinPendapatanLessThanEqualAndStatusTrueOrderByMinPendapatanDesc(
            java.math.BigDecimal pendapatan);
}
