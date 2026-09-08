package com.bcafinance.backend_saku.core.security;

import com.bcafinance.backend_saku.core.entity.Karyawan;
import com.bcafinance.backend_saku.core.repository.KaryawanRepository;
import com.bcafinance.backend_saku.core.repository.PermissionRepository;
import com.bcafinance.backend_saku.core.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppUserDetailService implements UserDetailsService {

        private final KaryawanRepository karyawanRepository;
        private final RolePermissionRepository rolePermissionRepository;
        private final PermissionRepository permissionRepository;

        @Override
        public UserDetails loadUserByUsername(String identifier) {

                Optional<AppUser> optionalUser = findKaryawan(identifier);

                return optionalUser.orElseThrow(
                                () -> new UsernameNotFoundException(
                                                "Username atau email tidak ditemukan: " + identifier));
        }

        private Optional<AppUser> findKaryawan(String identifier) {

                return karyawanRepository
                                .findByUsernameOrEmail(identifier)
                                .map(this::toAppUser);
        }

        private AppUser toAppUser(Karyawan karyawan) {
                List<String> permissions = rolePermissionRepository.findAllByMstRoleId(karyawan.getRole().getId())
                                .stream()
                                .map(rolePermission -> permissionRepository
                                                .findById(rolePermission.getMstPermissionId()))
                                .flatMap(Optional::stream)
                                .flatMap(permission -> java.util.stream.Stream.of(
                                                "PERM_" + permission.getResource() + "_" + permission.getAction(),
                                                "PERM_" + permission.getNama()))
                                .map(value -> value.toUpperCase().replaceAll("[^A-Z0-9_]", "_"))
                                .distinct()
                                .collect(Collectors.toList());

                return new AppUser(
                                karyawan.getId(),
                                karyawan.getEmail(),
                                karyawan.getUsername(),
                                karyawan.getPassword(),
                                karyawan.getRole().getNama(),
                                "KARYAWAN",
                                permissions);
        }
}