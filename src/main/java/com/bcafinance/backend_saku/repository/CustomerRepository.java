package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}
