package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.AlamatCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlamatCustomerRepository extends JpaRepository<AlamatCustomer, UUID> {

    void deleteByCustomer_Id(UUID customerId);
}
