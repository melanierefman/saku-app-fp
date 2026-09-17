package com.bcafinance.backend_saku.features.backoffice.service;

import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.entity.Karyawan;
import com.bcafinance.backend_saku.core.entity.Pencairan;
import com.bcafinance.backend_saku.core.entity.PengajuanPinjaman;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.notification.EmailService;
import com.bcafinance.backend_saku.core.repository.AngsuranRepository;
import com.bcafinance.backend_saku.core.repository.CabangRepository;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.repository.DokumenCustomerRepository;
import com.bcafinance.backend_saku.core.repository.DokumenPinjamanRepository;
import com.bcafinance.backend_saku.core.repository.KaryawanRepository;
import com.bcafinance.backend_saku.core.repository.PencairanRepository;
import com.bcafinance.backend_saku.core.repository.PengajuanPinjamanRepository;
import com.bcafinance.backend_saku.core.repository.PersetujuanRepository;
import com.bcafinance.backend_saku.core.repository.ReviewPengajuanRepository;
import com.bcafinance.backend_saku.features.backoffice.dto.PencairanRequest;
import com.bcafinance.backend_saku.features.backoffice.dto.PencairanResponse;
import com.bcafinance.backend_saku.features.customer.service.NotifikasiService;
import com.bcafinance.backend_saku.features.superadmin.auditlog.service.AuditLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PencairanServiceTest {

    @Mock
    private PengajuanPinjamanRepository pengajuanRepository;

    @Mock
    private PencairanRepository pencairanRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private KaryawanRepository karyawanRepository;

    @Mock
    private AngsuranRepository angsuranRepository;

    @Mock
    private PersetujuanRepository persetujuanRepository;

    @Mock
    private ReviewPengajuanRepository reviewPengajuanRepository;

    @Mock
    private CabangRepository cabangRepository;

    @Mock
    private DokumenCustomerRepository dokumenCustomerRepository;

    @Mock
    private DokumenPinjamanRepository dokumenPinjamanRepository;

    @Mock
    private NotifikasiService notifikasiService;

    @Mock
    private EmailService emailService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private PencairanService pencairanService;

    @Test
    @DisplayName("Cairkan: Gagal jika status pengajuan belum disetujui Branch Manager")
    void testCairkanPinjamanNotApprovedByBm() {
        UUID pengajuanId = UUID.randomUUID();
        UUID karyawanId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        PengajuanPinjaman pengajuan = new PengajuanPinjaman();
        pengajuan.setId(pengajuanId);
        pengajuan.setMstCustomerId(customerId);
        pengajuan.setStatusPengajuan("MENUNGGU_REVIEW");

        Karyawan karyawan = new Karyawan();
        karyawan.setId(karyawanId);

        Customer customer = new Customer();
        customer.setId(customerId);

        when(pengajuanRepository.findById(pengajuanId)).thenReturn(Optional.of(pengajuan));
        when(karyawanRepository.findById(karyawanId)).thenReturn(Optional.of(karyawan));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        PencairanRequest request = new PencairanRequest();
        request.setCatatan("Pencairan dana");

        assertThatThrownBy(() -> pencairanService.cairkanPinjaman(pengajuanId, karyawanId, request))
                .isInstanceOf(BussinessRuleException.class)
                .hasMessageContaining("belum disetujui oleh Branch Manager");
    }

    @Test
    @DisplayName("Cairkan: Berhasil mencairkan dana untuk pinjaman yang statusnya PENGAJUAN_DISETUJUI")
    void testCairkanPinjamanSuccess() {
        UUID pengajuanId = UUID.randomUUID();
        UUID karyawanId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        PengajuanPinjaman pengajuan = new PengajuanPinjaman();
        pengajuan.setId(pengajuanId);
        pengajuan.setNomorPengajuan("PJ-2026-999");
        pengajuan.setMstCustomerId(customerId);
        pengajuan.setMstBranchId(branchId);
        pengajuan.setStatusPengajuan("PENGAJUAN_DISETUJUI");
        pengajuan.setJumlahPinjaman(BigDecimal.valueOf(10_000_000));
        pengajuan.setTenorBulan(12);
        pengajuan.setBunga(BigDecimal.valueOf(0.05));
        pengajuan.setBiayaAdmin(BigDecimal.valueOf(100_000));

        Karyawan karyawan = new Karyawan();
        karyawan.setId(karyawanId);
        karyawan.setNama("BO Staff");

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setNama("Budi");
        customer.setNamaBank("BCA");
        customer.setNoRekening("1234567890");
        customer.setNamaRekening("Budi Santoso");

        when(pengajuanRepository.findById(pengajuanId)).thenReturn(Optional.of(pengajuan));
        when(karyawanRepository.findById(karyawanId)).thenReturn(Optional.of(karyawan));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(pencairanRepository.existsByTrxPengajuanPinjamanIdAndStatusPencairan(pengajuanId, "BERHASIL")).thenReturn(false);
        when(pencairanRepository.save(any(Pencairan.class))).thenAnswer(i -> i.getArgument(0));

        PencairanRequest request = new PencairanRequest();
        request.setCatatan("Pencairan dana berhasil ditransfer ke rekening BCA nasabah");

        PencairanResponse response = pencairanService.cairkanPinjaman(pengajuanId, karyawanId, request);

        assertThat(response).isNotNull();
        assertThat(pengajuan.getStatusPengajuan()).isEqualTo("DICAIRKAN");
        assertThat(response.getStatusPencairan()).isEqualTo("BERHASIL");
        verify(pencairanRepository).save(any(Pencairan.class));
        verify(pengajuanRepository).save(pengajuan);
    }
}
