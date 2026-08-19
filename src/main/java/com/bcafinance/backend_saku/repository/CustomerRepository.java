package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByNoHp(String noHp);

    boolean existsByNikAndIdNot(String nik, UUID id);

    List<Customer> findAllByStatusFalseOrderByCreatedDateAsc();

    // Email or Username
    @Query("""
                SELECT c
                FROM Customer c
                WHERE (c.username = :identifier OR c.email = :identifier)
            """)
    Optional<Customer> findByUsernameOrEmail(@Param("identifier") String identifier);
}
