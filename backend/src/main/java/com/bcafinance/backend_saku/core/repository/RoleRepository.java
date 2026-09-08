package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    List<Role> findAllByOrderByCreatedDateDesc();

    List<Role> findAllByStatusTrueOrderByNamaAsc();

    Optional<Role> findByNamaIgnoreCase(String nama);

    boolean existsByNamaIgnoreCase(String nama);
}

