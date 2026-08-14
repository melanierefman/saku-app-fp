package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.Cabang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CabangRepository extends JpaRepository<Cabang, UUID> {
}
