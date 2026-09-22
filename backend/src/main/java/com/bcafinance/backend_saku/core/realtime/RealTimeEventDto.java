package com.bcafinance.backend_saku.core.realtime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealTimeEventDto {
    /**
     * Tipe Event:
     * - KYC_SUBMITTED: Registrasi baru selesai dibuat
     * - KYC_REVISED: Customer mengunggah ulang revisi dokumen KTP/Selfie
     * - LOAN_SUBMITTED: Customer mengajukan pinjaman baru
     * - LOAN_REVISED: Customer mengunggah dokumen revisi pinjaman
     * - LOAN_READY_FOR_BM: Marketing telah mereview dan merekomendasikan pinjaman (siap untuk persetujuan BM)
     * - LOAN_READY_FOR_DISBURSEMENT: BM telah menyetujui pinjaman (siap dicairkan oleh Backoffice)
     * - LOAN_DISBURSED: Backoffice telah mencairkan dana pinjaman
     */
    private String eventType;

    private String referenceId;
    private String title;
    private String message;
    private String customerName;
    private String nomorPengajuan;
    private List<String> targetRoles;
    private LocalDateTime timestamp;
}
