package com.bcafinance.backend_saku.features.branchmanager.service;

import com.bcafinance.backend_saku.core.dto.AlamatDetailResponse;
import com.bcafinance.backend_saku.core.dto.CustomerPinjamanHistoryItemResponse;
import com.bcafinance.backend_saku.core.dto.PageResponse;
import com.bcafinance.backend_saku.core.entity.AlamatCustomer;
import com.bcafinance.backend_saku.core.entity.Cabang;
import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.entity.DokumenCustomer;
import com.bcafinance.backend_saku.core.entity.Karyawan;
import com.bcafinance.backend_saku.core.entity.PengajuanPinjaman;
import com.bcafinance.backend_saku.core.entity.ScoringCustomer;
import com.bcafinance.backend_saku.core.entity.VerifikasiCustomer;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.AlamatCustomerRepository;
import com.bcafinance.backend_saku.core.repository.CabangRepository;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.repository.DokumenCustomerRepository;
import com.bcafinance.backend_saku.core.repository.KaryawanRepository;
import com.bcafinance.backend_saku.core.repository.PengajuanPinjamanRepository;
import com.bcafinance.backend_saku.core.repository.ScoringCustomerRepository;
import com.bcafinance.backend_saku.core.repository.VerifikasiCustomerRepository;
import com.bcafinance.backend_saku.features.branchmanager.dto.BranchManagerCustomerDetailResponse;
import com.bcafinance.backend_saku.features.branchmanager.dto.BranchManagerCustomerItemResponse;
import com.bcafinance.backend_saku.features.customer.service.CustomerPlafondService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BranchManagerCustomerService {

    private final CustomerRepository customerRepository;
    private final VerifikasiCustomerRepository verifikasiRepository;
    private final AlamatCustomerRepository alamatRepository;
    private final DokumenCustomerRepository dokumenRepository;
    private final ScoringCustomerRepository scoringRepository;
    private final PengajuanPinjamanRepository pengajuanRepository;
    private final KaryawanRepository karyawanRepository;
    private final CabangRepository cabangRepository;
    private final CustomerPlafondService customerPlafondService;

    public PageResponse<BranchManagerCustomerItemResponse> findAllPaginated(
            int page, int size, String search, String tier, UUID karyawanId) {

        Cabang bmCabang = null;
        String bmBranchName = null;
        if (karyawanId != null) {
            Optional<Karyawan> bmOpt = karyawanRepository.findById(karyawanId);
            if (bmOpt.isPresent() && bmOpt.get().getCabang() != null) {
                bmCabang = bmOpt.get().getCabang();
                bmBranchName = bmCabang.getNama();
            }
        }

        final Cabang filterCabang = bmCabang;
        final String finalBmBranchName = bmBranchName;
        List<Customer> allCustomers = customerRepository.findAllByOrderByCreatedDateDesc();

        List<BranchManagerCustomerItemResponse> verifiedCustomers = allCustomers.stream()
                .filter(this::isRegistrationCompleted)
                .filter(this::isKycApproved)
                .filter(c -> isCustomerInBranch(c, filterCabang))
                .map(c -> mapToItemResponse(c, finalBmBranchName))
                .toList();

        if (tier != null && !tier.trim().isEmpty()) {
            String t = tier.trim().toLowerCase();
            verifiedCustomers = verifiedCustomers.stream().filter(item ->
                    item.getTierPlafond() != null && item.getTierPlafond().toLowerCase().contains(t)
            ).toList();
        }

        if (search != null && !search.trim().isEmpty()) {
            String s = search.trim().toLowerCase();
            verifiedCustomers = verifiedCustomers.stream().filter(item ->
                    (item.getNamaCustomer() != null && item.getNamaCustomer().toLowerCase().contains(s)) ||
                    (item.getNik() != null && item.getNik().toLowerCase().contains(s)) ||
                    (item.getEmail() != null && item.getEmail().toLowerCase().contains(s)) ||
                    (item.getNoHp() != null && item.getNoHp().toLowerCase().contains(s)) ||
                    (item.getKota() != null && item.getKota().toLowerCase().contains(s)) ||
                    (item.getProvinsi() != null && item.getProvinsi().toLowerCase().contains(s)) ||
                    (item.getNamaCabang() != null && item.getNamaCabang().toLowerCase().contains(s))
            ).toList();
        }

        return PageResponse.ofList(verifiedCustomers, page, size);
    }

    private boolean isCustomerInBranch(Customer customer, Cabang branch) {
        if (branch == null) return true;
        UUID branchId = branch.getId();
        String branchKota = branch.getKota() != null ? branch.getKota().trim().toLowerCase() : "";
        String branchNama = branch.getNama() != null ? branch.getNama().trim().toLowerCase() : "";

        // 1. Check if customer has any loan in this branch
        List<PengajuanPinjaman> loans = pengajuanRepository.findAllByMstCustomerIdOrderByCreatedDateDesc(customer.getId());
        boolean hasLoanInBranch = loans.stream().anyMatch(l -> branchId.equals(l.getMstBranchId()));
        if (hasLoanInBranch) return true;

        // If customer already has loan exclusively in another branch, they belong there
        boolean hasLoanInOtherBranch = loans.stream().anyMatch(l -> l.getMstBranchId() != null && !branchId.equals(l.getMstBranchId()));
        if (hasLoanInOtherBranch) {
            return false;
        }

        // 2. Check customer Domisili (or KTP) city against branch
        List<AlamatCustomer> addresses = alamatRepository.findAllByCustomer_Id(customer.getId());
        if (addresses.isEmpty()) return false;

        AlamatCustomer domisili = addresses.stream()
                .filter(a -> "DOMISILI".equalsIgnoreCase(a.getJenisAlamat()))
                .findFirst()
                .orElse(addresses.stream().findFirst().orElse(null));

        String kota = (domisili.getKotaKabupaten() != null ? domisili.getKotaKabupaten() : "").toLowerCase();
        String prov = (domisili.getProvinsi() != null ? domisili.getProvinsi() : "").toLowerCase();
        String alamat = (domisili.getAlamatLengkap() != null ? domisili.getAlamatLengkap() : "").toLowerCase();

        boolean isJawaTimur = prov.contains("jawa timur") || prov.contains("jatim") || kota.contains("surabaya");
        boolean isJawaBarat = prov.contains("jawa barat") || prov.contains("jabar") || kota.contains("bandung");

        // Surabaya branch -> Jawa Timur
        if (branchKota.contains("surabaya") || branchNama.contains("surabaya")) {
            return isJawaTimur;
        }

        // Bandung branch -> Jawa Barat
        if (branchKota.contains("bandung") || branchNama.contains("bandung")) {
            return isJawaBarat;
        }

        // Kantor Pusat (Jakarta) -> Sisanya (semua selain Jawa Timur dan Jawa Barat)
        if (branchKota.contains("jakarta") || branchNama.contains("jakarta") || branchNama.contains("pusat")) {
            return !isJawaTimur && !isJawaBarat;
        }

        // Generic fallback for any other cabang
        if (!branchKota.isEmpty() && (kota.contains(branchKota) || alamat.contains(branchKota))) return true;
        if (!branchNama.isEmpty() && (kota.contains(branchNama) || branchNama.contains(kota))) return true;

        return false;
    }

    public BranchManagerCustomerDetailResponse getDetail(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Nasabah dengan ID " + customerId + " tidak ditemukan"));

        if (!isRegistrationCompleted(customer) || !isKycApproved(customer)) {
            throw new BussinessRuleException("Data nasabah belum terverifikasi oleh Backoffice");
        }

        List<AlamatCustomer> alamatList = alamatRepository.findAllByCustomer_Id(customerId);
        AlamatCustomer alamatKtp = alamatList.stream()
                .filter(a -> "KTP".equalsIgnoreCase(a.getJenisAlamat()))
                .findFirst().orElse(null);
        AlamatCustomer alamatDomisili = alamatList.stream()
                .filter(a -> "DOMISILI".equalsIgnoreCase(a.getJenisAlamat()))
                .findFirst().orElse(null);

        List<DokumenCustomer> dokumenList = dokumenRepository.findAllByCustomer_Id(customerId);
        String fotoKtp = dokumenList.stream()
                .filter(d -> "KTP".equalsIgnoreCase(d.getDocType()))
                .map(DokumenCustomer::getFileUrl)
                .findFirst().orElse(null);
        String fotoSelfie = dokumenList.stream()
                .filter(d -> "SELFIE".equalsIgnoreCase(d.getDocType()))
                .map(DokumenCustomer::getFileUrl)
                .findFirst().orElse(null);

        Optional<ScoringCustomer> scoringOpt = scoringRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId);
        CustomerPlafondService.CustomerPlafondSummary plafondSummary = customerPlafondService.calculatePlafondSummary(customerId);

        Optional<VerifikasiCustomer> verifOpt = verifikasiRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId);
        LocalDateTime tanggalVerifikasi = verifOpt.map(v -> v.getUpdatedDate() != null ? v.getUpdatedDate() : v.getCreatedDate()).orElse(customer.getUpdatedDate());

        List<PengajuanPinjaman> pengajuanList = pengajuanRepository.findAllByMstCustomerIdOrderByCreatedDateDesc(customerId);
        List<CustomerPinjamanHistoryItemResponse> riwayatPinjaman = pengajuanList.stream()
                .map(p -> {
                    String namaCabang = cabangRepository.findById(p.getMstBranchId())
                            .map(Cabang::getNama).orElse("-");
                    return CustomerPinjamanHistoryItemResponse.builder()
                            .pengajuanId(p.getId())
                            .nomorPengajuan(p.getNomorPengajuan())
                            .tanggalPengajuan(p.getCreatedDate())
                            .jumlahPinjaman(p.getJumlahPinjaman())
                            .tenorBulan(p.getTenorBulan())
                            .statusPengajuan(p.getStatusPengajuan())
                            .tujuanPinjaman(p.getTujuanPinjaman())
                            .namaCabang(namaCabang)
                            .build();
                })
                .toList();

        String primaryBranchName = pengajuanList.stream()
                .map(p -> cabangRepository.findById(p.getMstBranchId()).map(Cabang::getNama).orElse(null))
                .filter(name -> name != null && !name.isBlank())
                .findFirst().orElse("Kantor Pusat / All Branches");

        return BranchManagerCustomerDetailResponse.builder()
                .customerId(customer.getId())
                .namaCustomer(customer.getNama())
                .nik(customer.getNik())
                .email(customer.getEmail())
                .noHp(customer.getNoHp())
                .namaIbuKandung(customer.getNamaIbuKandung())
                .namaBank(customer.getNamaBank())
                .noRekening(customer.getNoRekening())
                .namaRekening(customer.getNamaRekening())
                .namaCabang(primaryBranchName)
                .alamatKtp(mapAlamat(alamatKtp))
                .alamatDomisili(mapAlamat(alamatDomisili))
                .fotoKtp(fotoKtp)
                .fotoSelfie(fotoSelfie)
                .pekerjaan(scoringOpt.map(ScoringCustomer::getPekerjaan).orElse("-"))
                .tempatKerja(scoringOpt.map(ScoringCustomer::getTempatKerja).orElse("-"))
                .pendapatan(scoringOpt.map(ScoringCustomer::getPenghasilanBulanan).orElse(null))
                .skor(scoringOpt.map(ScoringCustomer::getSkor).orElse(null))
                .statusScoring(scoringOpt.map(ScoringCustomer::getStatusScoring).orElse(null))
                .tierPlafond(plafondSummary.tierName())
                .totalPlafond(plafondSummary.totalPlafond())
                .usedPlafond(plafondSummary.usedPlafond())
                .availablePlafond(plafondSummary.availablePlafond())
                .statusAkun(customer.getStatus())
                .tanggalVerifikasi(tanggalVerifikasi)
                .createdDate(customer.getCreatedDate())
                .riwayatPinjaman(riwayatPinjaman)
                .build();
    }

    private BranchManagerCustomerItemResponse mapToItemResponse(Customer customer, String fallbackBranchName) {
        List<AlamatCustomer> alamatList = alamatRepository.findAllByCustomer_Id(customer.getId());
        AlamatCustomer primaryAlamat = alamatList.stream()
                .filter(a -> "DOMISILI".equalsIgnoreCase(a.getJenisAlamat()))
                .findFirst()
                .orElseGet(() -> alamatList.stream()
                        .filter(a -> "KTP".equalsIgnoreCase(a.getJenisAlamat()))
                        .findFirst()
                        .orElse(null));

        CustomerPlafondService.CustomerPlafondSummary plafond = customerPlafondService.calculatePlafondSummary(customer.getId());
        List<PengajuanPinjaman> pengajuanList = pengajuanRepository.findAllByMstCustomerIdOrderByCreatedDateDesc(customer.getId());

        String branchName = pengajuanList.stream()
                .map(p -> cabangRepository.findById(p.getMstBranchId()).map(Cabang::getNama).orElse(null))
                .filter(name -> name != null && !name.isBlank())
                .findFirst()
                .orElse(fallbackBranchName != null ? fallbackBranchName : "Kantor Pusat / All");

        Optional<VerifikasiCustomer> verifOpt = verifikasiRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customer.getId());
        LocalDateTime tglVerifikasi = verifOpt.map(v -> v.getUpdatedDate() != null ? v.getUpdatedDate() : v.getCreatedDate()).orElse(customer.getUpdatedDate());

        return BranchManagerCustomerItemResponse.builder()
                .customerId(customer.getId())
                .namaCustomer(customer.getNama())
                .nik(maskNik(customer.getNik()))
                .email(customer.getEmail())
                .noHp(customer.getNoHp())
                .kota(primaryAlamat != null ? primaryAlamat.getKotaKabupaten() : "-")
                .provinsi(primaryAlamat != null ? primaryAlamat.getProvinsi() : "-")
                .namaCabang(branchName)
                .totalPlafond(plafond.totalPlafond())
                .usedPlafond(plafond.usedPlafond())
                .availablePlafond(plafond.availablePlafond())
                .tierPlafond(plafond.tierName())
                .totalPengajuan(pengajuanList.size())
                .statusAkun(customer.getStatus())
                .tanggalVerifikasi(tglVerifikasi)
                .createdDate(customer.getCreatedDate())
                .build();
    }

    private boolean isRegistrationCompleted(Customer customer) {
        if (customer == null) return false;
        if ("PENDING".equalsIgnoreCase(customer.getNik()) ||
            "PENDING".equalsIgnoreCase(customer.getPassword()) ||
            "PENDING".equalsIgnoreCase(customer.getNama()) ||
            "PENDING".equalsIgnoreCase(customer.getNoHp()) ||
            customer.getNik() == null || customer.getNik().isBlank() ||
            customer.getPassword() == null || customer.getPassword().isBlank()) {
            return false;
        }
        return true;
    }

    private boolean isKycApproved(Customer customer) {
        if (customer == null) return false;
        Optional<VerifikasiCustomer> verifOpt = verifikasiRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customer.getId());
        if (verifOpt.isPresent()) {
            String status = verifOpt.get().getStatusVerifikasi();
            return "APPROVED".equalsIgnoreCase(status) || "DISETUJUI".equalsIgnoreCase(status);
        }
        return Boolean.TRUE.equals(customer.getStatus());
    }

    private String maskNik(String nik) {
        if (nik == null || nik.length() < 8) return nik;
        int len = nik.length();
        return nik.substring(0, 4) + "*".repeat(Math.max(0, len - 8)) + nik.substring(len - 4);
    }

    private AlamatDetailResponse mapAlamat(AlamatCustomer alamat) {
        if (alamat == null) return null;

        StringBuilder formatted = new StringBuilder();
        if (alamat.getAlamatLengkap() != null) formatted.append(alamat.getAlamatLengkap());
        if (alamat.getRt() != null || alamat.getRw() != null) {
            formatted.append(", RT ").append(alamat.getRt() != null ? alamat.getRt() : "-")
                    .append("/RW ").append(alamat.getRw() != null ? alamat.getRw() : "-");
        }
        if (alamat.getKelurahan() != null) formatted.append(", Kel. ").append(alamat.getKelurahan());
        if (alamat.getKecamatan() != null) formatted.append(", Kec. ").append(alamat.getKecamatan());
        if (alamat.getKotaKabupaten() != null) formatted.append(", ").append(alamat.getKotaKabupaten());
        if (alamat.getProvinsi() != null) formatted.append(", ").append(alamat.getProvinsi());
        if (alamat.getKodePos() != null) formatted.append(" ").append(alamat.getKodePos());

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
