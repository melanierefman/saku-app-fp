package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.DokumenCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DokumenCustomerRepository extends JpaRepository<DokumenCustomer, UUID> {

    void deleteByCustomer_Id(UUID customerId);

    List<DokumenCustomer> findAllByCustomer_Id(UUID customerId);

    Optional<DokumenCustomer> findByCustomer_IdAndDocType(UUID customerId, String docType);
}

