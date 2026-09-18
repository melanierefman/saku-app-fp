package com.example.saku.app.core.util

/**
 * Utilitas untuk memformat enum dan konstanta backend menjadi label Bahasa Indonesia yang ramah pengguna.
 */
object EnumLabelUtils {

    /**
     * Format status pekerjaan / kepegawaian nasabah
     */
    fun formatStatusPekerjaan(status: String?): String {
        if (status.isNullOrBlank()) return "-"
        return when (status.trim().uppercase()) {
            "KARYAWAN_TETAP" -> "Karyawan Tetap"
            "KARYAWAN_KONTRAK" -> "Karyawan Kontrak"
            "WIRAUSAHA", "PENGUSAHA", "WIRASWASTA" -> "Wiraswasta / Pengusaha"
            "PROFESIONAL" -> "Profesional"
            "PNS", "PNS_BUMN", "PEGAWAI_NEGERI" -> "PNS / Pegawai BUMN"
            "IBU_RUMAH_TANGGA" -> "Ibu Rumah Tangga"
            "LAINNYA" -> "Lainnya"
            else -> status.replace("_", " ")
                .lowercase()
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
        }
    }

    /**
     * Format status pengajuan pinjaman
     */
    fun formatStatusPengajuan(status: String?): String {
        if (status.isNullOrBlank()) return "Dalam Proses"
        return when (status.trim().uppercase()) {
            "DIAJUKAN" -> "Diajukan"
            "PENDING", "MENUNGGU_REVIEW" -> "Menunggu Review"
            "SELESAI_DIREVIEW", "DISETUJUI_MARKETING" -> "Disetujui Marketing"
            "MENUNGGU_PERSETUJUAN", "MENUNGGU_BM" -> "Menunggu Persetujuan BM"
            "PENGAJUAN_DISETUJUI", "DISETUJUI_BM", "DISETUJUI", "APPROVED" -> "Disetujui BM"
            "MENUNGGU_PENCAIRAN", "READY_TO_DISBURSE" -> "Siap Dicairkan"
            "DICAIRKAN", "DISBURSED", "SUCCESS", "BERHASIL" -> "Sudah Dicairkan"
            "PENGAJUAN_DITOLAK", "DITOLAK_BM", "DITOLAK_MARKETING", "DITOLAK", "REJECTED" -> "Ditolak"
            "PERLU_REVISI", "DOKUMEN_DIREVISI" -> "Perlu Revisi Dokumen"
            "LUNAS", "PAID" -> "Lunas"
            else -> status.replace("_", " ")
                .lowercase()
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
        }
    }

    /**
     * Format tipe dokumen pinjaman / KYC
     */
    fun formatDocType(type: String?): String {
        if (type.isNullOrBlank()) return "-"
        return when (type.trim().uppercase()) {
            "KTP" -> "Foto e-KTP"
            "SELFIE" -> "Foto Selfie"
            "SLIP_GAJI" -> "Slip Gaji"
            "REKENING_KORAN" -> "Rekening Koran"
            "NPWP" -> "NPWP"
            else -> type.replace("_", " ")
        }
    }

    /**
     * Format status verifikasi KYC Customer
     */
    fun formatStatusKyc(status: String?): String {
        if (status.isNullOrBlank()) return "Menunggu Verifikasi"
        return when (status.trim().uppercase()) {
            "APPROVED", "VERIFIED", "TERVERIFIKASI", "TRUE" -> "Terverifikasi"
            "REJECTED", "DITOLAK" -> "Ditolak"
            "PERLU_REVISI", "REVISION_REQUIRED" -> "Perlu Revisi Dokumen"
            "PENDING", "MENUNGGU_VERIFIKASI", "FALSE" -> "Menunggu Verifikasi"
            else -> status
        }
    }
}
