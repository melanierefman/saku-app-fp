package com.bcafinance.backend_saku.core.security;

import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) {

        Optional<AppUser> optionalUser = findCustomer(identifier);

        return optionalUser.orElseThrow(
                () -> new UsernameNotFoundException(
                        "Username atau email tidak ditemukan: " + identifier));
    }

    private Optional<AppUser> findCustomer(String identifier) {

        return customerRepository
                .findByUsernameOrEmail(identifier)
                .map(this::toAppUser);
    }


    private AppUser toAppUser(Customer customer) {

        return new AppUser(
                customer.getId(),
                customer.getEmail(),
                customer.getUsername(),
                customer.getPassword(),
                "CUSTOMER",
                "CUSTOMER",
                List.of());
    }
}
