package com.bcafinance.backend_saku.features.superadmin.karyawan;

import com.bcafinance.backend_saku.core.entity.Cabang;
import com.bcafinance.backend_saku.core.entity.Karyawan;
import com.bcafinance.backend_saku.core.entity.Role;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.CabangRepository;
import com.bcafinance.backend_saku.core.repository.KaryawanRepository;
import com.bcafinance.backend_saku.core.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KaryawanServiceTest {

    @Mock
    private KaryawanRepository karyawanRepository;

    @Mock
    private CabangRepository cabangRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private KaryawanService karyawanService;

    @Test
    @DisplayName("Create Karyawan: Berhasil membuat akun karyawan baru dengan password terenkripsi")
    void testCreateKaryawanSuccess() {
        UUID roleId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        Role role = new Role();
        role.setId(roleId);
        role.setNama("MARKETING");
        role.setStatus(true);

        Cabang cabang = new Cabang();
        cabang.setId(branchId);
        cabang.setNama("PT SAKU BSD");
        cabang.setStatus(true);

        KaryawanRequest request = new KaryawanRequest();
        request.setNama("Budi Staff");
        request.setUsername("budistaff");
        request.setEmail("budi.staff@bcafinance.co.id");
        request.setPassword("password123");
        request.setMstRoleId(roleId);
        request.setMstBranchId(branchId);
        request.setStatus(true);

        when(karyawanRepository.existsByEmail("budi.staff@bcafinance.co.id")).thenReturn(false);
        when(karyawanRepository.existsByUsername("budistaff")).thenReturn(false);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(cabangRepository.findById(branchId)).thenReturn(Optional.of(cabang));
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(karyawanRepository.save(any(Karyawan.class))).thenAnswer(i -> {
            Karyawan k = i.getArgument(0);
            k.setId(UUID.randomUUID());
            return k;
        });

        KaryawanResponse response = karyawanService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("budistaff");
        assertThat(response.getRoleNama()).isEqualTo("MARKETING");
        verify(karyawanRepository).save(any(Karyawan.class));
    }

    @Test
    @DisplayName("Create Karyawan: Gagal jika email sudah digunakan")
    void testCreateKaryawanDuplicateEmail() {
        KaryawanRequest request = new KaryawanRequest();
        request.setEmail("duplicate@bcafinance.co.id");
        request.setUsername("unique_username");

        when(karyawanRepository.existsByEmail("duplicate@bcafinance.co.id")).thenReturn(true);

        assertThatThrownBy(() -> karyawanService.create(request))
                .isInstanceOf(BussinessRuleException.class)
                .hasMessageContaining("Email karyawan sudah terdaftar");
    }
}
