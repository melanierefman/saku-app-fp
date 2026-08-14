package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
}
