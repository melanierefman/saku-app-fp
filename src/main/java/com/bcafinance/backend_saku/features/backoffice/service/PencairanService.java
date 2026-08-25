package com.bcafinance.backend_saku.features.backoffice.service;

import com.bcafinance.backend_saku.core.dto.AlamatDetailResponse;
import com.bcafinance.backend_saku.core.dto.DokumenPinjamanResponse;
import com.bcafinance.backend_saku.core.entity.AlamatCustomer;
import com.bcafinance.backend_saku.core.entity.Angsuran;
import com.bcafinance.backend_saku.core.entity.Cabang;
import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.entity.DokumenCustomer;
import com.bcafinance.backend_saku.core.entity.DokumenPinjaman;
import com.bcafinance.backend_saku.core.entity.Karyawan;
import com.bcafinance.backend_saku.core.entity.Pencairan;
import com.bcafinance.backend_saku.core.entity.PengajuanPinjaman;
import com.bcafinance.backend_saku.core.entity.Persetujuan;
import com.bcafinance.backend_saku.core.entity.ReviewPengajuan;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.AlamatCustomerRepository;
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
import com.bcafinance.backend_saku.features.backoffice.dto.AngsuranItemResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.PencairanDetailResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.PencairanItemResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.PencairanRequest;
import com.bcafinance.backend_saku.features.backoffice.dto.PencairanResponse;
import com.bcafinance.backend_saku.features.branchmanager.dto.PersetujuanPinjamanResponse;
import com.bcafinance.backend_saku.features.marketing.dto.ReviewPengajuanResponse;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PencairanService {

    private final PengajuanPinjamanRepository pengajuanRepository;
    private final PencairanRepository pencairanRepository;
    private final AngsuranRepository angsuranRepository;
    private final CustomerRepository customerRepository;
    private final AlamatCustomerRepository alamatRepository;
    private final DokumenCustomerRepository dokumenCustomerRepository;
    private final DokumenPinjamanRepository dokumenPinjamanRepository;
    private final KaryawanRepository karyawanRepository;
    private final CabangRepository cabangRepository;
    private final ReviewPengajuanRepository reviewPengajuanRepository;
    private final PersetujuanRepository persetujuanRepository;

    public List<PencairanItemResponse> findAll(String statusFilter) {
        List<PengajuanPinjaman> list = pengajuanRepository.findAllByOrderByCreatedDateDesc();

        return list.stream()
                .filter(p -> {
                    String status = p.getStatusPengajuan() != null ? p.getStatusPengajuan() : "";
                    boolean isApprovedByBM = "PENGAJUAN_DISETUJUI".equalsIgnoreCase(status)
                            || "DISETUJUI".equalsIgnoreCase(status)
                            || "APPROVED".equalsIgnoreCase(status);
                    boolean isDisbursed = "DICAIRKAN".equalsIgnoreCase(status)
                            || "DISBURSED".equalsIgnoreCase(status);

                    return isApprovedByBM || isDisbursed;
                })
                .map(p -> {
                    Customer customer = customerRepository.findById(p.getMstCustomerId()).orElse(null);
                    Cabang cabang = cabangRepository.findById(p.getMstBranchId()).orElse(null);

                    // Ambil approval BM terakhir
                    Optional<Persetujuan> latestApproval = persetujuanRepository
                            .findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(p.getId());

                    // Ambil data pencairan jika sudah dicairkan
                    Optional<Pencairan> pencairanOpt = pencairanRepository
                            .findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(p.getId());

                    String statusPencairan = pencairanOpt.isPresent() ? pencairanOpt.get().getStatusPencairan() : "MENUNGGU_PENCAIRAN";
                    LocalDateTime tglPencairan = pencairanOpt.map(Pencairan::getCreatedDate).orElse(null);
                    String namaPetugasBO = pencairanOpt.flatMap(pc -> karyawanRepository.findById(pc.getMstKaryawanId()))
                            .map(Karyawan::getNama)
                            .orElse("-");

                    BigDecimal jumlahPencairan = pencairanOpt.map(Pencairan::getJumlahPencairan)
                            .orElse(p.getJumlahPinjaman());

                    return PencairanItemResponse.builder()
                            .pengajuanId(p.getId())
                            .noPengajuan(p.getNomorPengajuan())
                            .customerId(p.getMstCustomerId())
                            .namaCustomer(customer != null ? customer.getNama() : "-")
                            .nik(customer != null ? customer.getNik() : "-")
                            .email(customer != null ? customer.getEmail() : "-")
                            .noHp(customer != null ? customer.getNoHp() : "-")
                            .namaBank(customer != null ? customer.getNamaBank() : "-")
                            .noRekening(customer != null ? customer.getNoRekening() : "-")
                            .namaRekening(customer != null ? customer.getNamaRekening() : "-")
                            .jumlahPinjaman(p.getJumlahPinjaman())
                            .biayaAdmin(p.getBiayaAdmin())
                            .jumlahPencairan(jumlahPencairan)
                            .tenorBulan(p.getTenorBulan())
                            .bunga(p.getBunga())
                            .namaCabang(cabang != null ? cabang.getNama() : "-")
                            .statusPengajuan(p.getStatusPengajuan())
                            .tanggalDisetujuiBM(latestApproval.map(Persetujuan::getCreatedDate).orElse(null))
                            .statusPencairan(statusPencairan)
                            .tanggalPencairan(tglPencairan)
                            .namaPetugasBackoffice(namaPetugasBO)
                            .build();
                })
                .filter(item -> {
                    if (statusFilter == null || statusFilter.isBlank() || "ALL".equalsIgnoreCase(statusFilter)) {
                        return true;
                    }
                    if ("MENUNGGU_PENCAIRAN".equalsIgnoreCase(statusFilter)
                            || "SIAP_DICAIRKAN".equalsIgnoreCase(statusFilter)
                            || "PENDING".equalsIgnoreCase(statusFilter)) {
                        return "MENUNGGU_PENCAIRAN".equalsIgnoreCase(item.getStatusPencairan());
                    }
                    if ("DICAIRKAN".equalsIgnoreCase(statusFilter)
                            || "BERHASIL".equalsIgnoreCase(statusFilter)
                            || "SELESAI".equalsIgnoreCase(statusFilter)
                            || "DISBURSED".equalsIgnoreCase(statusFilter)) {
                        return !"MENUNGGU_PENCAIRAN".equalsIgnoreCase(item.getStatusPencairan());
                    }
                    return statusFilter.equalsIgnoreCase(item.getStatusPencairan())
                            || statusFilter.equalsIgnoreCase(item.getStatusPengajuan());
                })
                .toList();
    }

    public PencairanDetailResponse getDetail(UUID pengajuanId) {
        PengajuanPinjaman pengajuan = pengajuanRepository.findById(pengajuanId)
                .orElseThrow(() -> new BussinessRuleException("Data pengajuan pinjaman tidak ditemukan"));

        UUID customerId = pengajuan.getMstCustomerId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Data nasabah tidak ditemukan"));

        Cabang cabang = cabangRepository.findById(pengajuan.getMstBranchId()).orElse(null);

        // 1. Alamat Nasabah
        Optional<AlamatCustomer> alamatKtpOpt = alamatRepository.findByCustomer_IdAndJenisAlamat(customerId, "KTP");
        Optional<AlamatCustomer> alamatDomisiliOpt = alamatRepository.findByCustomer_IdAndJenisAlamat(customerId, "DOMISILI");

        // 2. Dokumen Foto Identitas (KTP & Selfie)
        Optional<DokumenCustomer> ktpDocOpt = dokumenCustomerRepository.findByCustomer_IdAndDocType(customerId, "KTP");
        Optional<DokumenCustomer> selfieDocOpt = dokumenCustomerRepository.findByCustomer_IdAndDocType(customerId, "SELFIE");

        // 3. Dokumen Pinjaman (Slip Gaji, Rekening Koran, NPWP)
        List<DokumenPinjaman> loanDocs = dokumenPinjamanRepository.findAllByTrxPengajuanPinjamanId(pengajuanId);
        List<DokumenPinjamanResponse> docListResponse = loanDocs.stream()
                .map(d -> DokumenPinjamanResponse.builder()
                        .id(d.getId())
                        .docType(d.getDocType())
                        .fileUrl(d.getFileUrl())
                        .createdDate(d.getCreatedDate())
                        .build())
                .toList();

        // 4. Review Marketing Terakhir
        Optional<ReviewPengajuan> latestReview = reviewPengajuanRepository
                .findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(pengajuanId);
        ReviewPengajuanResponse marketingResponse = latestReview.map(r -> {
            String revName = karyawanRepository.findById(r.getMstKaryawanId()).map(Karyawan::getNama).orElse("-");
            return ReviewPengajuanResponse.builder()
                    .id(r.getId())
                    .pengajuanId(pengajuanId)
                    .nomorPengajuan(pengajuan.getNomorPengajuan())
                    .hasilReview(r.getHasilReview())
                    .catatan(r.getCatatan())
                    .reviewerId(r.getMstKaryawanId())
                    .namaReviewer(revName)
                    .tanggalReview(r.getCreatedDate())
                    .statusPengajuan(pengajuan.getStatusPengajuan())
                    .build();
        }).orElse(null);

        // 5. Approval BM Terakhir
        Optional<Persetujuan> latestApproval = persetujuanRepository
                .findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(pengajuanId);
        PersetujuanPinjamanResponse bmResponse = latestApproval.map(a -> {
            String appName = karyawanRepository.findById(a.getMstKaryawanId()).map(Karyawan::getNama).orElse("-");
            return PersetujuanPinjamanResponse.builder()
                    .id(a.getId())
                    .pengajuanId(pengajuanId)
                    .nomorPengajuan(pengajuan.getNomorPengajuan())
                    .hasilPersetujuan(a.getHasilPersetujuan())
                    .catatan(a.getCatatan())
                    .approverId(a.getMstKaryawanId())
                    .namaApprover(appName)
                    .tanggalPersetujuan(a.getCreatedDate())
                    .statusPengajuan(pengajuan.getStatusPengajuan())
                    .build();
        }).orElse(null);

        // 6. Data Pencairan (jika sudah pernah dicairkan)
        Optional<Pencairan> pencairanOpt = pencairanRepository
                .findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(pengajuanId);

        String statusPencairan = pencairanOpt.isPresent() ? pencairanOpt.get().getStatusPencairan() : "MENUNGGU_PENCAIRAN";
        LocalDateTime tglPencairan = pencairanOpt.map(Pencairan::getCreatedDate).orElse(null);
        UUID boKaryawanId = pencairanOpt.map(Pencairan::getMstKaryawanId).orElse(null);
        String namaPetugasBO = pencairanOpt.flatMap(pc -> karyawanRepository.findById(pc.getMstKaryawanId()))
                .map(Karyawan::getNama)
                .orElse("-");

        // 7. Jadwal Angsuran
        List<Angsuran> angsuranList = angsuranRepository.findAllByTrxPengajuanPinjamanIdOrderByCicilanKeAsc(pengajuanId);
        List<AngsuranItemResponse> angsuranResponseList = angsuranList.stream()
                .map(this::toAngsuranResponse)
                .toList();

        BigDecimal estimasiCicilan = calculateEstimasiAngsuran(
                pengajuan.getJumlahPinjaman(), pengajuan.getTenorBulan(), pengajuan.getBunga());

        BigDecimal jumlahPencairanBersih = pencairanOpt.map(Pencairan::getJumlahPencairan)
                .orElseGet(() -> {
                    BigDecimal biayaAdmin = pengajuan.getBiayaAdmin() != null ? pengajuan.getBiayaAdmin() : BigDecimal.ZERO;
                    return pengajuan.getJumlahPinjaman().subtract(biayaAdmin);
                });

        return PencairanDetailResponse.builder()
                .pengajuanId(pengajuan.getId())
                .nomorPengajuan(pengajuan.getNomorPengajuan())
                .tanggalPengajuan(pengajuan.getCreatedDate())
                .statusPengajuan(pengajuan.getStatusPengajuan())
                .catatanPengajuan(pengajuan.getCatatanReview())
                .customerId(customer.getId())
                .namaLengkap(customer.getNama())
                .nik(customer.getNik())
                .email(customer.getEmail())
                .noHp(customer.getNoHp())
                .alamatKtp(alamatKtpOpt.map(this::mapAlamat).orElse(null))
                .alamatDomisili(alamatDomisiliOpt.map(this::mapAlamat).orElse(null))
                .namaBank(customer.getNamaBank())
                .noRekening(customer.getNoRekening())
                .namaRekening(customer.getNamaRekening())
                .jumlahPinjaman(pengajuan.getJumlahPinjaman())
                .tenorBulan(pengajuan.getTenorBulan())
                .tujuanPinjaman(pengajuan.getTujuanPinjaman())
                .bunga(pengajuan.getBunga())
                .biayaAdmin(pengajuan.getBiayaAdmin())
                .jumlahPencairanBersih(jumlahPencairanBersih)
                .estimasiAngsuranBulanan(estimasiCicilan)
                .branchId(pengajuan.getMstBranchId())
                .namaCabang(cabang != null ? cabang.getNama() : "-")
                .kotaCabang(cabang != null ? cabang.getKota() : "-")
                .fotoSelfie(selfieDocOpt.map(DokumenCustomer::getFileUrl).orElse(null))
                .fotoKtp(ktpDocOpt.map(DokumenCustomer::getFileUrl).orElse(null))
                .dokumenPinjamanList(docListResponse)
                .reviewMarketingTerakhir(marketingResponse)
                .persetujuanBMTerakhir(bmResponse)
                .pencairanId(pencairanOpt.map(Pencairan::getId).orElse(null))
                .statusPencairan(statusPencairan)
                .tanggalPencairan(tglPencairan)
                .disbursedByKaryawanId(boKaryawanId)
                .namaPetugasBackoffice(namaPetugasBO)
                .listAngsuran(angsuranResponseList)
                .build();
    }

    @Transactional
    public PencairanResponse cairkanPinjaman(UUID pengajuanId, UUID karyawanId, PencairanRequest request) {
        PengajuanPinjaman pengajuan = pengajuanRepository.findById(pengajuanId)
                .orElseThrow(() -> new BussinessRuleException("Data pengajuan pinjaman tidak ditemukan"));

        Karyawan karyawan = karyawanRepository.findById(karyawanId)
                .orElseThrow(() -> new BussinessRuleException("Data Karyawan Backoffice tidak ditemukan"));

        Customer customer = customerRepository.findById(pengajuan.getMstCustomerId())
                .orElseThrow(() -> new BussinessRuleException("Data nasabah tidak ditemukan"));

        String currentStatus = pengajuan.getStatusPengajuan() != null ? pengajuan.getStatusPengajuan() : "";

        // Validasi: Status pengajuan harus sudah disetujui BM
        boolean isApproved = "PENGAJUAN_DISETUJUI".equalsIgnoreCase(currentStatus)
                || "DISETUJUI".equalsIgnoreCase(currentStatus)
                || "APPROVED".equalsIgnoreCase(currentStatus);

        if (!isApproved) {
            throw new BussinessRuleException(
                    "Pinjaman tidak dapat dicairkan karena belum disetujui oleh Branch Manager (Status saat ini: "
                            + currentStatus + ")");
        }

        // Cek apakah sudah pernah dicairkan
        boolean alreadyDisbursed = pencairanRepository.existsByTrxPengajuanPinjamanIdAndStatusPencairan(
                pengajuanId, "BERHASIL");
        if (alreadyDisbursed) {
            throw new BussinessRuleException("Pinjaman ini sudah pernah dicairkan sebelumnya.");
        }

        // Tentukan jumlah pencairan
        BigDecimal jumlahPencairan;
        if (request != null && request.getJumlahPencairan() != null && request.getJumlahPencairan().compareTo(BigDecimal.ZERO) > 0) {
            jumlahPencairan = request.getJumlahPencairan();
        } else {
            BigDecimal biayaAdmin = pengajuan.getBiayaAdmin() != null ? pengajuan.getBiayaAdmin() : BigDecimal.ZERO;
            jumlahPencairan = pengajuan.getJumlahPinjaman().subtract(biayaAdmin);
            if (jumlahPencairan.compareTo(BigDecimal.ZERO) <= 0) {
                jumlahPencairan = pengajuan.getJumlahPinjaman();
            }
        }

        // 1. Simpan Transaksi Pencairan ke trx_pencairan
        Pencairan pencairan = new Pencairan();
        pencairan.setId(UUID.randomUUID());
        pencairan.setJumlahPencairan(jumlahPencairan);
        pencairan.setStatusPencairan("BERHASIL");
        pencairan.setCreatedDate(LocalDateTime.now());
        pencairan.setUpdatedDate(LocalDateTime.now());
        pencairan.setTrxPengajuanPinjamanId(pengajuanId);
        pencairan.setMstKaryawanId(karyawanId);

        Pencairan savedPencairan = pencairanRepository.save(pencairan);

        // 2. Update status pengajuan pinjaman menjadi DICAIRKAN
        pengajuan.setStatusPengajuan("DICAIRKAN");
        String catatanBO = (request != null && request.getCatatan() != null && !request.getCatatan().isBlank())
                ? request.getCatatan()
                : "Dana pinjaman telah dicairkan ke rekening nasabah";
        pengajuan.setCatatanReview(catatanBO);
        pengajuan.setUpdatedDate(LocalDateTime.now());
        pengajuanRepository.save(pengajuan);

        // 3. Generate Jadwal Angsuran (trx_angsuran)
        angsuranRepository.deleteByTrxPengajuanPinjamanId(pengajuanId);

        BigDecimal estimasiCicilan = calculateEstimasiAngsuran(
                pengajuan.getJumlahPinjaman(), pengajuan.getTenorBulan(), pengajuan.getBunga());

        LocalDate today = LocalDate.now();
        List<AngsuranItemResponse> listAngsuranResponse = new ArrayList<>();

        for (int i = 1; i <= pengajuan.getTenorBulan(); i++) {
            Angsuran angsuran = new Angsuran();
            angsuran.setId(UUID.randomUUID());
            angsuran.setCicilanKe(i);
            angsuran.setJumlahAngsuran(estimasiCicilan);
            angsuran.setJatuhTempo(today.plusMonths(i));
            angsuran.setStatusBayar("BELUM_LUNAS");
            angsuran.setCreatedDate(LocalDateTime.now());
            angsuran.setUpdatedDate(LocalDateTime.now());
            angsuran.setTrxPengajuanPinjamanId(pengajuanId);

            Angsuran savedAngsuran = angsuranRepository.save(angsuran);
            listAngsuranResponse.add(toAngsuranResponse(savedAngsuran));
        }

        return PencairanResponse.builder()
                .pencairanId(savedPencairan.getId())
                .pengajuanId(pengajuanId)
                .nomorPengajuan(pengajuan.getNomorPengajuan())
                .customerId(customer.getId())
                .namaCustomer(customer.getNama())
                .namaBank(customer.getNamaBank())
                .noRekening(customer.getNoRekening())
                .namaRekening(customer.getNamaRekening())
                .jumlahPinjaman(pengajuan.getJumlahPinjaman())
                .biayaAdmin(pengajuan.getBiayaAdmin())
                .jumlahPencairan(jumlahPencairan)
                .statusPencairan("BERHASIL")
                .statusPengajuan("DICAIRKAN")
                .tanggalPencairan(savedPencairan.getCreatedDate())
                .disbursedByKaryawanId(karyawanId)
                .namaPetugasBackoffice(karyawan.getNama())
                .listAngsuran(listAngsuranResponse)
                .build();
    }

    private AngsuranItemResponse toAngsuranResponse(Angsuran a) {
        if (a == null) return null;
        return AngsuranItemResponse.builder()
                .id(a.getId())
                .cicilanKe(a.getCicilanKe())
                .jumlahAngsuran(a.getJumlahAngsuran())
                .jatuhTempo(a.getJatuhTempo())
                .statusBayar(a.getStatusBayar())
                .build();
    }

    private BigDecimal calculateEstimasiAngsuran(BigDecimal jumlahPinjaman, Integer tenorBulan, BigDecimal bungaTahunan) {
        if (jumlahPinjaman == null || tenorBulan == null || tenorBulan <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal pokokBulanan = jumlahPinjaman.divide(BigDecimal.valueOf(tenorBulan), 2, RoundingMode.HALF_UP);
        BigDecimal rate = bungaTahunan != null ? bungaTahunan : BigDecimal.ZERO;
        BigDecimal bungaBulanan = jumlahPinjaman
                .multiply(rate.movePointLeft(2))
                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

        return pokokBulanan.add(bungaBulanan);
    }

    public List<AngsuranItemResponse> getJadwalAngsuran(UUID pengajuanId) {
        PengajuanPinjaman pengajuan = pengajuanRepository.findById(pengajuanId)
                .orElseThrow(() -> new BussinessRuleException("Data pengajuan pinjaman tidak ditemukan"));

        return angsuranRepository.findAllByTrxPengajuanPinjamanIdOrderByCicilanKeAsc(pengajuan.getId())
                .stream()
                .map(this::toAngsuranResponse)
                .toList();
    }

    private AlamatDetailResponse mapAlamat(AlamatCustomer alamat) {
        if (alamat == null) return null;

        StringBuilder formatted = new StringBuilder();
        if (alamat.getAlamatLengkap() != null) {
            formatted.append(alamat.getAlamatLengkap());
        }
        if (alamat.getRt() != null || alamat.getRw() != null) {
            formatted.append(", RT ").append(alamat.getRt() != null ? alamat.getRt() : "-")
                    .append("/RW ").append(alamat.getRw() != null ? alamat.getRw() : "-");
        }
        if (alamat.getKelurahan() != null) {
            formatted.append(", Kel. ").append(alamat.getKelurahan());
        }
        if (alamat.getKecamatan() != null) {
            formatted.append(", Kec. ").append(alamat.getKecamatan());
        }
        if (alamat.getKotaKabupaten() != null) {
            formatted.append(", ").append(alamat.getKotaKabupaten());
        }
        if (alamat.getProvinsi() != null) {
            formatted.append(", ").append(alamat.getProvinsi());
        }
        if (alamat.getKodePos() != null) {
            formatted.append(" ").append(alamat.getKodePos());
        }

        return AlamatDetailResponse.builder()
                .jenisAlamat(alamat.getJenisAlamat())
                .alamatLengkap(alamat.getAlamatLengkap())
                .rt(alamat.getRt())
                .rw(alamat.getRw())
                .kelurahan(alamat.getKelurahan())
                .kecamatan(alamat.getKecamatan())
                .kotaKabupaten(alamat.getKotaKabupaten())
                .provinsi(alamat.getProvinsi())
                .kodePos(alamat.getKodePos())
                .formattedAddress(formatted.toString().trim())
                .build();
    }
}

