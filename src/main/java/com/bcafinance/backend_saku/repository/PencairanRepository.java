package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.Pencairan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PencairanRepository extends JpaRepository<Pencairan, UUID> {
}
