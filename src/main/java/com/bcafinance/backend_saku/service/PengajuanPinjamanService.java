package com.bcafinance.backend_saku.service;

import com.bcafinance.backend_saku.dto.DokumenPinjamanResponse;
import com.bcafinance.backend_saku.dto.PengajuanPinjamanRequest;
import com.bcafinance.backend_saku.dto.PengajuanPinjamanResponse;
import com.bcafinance.backend_saku.dto.PengajuanStepResponse;
import com.bcafinance.backend_saku.entity.AlamatCustomer;
import com.bcafinance.backend_saku.entity.Cabang;
import com.bcafinance.backend_saku.entity.Customer;
import com.bcafinance.backend_saku.entity.DokumenPinjaman;
import com.bcafinance.backend_saku.entity.PengajuanPinjaman;
import com.bcafinance.backend_saku.entity.Plafond;
import com.bcafinance.backend_saku.entity.ScoringCustomer;
import com.bcafinance.backend_saku.exception.BussinessRuleException;
import com.bcafinance.backend_saku.repository.AlamatCustomerRepository;
import com.bcafinance.backend_saku.repository.CabangRepository;
import com.bcafinance.backend_saku.repository.CustomerRepository;
import com.bcafinance.backend_saku.repository.DokumenPinjamanRepository;
import com.bcafinance.backend_saku.repository.PengajuanPinjamanRepository;
import com.bcafinance.backend_saku.repository.PlafondRepository;
import com.bcafinance.backend_saku.repository.ScoringCustomerRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
            throw new BussinessRuleException("Skor kredit customer tidak memenuhi batas minimum untuk pengajuan pinjaman");
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
                .orElseThrow(() -> new BussinessRuleException("Data pengajuan pinjaman tidak ditemukan atau bukan milik Anda"));

        if (slipGaji == null || slipGaji.isEmpty()) {
            throw new BussinessRuleException("Dokumen Slip Gaji wajib diunggah");
        }

        if (rekeningKoran == null || rekeningKoran.isEmpty()) {
            throw new BussinessRuleException("Dokumen Rekening Koran wajib diunggah");
        }

        String directory = "pinjaman/" + pengajuanId;
        List<DokumenPinjamanResponse> uploadedDocs = new ArrayList<>();

        // 1. Simpan Slip Gaji
        String slipGajiUrl = fileStorageService.store(slipGaji, directory);
        DokumenPinjaman docSlipGaji = saveOrUpdateDokumen(pengajuanId, "SLIP_GAJI", slipGajiUrl);
        uploadedDocs.add(toDokumenResponse(docSlipGaji));

        // 2. Simpan Rekening Koran
        String rekKoranUrl = fileStorageService.store(rekeningKoran, directory);
        DokumenPinjaman docRekKoran = saveOrUpdateDokumen(pengajuanId, "REKENING_KORAN", rekKoranUrl);
        uploadedDocs.add(toDokumenResponse(docRekKoran));

        // 3. Simpan NPWP (Opsional)
        if (npwp != null && !npwp.isEmpty()) {
            String npwpUrl = fileStorageService.store(npwp, directory);
            DokumenPinjaman docNpwp = saveOrUpdateDokumen(pengajuanId, "NPWP", npwpUrl);
            uploadedDocs.add(toDokumenResponse(docNpwp));
        }

        // Update status pengajuan menjadi PENDING untuk direview
        pengajuan.setStatusPengajuan("PENDING");
        pengajuan.setCatatanReview("Dokumen pendukung berhasil diunggah, menunggu proses review oleh tim cabang");
        pengajuan.setUpdatedDate(LocalDateTime.now());
        PengajuanPinjaman saved = pengajuanRepository.save(pengajuan);

        Cabang cabang = cabangRepository.findById(saved.getMstBranchId()).orElse(null);
        PengajuanPinjamanResponse detail = toResponse(saved, customer.getNama(), cabang, uploadedDocs);

        return new PengajuanStepResponse(
                saved.getId(),
                saved.getNomorPengajuan(),
                2,
                "Dokumen pendukung berhasil diunggah. Pengajuan pinjaman selesai dan sedang dalam proses review.",
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
                .findTopByMinPendapatanLessThanEqualAndStatusTrueOrderByMinPendapatanDesc(scoring.getPenghasilanBulanan())
                .orElseThrow(() -> new BussinessRuleException("Customer belum memenuhi batas minimum plafond manapun"));
    }

    private void validateLoanAmount(BigDecimal jumlahPinjaman, Plafond plafond) {
        if (plafond.getMinPlafond() != null && jumlahPinjaman.compareTo(plafond.getMinPlafond()) < 0) {
            throw new BussinessRuleException("Jumlah pinjaman kurang dari batas minimum plafond: Rp " + plafond.getMinPlafond());
        }

        BigDecimal maxLimit = plafond.getPlafondMaksimal() != null ? plafond.getPlafondMaksimal() : plafond.getMaxPlafond();
        if (maxLimit != null && jumlahPinjaman.compareTo(maxLimit) > 0) {
            throw new BussinessRuleException("Jumlah pinjaman melebihi batas maksimum plafond yang disetujui: Rp " + maxLimit);
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

    private PengajuanPinjamanResponse toResponse(
            PengajuanPinjaman p,
            String namaCustomer,
            Cabang cabang,
            List<DokumenPinjamanResponse> dokumenList) {
        BigDecimal estimasiCicilan = calculateEstimasiAngsuran(p.getJumlahPinjaman(), p.getTenorBulan(), p.getBunga());

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
                .build();
    }
}
