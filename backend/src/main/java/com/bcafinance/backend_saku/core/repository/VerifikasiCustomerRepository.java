package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.VerifikasiCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VerifikasiCustomerRepository extends JpaRepository<VerifikasiCustomer, UUID> {

    Optional<VerifikasiCustomer> findFirstByMstCustomerIdOrderByCreatedDateDesc(UUID mstCustomerId);

    List<VerifikasiCustomer> findAllByMstCustomerIdOrderByCreatedDateDesc(UUID mstCustomerId);

    void deleteByMstCustomerId(UUID customerId);
}


