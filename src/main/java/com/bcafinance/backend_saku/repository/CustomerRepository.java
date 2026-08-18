package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.Customer;
import com.bcafinance.backend_saku.entity.Karyawan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    //Email or Username
    @Query("""
        SELECT c
        FROM Customer c
        WHERE (c.username = :identifier OR c.email = :identifier)
    """)
    Optional<Customer> findByUsernameOrEmail(@Param("identifier") String identifier);
}
