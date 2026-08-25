package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.Notifikasi;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotifikasiRepository extends JpaRepository<Notifikasi, UUID> {

    List<Notifikasi> findAllByMstCustomerIdOrderByCreatedDateDesc(UUID mstCustomerId);

    List<Notifikasi> findAllByMstCustomerIdAndStatusOrderByCreatedDateDesc(UUID mstCustomerId, String status);

    Optional<Notifikasi> findByIdAndMstCustomerId(UUID id, UUID mstCustomerId);

    long countByMstCustomerIdAndStatus(UUID mstCustomerId, String status);

    List<Notifikasi> findAllByMstCustomerIdAndStatus(UUID mstCustomerId, String status);
}

