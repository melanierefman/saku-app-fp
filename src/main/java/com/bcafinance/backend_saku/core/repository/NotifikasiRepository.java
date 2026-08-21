package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.Notifikasi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotifikasiRepository extends JpaRepository<Notifikasi, UUID> {
}
