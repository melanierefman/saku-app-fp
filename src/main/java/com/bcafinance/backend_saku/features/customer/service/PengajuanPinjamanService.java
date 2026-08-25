package com.bcafinance.backend_saku.features.customer.service;

import com.bcafinance.backend_saku.core.dto.AngsuranItemResponse;
import com.bcafinance.backend_saku.core.dto.DokumenPinjamanResponse;
import com.bcafinance.backend_saku.features.customer.dto.PengajuanPinjamanRequest;
import com.bcafinance.backend_saku.features.customer.dto.PengajuanPinjamanResponse;
import com.bcafinance.backend_saku.features.customer.dto.PengajuanStepResponse;
import com.bcafinance.backend_saku.core.entity.AlamatCustomer;
import com.bcafinance.backend_saku.core.entity.Angsuran;
import com.bcafinance.backend_saku.core.entity.Cabang;
import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.entity.DokumenPinjaman;
import com.bcafinance.backend_saku.core.entity.PengajuanPinjaman;
import com.bcafinance.backend_saku.core.entity.Plafond;
import com.bcafinance.backend_saku.core.entity.ScoringCustomer;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.AlamatCustomerRepository;
import com.bcafinance.backend_saku.core.repository.AngsuranRepository;
import com.bcafinance.backend_saku.core.repository.CabangRepository;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.repository.DokumenPinjamanRepository;
import com.bcafinance.backend_saku.core.repository.PengajuanPinjamanRepository;
import com.bcafinance.backend_saku.core.repository.PlafondRepository;
import com.bcafinance.backend_saku.core.repository.ScoringCustomerRepository;
import com.bcafinance.backend_saku.core.storage.FileStorageService;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PengajuanPinjamanService {

    private final PengajuanPinjamanRepository pengajuanRepository;
    private final DokumenPinjamanRepository dokumenPinjamanRepository;
    private final AngsuranRepository angsuranRepository;
    private final CustomerRepository customerRepository;
    private final ScoringCustomerRepository scoringRepository;
    private final PlafondRepository plafondRepository;
    private final CabangRepository cabangRepository;
    private final AlamatCustomerRepository alamatRepository;
    private final FileStorageService fileStorageService;


    @Transactional
    public PengajuanStepResponse step1(UUID customerId, PengajuanPinjamanRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));

        if (!Boolean.TRUE.equals(customer.getStatus())) {
            throw new BussinessRuleException("Akun customer belum aktif atau belum diverifikasi oleh Backoffice");
        }

        ScoringCustomer scoring = scoringRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId)
                .orElseThrow(() -> new BussinessRuleException("Data scoring customer belum tersedia"));

        if (scoring.getSkor() == null || scoring.getSkor() < 60) {
            throw new BussinessRuleException(
                    "Skor kredit customer tidak memenuhi batas minimum untuk pengajuan pinjaman");
        }

        Plafond plafond = resolvePlafond(scoring);

        validateLoanAmount(request.getJumlahPinjaman(), plafond);

        Cabang cabang = resolveCabang(customerId, request.getMstBranchId());

        String nomorPengajuan = generateNomorPengajuan();

        PengajuanPinjaman pengajuan = new PengajuanPinjaman();
        pengajuan.setId(UUID.randomUUID());
        pengajuan.setNomorPengajuan(nomorPengajuan);
        pengajuan.setJumlahPinjaman(request.getJumlahPinjaman());
        pengajuan.setTenorBulan(request.getTenorBulan());
        pengajuan.setTujuanPinjaman(request.getTujuanPinjaman());
        pengajuan.setBunga(plafond.getBunga());
        pengajuan.setBiayaAdmin(plafond.getBiayaAdmin());
        pengajuan.setSkorKesehatan(scoring.getSkor());
        pengajuan.setStatusPengajuan("MENUNGGU_DOKUMEN");
        pengajuan.setCatatanReview("Data rincian pinjaman tersimpan, silakan unggah dokumen pendukung");
        pengajuan.setCreatedDate(LocalDateTime.now());
        pengajuan.setUpdatedDate(LocalDateTime.now());
        pengajuan.setMstCustomerId(customerId);
        pengajuan.setMstBranchId(cabang.getId());
        pengajuan.setTrxScoringCustomerId(scoring.getId());

        PengajuanPinjaman saved = pengajuanRepository.save(pengajuan);
        PengajuanPinjamanResponse detail = toResponse(saved, customer.getNama(), cabang, List.of());

        return new PengajuanStepResponse(
                saved.getId(),
                saved.getNomorPengajuan(),
                1,
                "Rincian pengajuan pinjaman berhasil disimpan. Silakan lanjutkan ke Step 2 untuk mengunggah dokumen pendukung.",
                detail);
    }

    @Transactional
    public PengajuanStepResponse step2(
            UUID customerId,
            UUID pengajuanId,
            MultipartFile slipGaji,
            MultipartFile rekeningKoran,
            MultipartFile npwp) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));

        PengajuanPinjaman pengajuan = pengajuanRepository.findByIdAndMstCustomerId(pengajuanId, customerId)
                .orElseThrow(() -> new BussinessRuleException(
                        "Data pengajuan pinjaman tidak ditemukan atau bukan milik Anda"));

        String currentStatus = pengajuan.getStatusPengajuan() != null ? pengajuan.getStatusPengajuan() : "";

        if ("DISETUJUI".equalsIgnoreCase(currentStatus)
                || "SELESAI_DIREVIEW".equalsIgnoreCase(currentStatus)
                || "APPROVED".equalsIgnoreCase(currentStatus)
                || "PENGAJUAN_DITOLAK".equalsIgnoreCase(currentStatus)
                || "DITOLAK".equalsIgnoreCase(currentStatus)
                || "REJECTED".equalsIgnoreCase(currentStatus)) {
            throw new BussinessRuleException(
                    "Pengajuan pinjaman sudah diproses (" + currentStatus + ") dan dokumen tidak dapat diubah.");
        }

        boolean isRevisi = "PERLU_REVISI".equalsIgnoreCase(currentStatus);

        boolean hasSlipGaji = slipGaji != null && !slipGaji.isEmpty();
        boolean hasRekeningKoran = rekeningKoran != null && !rekeningKoran.isEmpty();
        boolean hasNpwp = npwp != null && !npwp.isEmpty();

        // Validasi jika pengajuan awal (bukan revisi)
        if (!isRevisi) {
            if (!hasSlipGaji) {
                throw new BussinessRuleException("Dokumen Slip Gaji wajib diunggah");
            }
            if (!hasRekeningKoran) {
                throw new BussinessRuleException("Dokumen Rekening Koran wajib diunggah");
            }
        } else {
            // Jika dalam status revisi, minimal harus mengunggah 1 dokumen perbaikan
            if (!hasSlipGaji && !hasRekeningKoran && !hasNpwp) {
                throw new BussinessRuleException("Silakan pilih minimal satu file dokumen perbaikan untuk diunggah");
            }
        }

        String directory = "pinjaman/" + pengajuanId;

        // 1. Simpan/Update Slip Gaji jika diunggah
        if (hasSlipGaji) {
            String slipGajiUrl = fileStorageService.store(slipGaji, directory);
            saveOrUpdateDokumen(pengajuanId, "SLIP_GAJI", slipGajiUrl);
        }

        // 2. Simpan/Update Rekening Koran jika diunggah
        if (hasRekeningKoran) {
            String rekKoranUrl = fileStorageService.store(rekeningKoran, directory);
            saveOrUpdateDokumen(pengajuanId, "REKENING_KORAN", rekKoranUrl);
        }

        // 3. Simpan/Update NPWP jika diunggah
        if (hasNpwp) {
            String npwpUrl = fileStorageService.store(npwp, directory);
            saveOrUpdateDokumen(pengajuanId, "NPWP", npwpUrl);
        }

        // Ambil semua dokumen terkini (gabungan yang baru dan yang sudah ada
        // sebelumnya)
        List<DokumenPinjamanResponse> allUploadedDocs = dokumenPinjamanRepository
                .findAllByTrxPengajuanPinjamanId(pengajuanId)
                .stream()
                .map(this::toDokumenResponse)
                .toList();

        // Update status pengajuan menjadi PENDING untuk direview ulang
        String message;
        if (isRevisi) {
            pengajuan.setStatusPengajuan("PENDING");
            pengajuan.setCatatanReview(
                    "Dokumen perbaikan telah diunggah oleh nasabah, menunggu review ulang oleh Marketing");
            message = "Dokumen perbaikan berhasil diunggah. Pengajuan pinjaman kembali masuk ke antrean review Marketing.";
        } else {
            pengajuan.setStatusPengajuan("PENDING");
            pengajuan.setCatatanReview("Dokumen pendukung berhasil diunggah, menunggu proses review oleh tim cabang");
            message = "Dokumen pendukung berhasil diunggah. Pengajuan pinjaman selesai dan sedang dalam proses review.";
        }

        pengajuan.setUpdatedDate(LocalDateTime.now());
        PengajuanPinjaman saved = pengajuanRepository.save(pengajuan);

        Cabang cabang = cabangRepository.findById(saved.getMstBranchId()).orElse(null);
        PengajuanPinjamanResponse detail = toResponse(saved, customer.getNama(), cabang, allUploadedDocs);

        return new PengajuanStepResponse(
                saved.getId(),
                saved.getNomorPengajuan(),
                2,
                message,
                detail);
    }

    public List<PengajuanPinjamanResponse> findMyLoans(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));

        return pengajuanRepository.findAllByMstCustomerIdOrderByCreatedDateDesc(customerId)
                .stream()
                .map(pengajuan -> {
                    Cabang cabang = cabangRepository.findById(pengajuan.getMstBranchId()).orElse(null);
                    List<DokumenPinjamanResponse> docs = dokumenPinjamanRepository
                            .findAllByTrxPengajuanPinjamanId(pengajuan.getId())
                            .stream()
                            .map(this::toDokumenResponse)
                            .toList();
                    return toResponse(pengajuan, customer.getNama(), cabang, docs);
                })
                .toList();
    }

    public PengajuanPinjamanResponse findById(UUID id, UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));

        PengajuanPinjaman pengajuan = pengajuanRepository.findByIdAndMstCustomerId(id, customerId)
                .orElseThrow(() -> new BussinessRuleException("Data pengajuan pinjaman tidak ditemukan"));

        Cabang cabang = cabangRepository.findById(pengajuan.getMstBranchId()).orElse(null);
        List<DokumenPinjamanResponse> docs = dokumenPinjamanRepository
                .findAllByTrxPengajuanPinjamanId(pengajuan.getId())
                .stream()
                .map(this::toDokumenResponse)
                .toList();

        return toResponse(pengajuan, customer.getNama(), cabang, docs);
    }

    private DokumenPinjaman saveOrUpdateDokumen(UUID pengajuanId, String docType, String fileUrl) {
        DokumenPinjaman dokumen = dokumenPinjamanRepository
                .findByTrxPengajuanPinjamanIdAndDocType(pengajuanId, docType)
                .orElseGet(() -> {
                    DokumenPinjaman d = new DokumenPinjaman();
                    d.setId(UUID.randomUUID());
                    d.setCreatedDate(LocalDateTime.now());
                    d.setTrxPengajuanPinjamanId(pengajuanId);
                    d.setDocType(docType);
                    return d;
                });

        dokumen.setFileUrl(fileUrl);
        dokumen.setUpdatedDate(LocalDateTime.now());
        return dokumenPinjamanRepository.save(dokumen);
    }

    private Plafond resolvePlafond(ScoringCustomer scoring) {
        if (scoring.getMstPlafondId() != null) {
            Optional<Plafond> opt = plafondRepository.findById(scoring.getMstPlafondId());
            if (opt.isPresent()) {
                return opt.get();
            }
        }

        return plafondRepository
                .findTopByMinPendapatanLessThanEqualAndStatusTrueOrderByMinPendapatanDesc(
                        scoring.getPenghasilanBulanan())
                .orElseThrow(() -> new BussinessRuleException("Customer belum memenuhi batas minimum plafond manapun"));
    }

    private void validateLoanAmount(BigDecimal jumlahPinjaman, Plafond plafond) {
        if (plafond.getMinPlafond() != null && jumlahPinjaman.compareTo(plafond.getMinPlafond()) < 0) {
            throw new BussinessRuleException(
                    "Jumlah pinjaman kurang dari batas minimum plafond: Rp " + plafond.getMinPlafond());
        }

        BigDecimal maxLimit = plafond.getPlafondMaksimal() != null ? plafond.getPlafondMaksimal()
                : plafond.getMaxPlafond();
        if (maxLimit != null && jumlahPinjaman.compareTo(maxLimit) > 0) {
            throw new BussinessRuleException(
                    "Jumlah pinjaman melebihi batas maksimum plafond yang disetujui: Rp " + maxLimit);
        }
    }

    private Cabang resolveCabang(UUID customerId, UUID branchId) {
        if (branchId != null) {
            return cabangRepository.findById(branchId)
                    .filter(Cabang::getStatus)
                    .orElseThrow(() -> new BussinessRuleException("Cabang yang dipilih tidak valid atau tidak aktif"));
        }

        Optional<AlamatCustomer> domisiliOpt = alamatRepository.findByCustomer_IdAndJenisAlamat(customerId, "DOMISILI");
        if (domisiliOpt.isPresent() && domisiliOpt.get().getKotaKabupaten() != null) {
            Optional<Cabang> cabangByDomisili = findMatchingCabangByKota(domisiliOpt.get().getKotaKabupaten());
            if (cabangByDomisili.isPresent()) {
                return cabangByDomisili.get();
            }
        }

        Optional<AlamatCustomer> ktpOpt = alamatRepository.findByCustomer_IdAndJenisAlamat(customerId, "KTP");
        if (ktpOpt.isPresent() && ktpOpt.get().getKotaKabupaten() != null) {
            Optional<Cabang> cabangByKtp = findMatchingCabangByKota(ktpOpt.get().getKotaKabupaten());
            if (cabangByKtp.isPresent()) {
                return cabangByKtp.get();
            }
        }

        return cabangRepository.findFirstByIsDefaultTrueAndStatusTrue()
                .or(cabangRepository::findFirstByStatusTrue)
                .orElseThrow(() -> new BussinessRuleException("Belum ada data cabang operasional yang aktif"));
    }

    private Optional<Cabang> findMatchingCabangByKota(String rawKota) {
        if (rawKota == null || rawKota.isBlank()) {
            return Optional.empty();
        }

        String cleanedKota = rawKota.trim()
                .replaceAll("(?i)^(Kota|Kabupaten|Kab\\.?)\\s+", "")
                .trim();

        List<Cabang> exactMatch = cabangRepository.findAllByKotaIgnoreCaseAndStatusTrue(rawKota.trim());
        if (!exactMatch.isEmpty()) {
            return Optional.of(exactMatch.get(0));
        }

        if (!cleanedKota.equalsIgnoreCase(rawKota.trim())) {
            List<Cabang> cleanedExact = cabangRepository.findAllByKotaIgnoreCaseAndStatusTrue(cleanedKota);
            if (!cleanedExact.isEmpty()) {
                return Optional.of(cleanedExact.get(0));
            }
        }

        List<Cabang> containingMatch = cabangRepository.findAllByKotaContainingIgnoreCaseAndStatusTrue(cleanedKota);
        if (!containingMatch.isEmpty()) {
            return Optional.of(containingMatch.get(0));
        }

        return Optional.empty();
    }

    private String generateNomorPengajuan() {
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String candidate = "PJ-" + datePrefix + "-" + randomSuffix;

        while (pengajuanRepository.existsByNomorPengajuan(candidate)) {
            randomSuffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            candidate = "PJ-" + datePrefix + "-" + randomSuffix;
        }

        return candidate;
    }

    private BigDecimal calculateEstimasiAngsuran(BigDecimal jumlahPinjaman, Integer tenorBulan,
            BigDecimal bungaTahunan) {
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

    private DokumenPinjamanResponse toDokumenResponse(DokumenPinjaman d) {
        if (d == null) {
            return null;
        }
        return DokumenPinjamanResponse.builder()
                .id(d.getId())
                .docType(d.getDocType())
                .fileUrl(d.getFileUrl())
                .createdDate(d.getCreatedDate())
                .build();
    }

    public List<AngsuranItemResponse> getJadwalAngsuran(UUID pengajuanId, UUID customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new BussinessRuleException("Customer tidak ditemukan");
        }

        PengajuanPinjaman pengajuan = pengajuanRepository.findByIdAndMstCustomerId(pengajuanId, customerId)
                .orElseThrow(() -> new BussinessRuleException("Data pengajuan pinjaman tidak ditemukan atau bukan milik Anda"));

        return angsuranRepository.findAllByTrxPengajuanPinjamanIdOrderByCicilanKeAsc(pengajuan.getId())
                .stream()
                .map(this::toAngsuranResponse)
                .toList();
    }


    private AngsuranItemResponse toAngsuranResponse(Angsuran a) {
        if (a == null) {
            return null;
        }
        return AngsuranItemResponse.builder()
                .id(a.getId())
                .cicilanKe(a.getCicilanKe())
                .jumlahAngsuran(a.getJumlahAngsuran())
                .jatuhTempo(a.getJatuhTempo())
                .statusBayar(a.getStatusBayar())
                .build();
    }

    private PengajuanPinjamanResponse toResponse(
            PengajuanPinjaman p,
            String namaCustomer,
            Cabang cabang,
            List<DokumenPinjamanResponse> dokumenList) {
        BigDecimal estimasiCicilan = calculateEstimasiAngsuran(p.getJumlahPinjaman(), p.getTenorBulan(), p.getBunga());

        List<AngsuranItemResponse> listAngsuran = angsuranRepository
                .findAllByTrxPengajuanPinjamanIdOrderByCicilanKeAsc(p.getId())
                .stream()
                .map(this::toAngsuranResponse)
                .toList();

        return PengajuanPinjamanResponse.builder()
                .id(p.getId())
                .nomorPengajuan(p.getNomorPengajuan())
                .customerId(p.getMstCustomerId())
                .namaCustomer(namaCustomer)
                .branchId(p.getMstBranchId())
                .namaCabang(cabang != null ? cabang.getNama() : "-")
                .kotaCabang(cabang != null ? cabang.getKota() : "-")
                .jumlahPinjaman(p.getJumlahPinjaman())
                .tenorBulan(p.getTenorBulan())
                .tujuanPinjaman(p.getTujuanPinjaman())
                .bunga(p.getBunga())
                .biayaAdmin(p.getBiayaAdmin())
                .estimasiAngsuranBulanan(estimasiCicilan)
                .skorKesehatan(p.getSkorKesehatan())
                .statusPengajuan(p.getStatusPengajuan())
                .catatanReview(p.getCatatanReview())
                .createdDate(p.getCreatedDate())
                .updatedDate(p.getUpdatedDate())
                .dokumenList(dokumenList)
                .listAngsuran(listAngsuran)
                .build();
    }
}

