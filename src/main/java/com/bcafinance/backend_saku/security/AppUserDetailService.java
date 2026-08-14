package com.bcafinance.backend_saku.security;

import com.bcafinance.backend_saku.entity.Karyawan;
import com.bcafinance.backend_saku.repository.KaryawanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserDetailService implements UserDetailsService {

    private final KaryawanRepository karyawanRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) {

        Optional<AppUser> optionalUser = findKaryawan(identifier);

        return optionalUser.orElseThrow(
                () -> new UsernameNotFoundException(
                        "Username atau email tidak ditemukan: " + identifier
                )
        );
    }

    private Optional<AppUser> findKaryawan(String identifier) {

        return karyawanRepository
                .findByUsernameOrEmail(identifier)
                .map(this::toAppUser);
    }

    private AppUser toAppUser(Karyawan karyawan) {

        return new AppUser(
                karyawan.getId(),
                karyawan.getEmail(),
                karyawan.getUsername(),
                karyawan.getPassword(),
                karyawan.getRole().getNama()
        );
    }
}