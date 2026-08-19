package com.bcafinance.backend_saku.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Setter
@Getter
@AllArgsConstructor
public class AppUser implements UserDetails {

    private UUID idKaryawan;
    private String email;
    private String username;
    private String password;
    private String role;
    private String tipe;
    private List<String> permissions;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String normalizedRole = role == null
                ? ""
                : role.toUpperCase().replaceAll("[^A-Z0-9]", "");

        return Stream.concat(
                Stream.of(new SimpleGrantedAuthority("ROLE_" + normalizedRole)),
                permissions == null
                        ? Stream.empty()
                        : permissions.stream().map(SimpleGrantedAuthority::new))
                .toList();
    }
}