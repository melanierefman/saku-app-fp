package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.Customer;
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

    boolean existsByNoHpAndIdNot(String noHp, UUID id);

    boolean existsByEmailAndIdNot(String email, UUID id);

    boolean existsByUsernameAndIdNot(String username, UUID id);

    boolean existsByNikAndIdNot(String nik, UUID id);

    List<Customer> findAllByStatusFalseOrderByCreatedDateAsc();

    List<Customer> findAllByOrderByCreatedDateDesc();

    List<Customer> findAllByStatusFalseOrderByCreatedDateDesc();


    Optional<Customer> findByEmail(String email);

    // Email or Username
    @Query("""
                SELECT c
                FROM Customer c
                WHERE (c.username = :identifier OR c.email = :identifier)
            """)
    Optional<Customer> findByUsernameOrEmail(@Param("identifier") String identifier);
}

