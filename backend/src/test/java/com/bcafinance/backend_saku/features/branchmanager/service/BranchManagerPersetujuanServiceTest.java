package com.bcafinance.backend_saku.features.branchmanager.service;

import com.bcafinance.backend_saku.core.entity.Cabang;
import com.bcafinance.backend_saku.core.entity.Karyawan;
import com.bcafinance.backend_saku.core.entity.PengajuanPinjaman;
import com.bcafinance.backend_saku.core.entity.Persetujuan;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.AlamatCustomerRepository;
import com.bcafinance.backend_saku.core.repository.CabangRepository;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.repository.DokumenCustomerRepository;
import com.bcafinance.backend_saku.core.repository.DokumenPinjamanRepository;
import com.bcafinance.backend_saku.core.repository.KaryawanRepository;
import com.bcafinance.backend_saku.core.repository.PengajuanPinjamanRepository;
import com.bcafinance.backend_saku.core.repository.PersetujuanRepository;
import com.bcafinance.backend_saku.core.repository.ReviewPengajuanRepository;
import com.bcafinance.backend_saku.core.repository.ScoringCustomerRepository;
import com.bcafinance.backend_saku.features.branchmanager.dto.PersetujuanPinjamanRequest;
import com.bcafinance.backend_saku.features.branchmanager.dto.PersetujuanPinjamanResponse;
import com.bcafinance.backend_saku.features.customer.service.NotifikasiService;
import com.bcafinance.backend_saku.features.scoring.service.ScoringService;
import com.bcafinance.backend_saku.features.superadmin.auditlog.service.AuditLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchManagerPersetujuanServiceTest {

    @Mock
    private PengajuanPinjamanRepository pengajuanRepository;

    @Mock
    private PersetujuanRepository persetujuanRepository;

    @Mock
    private ReviewPengajuanRepository reviewPengajuanRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AlamatCustomerRepository alamatRepository;

    @Mock
    private DokumenCustomerRepository dokumenCustomerRepository;

    @Mock
    private DokumenPinjamanRepository dokumenPinjamanRepository;

    @Mock
    private ScoringCustomerRepository scoringRepository;

    @Mock
    private KaryawanRepository karyawanRepository;

    @Mock
    private CabangRepository cabangRepository;

    @Mock
    private ScoringService scoringService;

    @Mock
    private NotifikasiService notifikasiService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private BranchManagerPersetujuanService branchManagerPersetujuanService;

    @Test
    @DisplayName("Persetujuan BM: Branch Manager menyetujui pinjaman (Status -> PENGAJUAN_DISETUJUI)")
    void testBmApproveSuccess() {
        UUID pengajuanId = UUID.randomUUID();
        UUID karyawanId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        Cabang cabang = new Cabang();
        cabang.setId(branchId);

        Karyawan bm = new Karyawan();
        bm.setId(karyawanId);
        bm.setNama("Branch Manager 1");
        bm.setCabang(cabang);

        PengajuanPinjaman pengajuan = new PengajuanPinjaman();
        pengajuan.setId(pengajuanId);
        pengajuan.setNomorPengajuan("PJ-2026-BM-01");
        pengajuan.setMstBranchId(branchId);
        pengajuan.setStatusPengajuan("SELESAI_DIREVIEW");

        when(pengajuanRepository.findById(pengajuanId)).thenReturn(Optional.of(pengajuan));
        when(karyawanRepository.findById(karyawanId)).thenReturn(Optional.of(bm));
        when(persetujuanRepository.save(any(Persetujuan.class))).thenAnswer(i -> i.getArgument(0));

        PersetujuanPinjamanRequest request = new PersetujuanPinjamanRequest();
        request.setHasilPersetujuan("DISETUJUI");
        request.setCatatan("Plafond dan dokumen disetujui untuk pencairan");

        PersetujuanPinjamanResponse response = branchManagerPersetujuanService.persetujuan(pengajuanId, karyawanId, request);

        assertThat(response).isNotNull();
        assertThat(pengajuan.getStatusPengajuan()).isEqualTo("PENGAJUAN_DISETUJUI");
        assertThat(response.getHasilPersetujuan()).isEqualTo("DISETUJUI");
        verify(pengajuanRepository).save(pengajuan);
    }

    @Test
    @DisplayName("Persetujuan BM: Gagal jika BM mereview pengajuan cabang lain")
    void testBmCrossBranchForbidden() {
        UUID pengajuanId = UUID.randomUUID();
        UUID karyawanId = UUID.randomUUID();
        UUID branchA = UUID.randomUUID();
        UUID branchB = UUID.randomUUID();

        Cabang cabangA = new Cabang();
        cabangA.setId(branchA);

        Karyawan bm = new Karyawan();
        bm.setId(karyawanId);
        bm.setCabang(cabangA);

        PengajuanPinjaman pengajuan = new PengajuanPinjaman();
        pengajuan.setId(pengajuanId);
        pengajuan.setMstBranchId(branchB); // Cabang B (Berbeda)

        when(pengajuanRepository.findById(pengajuanId)).thenReturn(Optional.of(pengajuan));
        when(karyawanRepository.findById(karyawanId)).thenReturn(Optional.of(bm));

        PersetujuanPinjamanRequest request = new PersetujuanPinjamanRequest();
        request.setHasilPersetujuan("DISETUJUI");

        assertThatThrownBy(() -> branchManagerPersetujuanService.persetujuan(pengajuanId, karyawanId, request))
                .isInstanceOf(BussinessRuleException.class)
                .hasMessageContaining("Anda tidak memiliki izin");
    }
}
