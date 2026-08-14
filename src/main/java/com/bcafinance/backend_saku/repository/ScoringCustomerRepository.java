package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.ScoringCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScoringCustomerRepository extends JpaRepository<ScoringCustomer, UUID> {
}
