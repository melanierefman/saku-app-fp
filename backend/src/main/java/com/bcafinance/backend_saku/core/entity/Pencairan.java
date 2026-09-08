package com.bcafinance.backend_saku.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trx_pencairan")
@Getter
@Setter
@NoArgsConstructor
public class Pencairan {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Digits(integer = 13, fraction = 2)
    @Column(name = "jumlah_pencairan", nullable = false, precision = 15, scale = 2)
    private BigDecimal jumlahPencairan;

    @NotNull
    @Size(max = 20)
    @Column(name = "status_pencairan", nullable = false, length = 20)
    private String statusPencairan;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @NotNull
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @NotNull
    @Column(name = "trx_pengajuan_pinjaman_id", nullable = false)
    private UUID trxPengajuanPinjamanId;

    @NotNull
    @Column(name = "mst_karyawan_id", nullable = false)
    private UUID mstKaryawanId;
}
