package com.bcafinance.backend_saku.service;

import com.bcafinance.backend_saku.dto.AlamatDetailResponse;
import com.bcafinance.backend_saku.dto.BranchManagerPengajuanDetailResponse;
import com.bcafinance.backend_saku.dto.BranchManagerPengajuanItemResponse;
import com.bcafinance.backend_saku.dto.DokumenPinjamanResponse;
import com.bcafinance.backend_saku.dto.PersetujuanPinjamanRequest;
import com.bcafinance.backend_saku.dto.PersetujuanPinjamanResponse;
import com.bcafinance.backend_saku.dto.ReviewPengajuanResponse;
import com.bcafinance.backend_saku.dto.ScoringAnalysisResponse;
import com.bcafinance.backend_saku.entity.AlamatCustomer;
import com.bcafinance.backend_saku.entity.Cabang;
import com.bcafinance.backend_saku.entity.Customer;
import com.bcafinance.backend_saku.entity.DokumenCustomer;
import com.bcafinance.backend_saku.entity.DokumenPinjaman;
import com.bcafinance.backend_saku.entity.Karyawan;
import com.bcafinance.backend_saku.entity.PengajuanPinjaman;
import com.bcafinance.backend_saku.entity.Persetujuan;
import com.bcafinance.backend_saku.entity.ReviewPengajuan;
import com.bcafinance.backend_saku.entity.ScoringCustomer;
import com.bcafinance.backend_saku.exception.BussinessRuleException;
import com.bcafinance.backend_saku.repository.AlamatCustomerRepository;
import com.bcafinance.backend_saku.repository.CabangRepository;
import com.bcafinance.backend_saku.repository.CustomerRepository;
import com.bcafinance.backend_saku.repository.DokumenCustomerRepository;
import com.bcafinance.backend_saku.repository.DokumenPinjamanRepository;
import com.bcafinance.backend_saku.repository.KaryawanRepository;
import com.bcafinance.backend_saku.repository.PengajuanPinjamanRepository;
import com.bcafinance.backend_saku.repository.PersetujuanRepository;
import com.bcafinance.backend_saku.repository.ReviewPengajuanRepository;
import com.bcafinance.backend_saku.repository.ScoringCustomerRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BranchManagerPersetujuanService {

    private final PengajuanPinjamanRepository pengajuanRepository;
    private final PersetujuanRepository persetujuanRepository;
    private final ReviewPengajuanRepository reviewPengajuanRepository;
    private final CustomerRepository customerRepository;
    private final AlamatCustomerRepository alamatRepository;
    private final DokumenCustomerRepository dokumenCustomerRepository;
    private final DokumenPinjamanRepository dokumenPinjamanRepository;
    private final ScoringCustomerRepository scoringRepository;
    private final KaryawanRepository karyawanRepository;
    private final CabangRepository cabangRepository;
    private final ScoringService scoringService;

    public List<BranchManagerPengajuanItemResponse> findAll(String statusFilter) {
        List<PengajuanPinjaman> list = pengajuanRepository.findAllByOrderByCreatedDateDesc();

        return list.stream()
                .filter(p -> {
                    // Hanya tampilkan pengajuan yang sudah selesai direview oleh marketing atau sudah diproses oleh BM
                    String status = p.getStatusPengajuan() != null ? p.getStatusPengajuan() : "";
                    boolean isReviewedByMarketing = "SELESAI_DIREVIEW".equalsIgnoreCase(status)
                            || "PENGAJUAN_DISETUJUI".equalsIgnoreCase(status);
                    boolean isRejectedWithBMReview = "PENGAJUAN_DITOLAK".equalsIgnoreCase(status)
                            && persetujuanRepository.findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(p.getId()).isPresent();

                    return isReviewedByMarketing || isRejectedWithBMReview;
                })
                .map(p -> {
                    Customer customer = customerRepository.findById(p.getMstCustomerId()).orElse(null);
                    Cabang cabang = cabangRepository.findById(p.getMstBranchId()).orElse(null);

                    // Ambil review marketing terakhir
                    Optional<ReviewPengajuan> latestMarketingReview = reviewPengajuanRepository
                            .findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(p.getId());

                    String namaMarketing = "-";
                    String catatanMarketing = "-";
                    LocalDateTime tglMarketing = null;
                    if (latestMarketingReview.isPresent()) {
                        ReviewPengajuan rev = latestMarketingReview.get();
                        catatanMarketing = rev.getCatatan();
                        tglMarketing = rev.getCreatedDate();
                        namaMarketing = karyawanRepository.findById(rev.getMstKaryawanId())
                                .map(Karyawan::getNama)
                                .orElse("-");
                    }

                    // Ambil persetujuan BM terakhir jika sudah pernah diproses
                    Optional<Persetujuan> latestApproval = persetujuanRepository
                            .findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(p.getId());

                    String statusTampilanBM = mapStatusTampilanBM(p.getStatusPengajuan(), latestApproval);

                    return BranchManagerPengajuanItemResponse.builder()
                            .pengajuanId(p.getId())
                            .noPengajuan(p.getNomorPengajuan())
                            .customerId(p.getMstCustomerId())
                            .customer(customer != null ? customer.getNama() : "-")
                            .email(customer != null ? customer.getEmail() : "-")
                            .noHp(customer != null ? customer.getNoHp() : "-")
                            .tanggalPengajuan(p.getCreatedDate())
                            .jumlah(p.getJumlahPinjaman())
                            .tenor(p.getTenorBulan())
                            .cabang(cabang != null ? cabang.getNama() : "-")
                            .status(statusTampilanBM)
                            .namaMarketingReviewer(namaMarketing)
                            .catatanMarketing(catatanMarketing)
                            .tanggalReviewMarketing(tglMarketing)
                            .hasilPersetujuanTerakhir(latestApproval.map(Persetujuan::getHasilPersetujuan).orElse(null))
                            .catatanPersetujuanTerakhir(latestApproval.map(Persetujuan::getCatatan).orElse(null))
                            .tanggalPersetujuanTerakhir(latestApproval.map(Persetujuan::getCreatedDate).orElse(null))
                            .build();
                })
                .filter(item -> {
                    if (statusFilter == null || statusFilter.isBlank() || "ALL".equalsIgnoreCase(statusFilter)) {
                        return true;
                    }
                    if ("MENUNGGU_PERSETUJUAN".equalsIgnoreCase(statusFilter) || "PENDING".equalsIgnoreCase(statusFilter)) {
                        return "MENUNGGU_PERSETUJUAN".equalsIgnoreCase(item.getStatus());
                    }
                    if ("PENGAJUAN_DISETUJUI".equalsIgnoreCase(statusFilter) || "DISETUJUI".equalsIgnoreCase(statusFilter) || "APPROVED".equalsIgnoreCase(statusFilter)) {
                        return "PENGAJUAN_DISETUJUI".equalsIgnoreCase(item.getStatus());
                    }
                    if ("PENGAJUAN_DITOLAK".equalsIgnoreCase(statusFilter) || "DITOLAK".equalsIgnoreCase(statusFilter) || "REJECTED".equalsIgnoreCase(statusFilter)) {
                        return "PENGAJUAN_DITOLAK".equalsIgnoreCase(item.getStatus());
                    }
                    return statusFilter.equalsIgnoreCase(item.getStatus());
                })
                .toList();
    }

    public BranchManagerPengajuanDetailResponse getDetail(UUID pengajuanId) {
        PengajuanPinjaman pengajuan = pengajuanRepository.findById(pengajuanId)
                .orElseThrow(() -> new BussinessRuleException("Pengajuan pinjaman tidak ditemukan"));

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
        String slipGajiUrl = loanDocs.stream()
                .filter(d -> "SLIP_GAJI".equalsIgnoreCase(d.getDocType()))
                .map(DokumenPinjaman::getFileUrl)
                .findFirst().orElse(null);
        String rekeningKoranUrl = loanDocs.stream()
                .filter(d -> "REKENING_KORAN".equalsIgnoreCase(d.getDocType()))
                .map(DokumenPinjaman::getFileUrl)
                .findFirst().orElse(null);
        String npwpUrl = loanDocs.stream()
                .filter(d -> "NPWP".equalsIgnoreCase(d.getDocType()))
                .map(DokumenPinjaman::getFileUrl)
                .findFirst().orElse(null);

        List<DokumenPinjamanResponse> docListResponse = loanDocs.stream()
                .map(d -> DokumenPinjamanResponse.builder()
                        .id(d.getId())
                        .docType(d.getDocType())
                        .fileUrl(d.getFileUrl())
                        .createdDate(d.getCreatedDate())
                        .build())
                .toList();

        // 4. Data Scoring Customer & Analisis Ambigu
        Optional<ScoringCustomer> scoringOpt = Optional.empty();
        if (pengajuan.getTrxScoringCustomerId() != null) {
            scoringOpt = scoringRepository.findById(pengajuan.getTrxScoringCustomerId());
        }
        if (scoringOpt.isEmpty()) {
            scoringOpt = scoringRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId);
        }

        ScoringAnalysisResponse analysis = scoringOpt.map(scoringService::analyzeScoring).orElse(null);

        // 5. Review Marketing Terakhir & Riwayat
        List<ReviewPengajuan> reviewEntities = reviewPengajuanRepository
                .findAllByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(pengajuanId);

        List<ReviewPengajuanResponse> reviewMarketingHistory = reviewEntities.stream()
                .map(r -> {
                    String reviewerName = karyawanRepository.findById(r.getMstKaryawanId())
                            .map(Karyawan::getNama)
                            .orElse("-");
                    return ReviewPengajuanResponse.builder()
                            .id(r.getId())
                            .pengajuanId(pengajuanId)
                            .nomorPengajuan(pengajuan.getNomorPengajuan())
                            .hasilReview(r.getHasilReview())
                            .catatan(r.getCatatan())
                            .reviewerId(r.getMstKaryawanId())
                            .namaReviewer(reviewerName)
                            .tanggalReview(r.getCreatedDate())
                            .statusPengajuan(pengajuan.getStatusPengajuan())
                            .build();
                })
                .toList();

        ReviewPengajuanResponse latestMarketing = reviewMarketingHistory.isEmpty() ? null : reviewMarketingHistory.get(0);

        // 6. Riwayat Persetujuan Branch Manager
        List<Persetujuan> persetujuanEntities = persetujuanRepository
                .findAllByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(pengajuanId);

        List<PersetujuanPinjamanResponse> persetujuanHistory = persetujuanEntities.stream()
                .map(a -> {
                    String approverName = karyawanRepository.findById(a.getMstKaryawanId())
                            .map(Karyawan::getNama)
                            .orElse("-");
                    return PersetujuanPinjamanResponse.builder()
                            .id(a.getId())
                            .pengajuanId(pengajuanId)
                            .nomorPengajuan(pengajuan.getNomorPengajuan())
                            .hasilPersetujuan(a.getHasilPersetujuan())
                            .catatan(a.getCatatan())
                            .approverId(a.getMstKaryawanId())
                            .namaApprover(approverName)
                            .tanggalPersetujuan(a.getCreatedDate())
                            .statusPengajuan(pengajuan.getStatusPengajuan())
                            .build();
                })
                .toList();

        BigDecimal estimasiCicilan = calculateEstimasiAngsuran(
                pengajuan.getJumlahPinjaman(), pengajuan.getTenorBulan(), pengajuan.getBunga());

        return BranchManagerPengajuanDetailResponse.builder()
                // 1. Info Utama Pengajuan
                .pengajuanId(pengajuan.getId())
                .nomorPengajuan(pengajuan.getNomorPengajuan())
                .tanggalPengajuan(pengajuan.getCreatedDate())
                .statusPengajuan(pengajuan.getStatusPengajuan())
                .catatanPengajuan(pengajuan.getCatatanReview())

                // 2. Data Customer
                .customerId(customer.getId())
                .namaLengkap(customer.getNama())
                .email(customer.getEmail())
                .nik(customer.getNik())
                .noHp(customer.getNoHp())
                .pekerjaan(scoringOpt.map(ScoringCustomer::getPekerjaan).orElse(null))
                .tempatKerja(scoringOpt.map(ScoringCustomer::getTempatKerja).orElse(null))
                .statusPekerjaan(scoringOpt.map(ScoringCustomer::getStatusPekerjaan).orElse(null))
                .pendapatan(scoringOpt.map(ScoringCustomer::getPenghasilanBulanan).orElse(null))
                .penghasilanBulanan(scoringOpt.map(ScoringCustomer::getPenghasilanBulanan).orElse(null))
                .namaBank(customer.getNamaBank())
                .noRekening(customer.getNoRekening())
                .namaRekening(customer.getNamaRekening())
                .alamatKtp(alamatKtpOpt.map(this::mapAlamat).orElse(null))
                .alamatDomisili(alamatDomisiliOpt.map(this::mapAlamat).orElse(null))

                // 3. Dokumen Foto Identitas & Pinjaman
                .fotoSelfie(selfieDocOpt.map(DokumenCustomer::getFileUrl).orElse(null))
                .fotoKtp(ktpDocOpt.map(DokumenCustomer::getFileUrl).orElse(null))
                .fotoSlipGaji(slipGajiUrl)
                .fotoRekeningKoran(rekeningKoranUrl)
                .fotoNpwp(npwpUrl)
                .dokumenPinjamanList(docListResponse)

                // 4. Hasil Scoring & Indikator Ambigu
                .statusPekerjaanScoring(scoringOpt.map(ScoringCustomer::getStatusPekerjaan).orElse(null))
                .penghasilanBulananScoring(scoringOpt.map(ScoringCustomer::getPenghasilanBulanan).orElse(null))
                .lamaBekerjaBulan(scoringOpt.map(ScoringCustomer::getLamaBekerjaBulan).orElse(null))
                .lamaJadiNasabahBulan(scoringOpt.map(ScoringCustomer::getLamaJadiNasabahBulan).orElse(null))
                .cicilanBerjalan(scoringOpt.map(ScoringCustomer::getTotalCicilanLainBulanan).orElse(null))
                .skor(scoringOpt.map(ScoringCustomer::getSkor).orElse(null))
                .statusScoring(scoringOpt.map(ScoringCustomer::getStatusScoring).orElse(null))
                .plafonNama(analysis != null ? analysis.getMatchedPlafondNama() : null)
                .plafonMaksimal(analysis != null ? analysis.getMatchedPlafondMaksimal() : null)
                .notesAmbigu(analysis != null ? analysis.getIndikatorAmbigu() : List.of())
                .ringkasanScoring(analysis != null ? analysis.getRingkasanAnalisis() : null)


                // 5. Detail Pinjaman
                .jumlahPinjaman(pengajuan.getJumlahPinjaman())
                .tenorBulan(pengajuan.getTenorBulan())
                .tujuanPinjaman(pengajuan.getTujuanPinjaman())
                .bunga(pengajuan.getBunga())
                .biayaAdmin(pengajuan.getBiayaAdmin())
                .estimasiCicilan(estimasiCicilan)
                .branchId(pengajuan.getMstBranchId())
                .namaCabang(cabang != null ? cabang.getNama() : "-")
                .kotaCabang(cabang != null ? cabang.getKota() : "-")

                // 6. Review Marketing Terakhir
                .marketingReviewerId(latestMarketing != null ? latestMarketing.getReviewerId() : null)
                .namaMarketingReviewer(latestMarketing != null ? latestMarketing.getNamaReviewer() : "-")
                .hasilReviewMarketing(latestMarketing != null ? latestMarketing.getHasilReview() : "-")
                .catatanMarketing(latestMarketing != null ? latestMarketing.getCatatan() : "-")
                .tanggalReviewMarketing(latestMarketing != null ? latestMarketing.getTanggalReview() : null)

                // 7. Riwayat
                .reviewMarketingHistory(reviewMarketingHistory)
                .persetujuanHistory(persetujuanHistory)
                .build();
    }

    @Transactional
    public PersetujuanPinjamanResponse persetujuan(
            UUID pengajuanId, UUID karyawanId, PersetujuanPinjamanRequest request) {
        PengajuanPinjaman pengajuan = pengajuanRepository.findById(pengajuanId)
                .orElseThrow(() -> new BussinessRuleException("Pengajuan pinjaman tidak ditemukan"));

        Karyawan karyawan = karyawanRepository.findById(karyawanId)
                .orElseThrow(() -> new BussinessRuleException("Data Karyawan Branch Manager tidak ditemukan"));

        String currentStatus = pengajuan.getStatusPengajuan() != null ? pengajuan.getStatusPengajuan() : "";

        // Validasi: Pengajuan harus sudah disetujui oleh Marketing (SELESAI_DIREVIEW) atau sudah dalam status persetujuan
        if (!"SELESAI_DIREVIEW".equalsIgnoreCase(currentStatus)
                && !"PENGAJUAN_DISETUJUI".equalsIgnoreCase(currentStatus)
                && !"APPROVED".equalsIgnoreCase(currentStatus)) {
            throw new BussinessRuleException("Pengajuan pinjaman belum selesai direview oleh tim Marketing (Status saat ini: " + currentStatus + ")");
        }

        String rawInput = request.getHasilPersetujuan().trim().toUpperCase();
        String hasilPersetujuan;
        String newStatusPengajuan;

        if (rawInput.contains("SETUJU") || rawInput.contains("APPROV")) {
            hasilPersetujuan = "DISETUJUI";
            newStatusPengajuan = "PENGAJUAN_DISETUJUI";
        } else {
            hasilPersetujuan = "DITOLAK";
            newStatusPengajuan = "PENGAJUAN_DITOLAK";
        }

        Persetujuan persetujuan = new Persetujuan();
        persetujuan.setId(UUID.randomUUID());
        persetujuan.setHasilPersetujuan(hasilPersetujuan);
        persetujuan.setCatatan(request.getCatatan());
        persetujuan.setCreatedDate(LocalDateTime.now());
        persetujuan.setUpdatedDate(LocalDateTime.now());
        persetujuan.setMstKaryawanId(karyawanId);
        persetujuan.setTrxPengajuanPinjamanId(pengajuanId);

        Persetujuan savedPersetujuan = persetujuanRepository.save(persetujuan);

        // Update status di trx_pengajuan_pinjaman
        pengajuan.setStatusPengajuan(newStatusPengajuan);
        pengajuan.setCatatanReview(request.getCatatan());
        pengajuan.setUpdatedDate(LocalDateTime.now());
        pengajuanRepository.save(pengajuan);

        return PersetujuanPinjamanResponse.builder()
                .id(savedPersetujuan.getId())
                .pengajuanId(pengajuanId)
                .nomorPengajuan(pengajuan.getNomorPengajuan())
                .hasilPersetujuan(hasilPersetujuan)
                .catatan(request.getCatatan())
                .approverId(karyawanId)
                .namaApprover(karyawan.getNama())
                .tanggalPersetujuan(savedPersetujuan.getCreatedDate())
                .statusPengajuan(newStatusPengajuan)
                .build();
    }

    private String mapStatusTampilanBM(String rawStatus, Optional<Persetujuan> latestApproval) {
        if ("PENGAJUAN_DISETUJUI".equalsIgnoreCase(rawStatus) || "APPROVED".equalsIgnoreCase(rawStatus)) {
            return "PENGAJUAN_DISETUJUI";
        }
        if ("PENGAJUAN_DITOLAK".equalsIgnoreCase(rawStatus) || "DITOLAK".equalsIgnoreCase(rawStatus)) {
            return "PENGAJUAN_DITOLAK";
        }
        if ("SELESAI_DIREVIEW".equalsIgnoreCase(rawStatus)) {
            return "MENUNGGU_PERSETUJUAN";
        }
        return "MENUNGGU_PERSETUJUAN";
    }

    private AlamatDetailResponse mapAlamat(AlamatCustomer alamat) {
        if (alamat == null) {
            return null;
        }

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
}
