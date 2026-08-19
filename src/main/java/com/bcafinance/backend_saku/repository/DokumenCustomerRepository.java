package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.DokumenCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DokumenCustomerRepository extends JpaRepository<DokumenCustomer, UUID> {

    void deleteByCustomer_Id(UUID customerId);
}
