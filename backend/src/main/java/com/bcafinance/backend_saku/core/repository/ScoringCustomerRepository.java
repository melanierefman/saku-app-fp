package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.ScoringCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;

public interface ScoringCustomerRepository extends JpaRepository<ScoringCustomer, UUID> {

    Optional<ScoringCustomer> findFirstByMstCustomerIdOrderByCreatedDateDesc(UUID customerId);

    Optional<ScoringCustomer> findByMstCustomerId(UUID customerId);

    void deleteByMstCustomerId(UUID customerId);
}

