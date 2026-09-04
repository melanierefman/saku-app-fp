package com.bcafinance.backend_saku.core.util;

import com.bcafinance.backend_saku.core.entity.Pencairan;
import com.bcafinance.backend_saku.core.entity.PengajuanPinjaman;
import com.bcafinance.backend_saku.core.entity.Persetujuan;
import com.bcafinance.backend_saku.core.entity.ReviewPengajuan;
import java.util.Optional;

public final class LoanStatusHelper {

    private LoanStatusHelper() {}

    public static final String MENUNGGU_REVIEW_MARKETING = "MENUNGGU_REVIEW_MARKETING";
    public static final String PERLU_REVISI = "PERLU_REVISI";
    public static final String DOKUMEN_DIREVISI = "DOKUMEN_DIREVISI";
    public static final String MENUNGGU_PERSETUJUAN_BM = "MENUNGGU_PERSETUJUAN_BM";
    public static final String MENUNGGU_PENCAIRAN = "MENUNGGU_PENCAIRAN";
    public static final String DICAIRKAN = "DICAIRKAN";
    public static final String DITOLAK_MARKETING = "DITOLAK_MARKETING";
    public static final String DITOLAK_BM = "DITOLAK_BM";

    public static String determineGlobalStatus(
            PengajuanPinjaman p,
            Optional<ReviewPengajuan> latestReview,
            Optional<Persetujuan> latestApproval,
            Optional<Pencairan> latestPencairan) {

        if (p == null) {
            return MENUNGGU_REVIEW_MARKETING;
        }

        // 1. Cek Tahap Pencairan Backoffice
        if (latestPencairan != null && latestPencairan.isPresent()) {
            Pencairan pc = latestPencairan.get();
            if ("DICAIRKAN".equalsIgnoreCase(pc.getStatusPencairan()) || "DISBURSED".equalsIgnoreCase(pc.getStatusPencairan())) {
                return DICAIRKAN;
            }
        }
        if ("DICAIRKAN".equalsIgnoreCase(p.getStatusPengajuan()) || "DISBURSED".equalsIgnoreCase(p.getStatusPengajuan())) {
            return DICAIRKAN;
        }

        // 2. Cek Tahap Persetujuan Branch Manager
        if (latestApproval != null && latestApproval.isPresent()) {
            Persetujuan ap = latestApproval.get();
            if ("DISETUJUI".equalsIgnoreCase(ap.getHasilPersetujuan()) || "APPROVE".equalsIgnoreCase(ap.getHasilPersetujuan()) || "PENGAJUAN_DISETUJUI".equalsIgnoreCase(ap.getHasilPersetujuan())) {
                return MENUNGGU_PENCAIRAN;
            } else if ("DITOLAK".equalsIgnoreCase(ap.getHasilPersetujuan()) || "REJECT".equalsIgnoreCase(ap.getHasilPersetujuan()) || "PENGAJUAN_DITOLAK".equalsIgnoreCase(ap.getHasilPersetujuan())) {
                return DITOLAK_BM;
            }
        }
        if ("PENGAJUAN_DISETUJUI".equalsIgnoreCase(p.getStatusPengajuan())) {
            return MENUNGGU_PENCAIRAN;
        }

        // 3. Cek Tahap Review Marketing
        if (latestReview != null && latestReview.isPresent()) {
            ReviewPengajuan rev = latestReview.get();
            if ("DISETUJUI".equalsIgnoreCase(rev.getHasilReview()) || "SELESAI_DIREVIEW".equalsIgnoreCase(rev.getHasilReview()) || "APPROVE".equalsIgnoreCase(rev.getHasilReview())) {
                return MENUNGGU_PERSETUJUAN_BM;
            } else if ("PERLU_REVISI".equalsIgnoreCase(rev.getHasilReview())) {
                return "PENDING".equalsIgnoreCase(p.getStatusPengajuan()) ? DOKUMEN_DIREVISI : PERLU_REVISI;
            } else if ("DITOLAK".equalsIgnoreCase(rev.getHasilReview()) || "PENGAJUAN_DITOLAK".equalsIgnoreCase(rev.getHasilReview()) || "REJECT".equalsIgnoreCase(rev.getHasilReview())) {
                return DITOLAK_MARKETING;
            }
        }
        if ("SELESAI_DIREVIEW".equalsIgnoreCase(p.getStatusPengajuan())) {
            return MENUNGGU_PERSETUJUAN_BM;
        }
        if ("PERLU_REVISI".equalsIgnoreCase(p.getStatusPengajuan())) {
            return PERLU_REVISI;
        }
        if ("PENGAJUAN_DITOLAK".equalsIgnoreCase(p.getStatusPengajuan())) {
            return DITOLAK_MARKETING;
        }

        // 4. Default Baru Diajukan
        return MENUNGGU_REVIEW_MARKETING;
    }

    public static String getStatusLabel(String globalStatus) {
        if (globalStatus == null) {
            return "Menunggu Review Marketing";
        }
        return switch (globalStatus.toUpperCase()) {
            case MENUNGGU_REVIEW_MARKETING, "MENUNGGU_REVIEW", "PENDING" -> "Menunggu Review Marketing";
            case PERLU_REVISI -> "Perlu Revisi Dokumen";
            case DOKUMEN_DIREVISI -> "Revisi Diajukan";
            case MENUNGGU_PERSETUJUAN_BM, "MENUNGGU_PERSETUJUAN" -> "Menunggu Persetujuan BM";
            case MENUNGGU_PENCAIRAN -> "Menunggu Pencairan";
            case DICAIRKAN, "DISBURSED", "SELESAI" -> "Dana Dicairkan";
            case DITOLAK_MARKETING -> "Ditolak Marketing";
            case DITOLAK_BM -> "Ditolak Branch Manager";
            case "DITOLAK" -> "Pengajuan Ditolak";
            default -> globalStatus.replace("_", " ");
        };
    }
}
