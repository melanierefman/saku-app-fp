package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.Menu;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, UUID> {

    List<Menu> findAllByOrderByCreatedDateDesc();

    List<Menu> findAllByStatusTrueOrderByNamaAsc();

    Optional<Menu> findByPathIgnoreCase(String path);

    boolean existsByNamaIgnoreCase(String nama);

    boolean existsByPathIgnoreCase(String path);
}

