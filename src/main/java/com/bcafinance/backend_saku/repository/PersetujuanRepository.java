package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.Persetujuan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PersetujuanRepository extends JpaRepository<Persetujuan, UUID> {
}
