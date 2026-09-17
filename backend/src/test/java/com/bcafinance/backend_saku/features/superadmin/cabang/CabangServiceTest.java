package com.bcafinance.backend_saku.features.superadmin.cabang;

import com.bcafinance.backend_saku.core.entity.Cabang;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.CabangRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CabangServiceTest {

    @Mock
    private CabangRepository cabangRepository;

    @InjectMocks
    private CabangService cabangService;

    @Test
    @DisplayName("Create Cabang: Berhasil menyimpan data cabang baru")
    void testCreateCabangSuccess() {
        CabangRequest request = new CabangRequest();
        request.setNama("PT SAKU Kelapa Gading");
        request.setKota("Jakarta Utara");
        request.setIsDefault(false);
        request.setStatus(true);

        when(cabangRepository.save(any(Cabang.class))).thenAnswer(i -> {
            Cabang c = i.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        CabangResponse response = cabangService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getNama()).isEqualTo("PT SAKU Kelapa Gading");
        assertThat(response.getKota()).isEqualTo("Jakarta Utara");
        verify(cabangRepository).save(any(Cabang.class));
    }

    @Test
    @DisplayName("Find All: Mengembalikan daftar seluruh cabang")
    void testFindAllCabang() {
        Cabang c = new Cabang();
        c.setId(UUID.randomUUID());
        c.setNama("PT SAKU BSD");
        c.setKota("Tangerang Selatan");
        c.setStatus(true);

        when(cabangRepository.findAll()).thenReturn(List.of(c));

        List<CabangResponse> list = cabangService.findAll();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getNama()).isEqualTo("PT SAKU BSD");
    }

    @Test
    @DisplayName("Find By Id: Melempar exception jika ID cabang tidak ditemukan")
    void testFindByIdNotFound() {
        UUID randomId = UUID.randomUUID();
        when(cabangRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cabangService.findById(randomId))
                .isInstanceOf(BussinessRuleException.class)
                .hasMessageContaining("tidak ditemukan");
    }
}
