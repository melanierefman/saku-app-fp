package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.Karyawan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface KaryawanRepository extends JpaRepository<Karyawan, UUID> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    // Email or Username
    @Query("""
                SELECT k
                FROM Karyawan k
                JOIN FETCH k.role
                WHERE (k.username = :identifier OR k.email = :identifier)
            """)
    Optional<Karyawan> findByUsernameOrEmail(@Param("identifier") String identifier);
}
