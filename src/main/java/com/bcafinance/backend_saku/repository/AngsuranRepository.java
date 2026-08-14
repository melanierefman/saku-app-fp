package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.Angsuran;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AngsuranRepository extends JpaRepository<Angsuran, UUID> {
}
