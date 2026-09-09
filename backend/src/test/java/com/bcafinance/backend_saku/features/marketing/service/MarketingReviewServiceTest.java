package com.bcafinance.backend_saku.features.marketing.service;

import com.bcafinance.backend_saku.core.entity.Cabang;
import com.bcafinance.backend_saku.core.entity.Karyawan;
import com.bcafinance.backend_saku.core.entity.PengajuanPinjaman;
import com.bcafinance.backend_saku.core.entity.ReviewPengajuan;
import com.bcafinance.backend_saku.core.repository.AlamatCustomerRepository;
import com.bcafinance.backend_saku.core.repository.CabangRepository;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.repository.DokumenCustomerRepository;
import com.bcafinance.backend_saku.core.repository.DokumenPinjamanRepository;
import com.bcafinance.backend_saku.core.repository.KaryawanRepository;
import com.bcafinance.backend_saku.core.repository.PengajuanPinjamanRepository;
import com.bcafinance.backend_saku.core.repository.ReviewPengajuanRepository;
import com.bcafinance.backend_saku.core.repository.ScoringCustomerRepository;
import com.bcafinance.backend_saku.features.customer.service.NotifikasiService;
import com.bcafinance.backend_saku.features.marketing.dto.ReviewPengajuanRequest;
import com.bcafinance.backend_saku.features.marketing.dto.ReviewPengajuanResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketingReviewServiceTest {

    @Mock
    private PengajuanPinjamanRepository pengajuanRepository;

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
    private MarketingReviewService marketingReviewService;

    @Test
    @DisplayName("Review: Marketing menyetujui pengajuan pinjaman (Status -> SELESAI_DIREVIEW)")
    void testReviewApproveSuccess() {
        UUID pengajuanId = UUID.randomUUID();
        UUID karyawanId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        Cabang cabang = new Cabang();
        cabang.setId(branchId);

        Karyawan karyawan = new Karyawan();
        karyawan.setId(karyawanId);
        karyawan.setNama("Marketing Officer");
        karyawan.setCabang(cabang);

        PengajuanPinjaman pengajuan = new PengajuanPinjaman();
        pengajuan.setId(pengajuanId);
        pengajuan.setNomorPengajuan("PJ-2026-001");
        pengajuan.setMstBranchId(branchId);
        pengajuan.setStatusPengajuan("MENUNGGU_REVIEW");

        when(pengajuanRepository.findById(pengajuanId)).thenReturn(Optional.of(pengajuan));
        when(karyawanRepository.findById(karyawanId)).thenReturn(Optional.of(karyawan));
        when(reviewPengajuanRepository.save(any(ReviewPengajuan.class))).thenAnswer(i -> i.getArgument(0));

        ReviewPengajuanRequest request = new ReviewPengajuanRequest();
        request.setHasilReview("DISETUJUI");
        request.setCatatan("Dokumen lengkap dan valid");

        ReviewPengajuanResponse response = marketingReviewService.review(pengajuanId, karyawanId, request);

        assertThat(response).isNotNull();
        assertThat(pengajuan.getStatusPengajuan()).isEqualTo("SELESAI_DIREVIEW");
        assertThat(response.getHasilReview()).isEqualTo("DISETUJUI");
        verify(pengajuanRepository).save(pengajuan);
    }

    @Test
    @DisplayName("Review: Marketing menolak pengajuan pinjaman (Status -> PENGAJUAN_DITOLAK)")
    void testReviewRejectSuccess() {
        UUID pengajuanId = UUID.randomUUID();
        UUID karyawanId = UUID.randomUUID();

        Karyawan karyawan = new Karyawan();
        karyawan.setId(karyawanId);
        karyawan.setNama("Marketing Officer");

        PengajuanPinjaman pengajuan = new PengajuanPinjaman();
        pengajuan.setId(pengajuanId);
        pengajuan.setNomorPengajuan("PJ-2026-002");
        pengajuan.setStatusPengajuan("MENUNGGU_REVIEW");

        when(pengajuanRepository.findById(pengajuanId)).thenReturn(Optional.of(pengajuan));
        when(karyawanRepository.findById(karyawanId)).thenReturn(Optional.of(karyawan));
        when(reviewPengajuanRepository.save(any(ReviewPengajuan.class))).thenAnswer(i -> i.getArgument(0));

        ReviewPengajuanRequest request = new ReviewPengajuanRequest();
        request.setHasilReview("DITOLAK");
        request.setCatatan("Penghasilan tidak mencukupi batas DBR");

        ReviewPengajuanResponse response = marketingReviewService.review(pengajuanId, karyawanId, request);

        assertThat(response).isNotNull();
        assertThat(pengajuan.getStatusPengajuan()).isEqualTo("PENGAJUAN_DITOLAK");
        assertThat(response.getHasilReview()).isEqualTo("DITOLAK");
        verify(pengajuanRepository).save(pengajuan);
    }
}
