package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.Karyawan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface KaryawanRepository extends JpaRepository<Karyawan, UUID> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<Karyawan> findByEmail(String email);

    // Email or Username
    @Query("""
                SELECT k
                FROM Karyawan k
                JOIN FETCH k.role
                WHERE (k.username = :identifier OR k.email = :identifier)
            """)
    Optional<Karyawan> findByUsernameOrEmail(@Param("identifier") String identifier);

    @Query(value = """
                SELECT k
                FROM Karyawan k
                LEFT JOIN FETCH k.role r
                LEFT JOIN FETCH k.cabang c
                WHERE (:search IS NULL OR :search = ''
                       OR LOWER(k.nama) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(k.email) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(k.username) LIKE LOWER(CONCAT('%', :search, '%')))
                  AND (:roleId IS NULL OR k.role.id = :roleId)
                  AND (:branchId IS NULL OR k.cabang.id = :branchId)
                  AND (:status IS NULL OR k.status = :status)
            """,
            countQuery = """
                SELECT COUNT(k)
                FROM Karyawan k
                WHERE (:search IS NULL OR :search = ''
                       OR LOWER(k.nama) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(k.email) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(k.username) LIKE LOWER(CONCAT('%', :search, '%')))
                  AND (:roleId IS NULL OR k.role.id = :roleId)
                  AND (:branchId IS NULL OR k.cabang.id = :branchId)
                  AND (:status IS NULL OR k.status = :status)
            """)
    Page<Karyawan> findByFilters(
            @Param("search") String search,
            @Param("roleId") UUID roleId,
            @Param("branchId") UUID branchId,
            @Param("status") Boolean status,
            Pageable pageable);
}


