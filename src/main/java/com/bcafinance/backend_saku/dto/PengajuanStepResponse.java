package com.bcafinance.backend_saku.dto;

import java.util.UUID;

public record PengajuanStepResponse(
        UUID pengajuanId,
        String nomorPengajuan,
        int step,
        String message,
        PengajuanPinjamanResponse data) {
}
