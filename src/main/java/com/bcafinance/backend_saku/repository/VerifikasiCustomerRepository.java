package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.VerifikasiCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerifikasiCustomerRepository extends JpaRepository<VerifikasiCustomer, UUID> {
}
