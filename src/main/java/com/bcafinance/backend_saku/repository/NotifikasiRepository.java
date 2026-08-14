package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.Notifikasi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotifikasiRepository extends JpaRepository<Notifikasi, UUID> {
}
