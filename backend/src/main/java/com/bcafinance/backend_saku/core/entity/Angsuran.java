package com.bcafinance.backend_saku.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trx_angsuran")
@Getter
@Setter
@NoArgsConstructor
public class Angsuran {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "cicilan_ke", nullable = false)
    private Integer cicilanKe;

    @NotNull
    @Digits(integer = 13, fraction = 2)
    @Column(name = "jumlah_angsuran", nullable = false, precision = 15, scale = 2)
    private BigDecimal jumlahAngsuran;

    @NotNull
    @Column(name = "jatuh_tempo", nullable = false)
    private LocalDate jatuhTempo;

    @NotNull
    @Size(max = 20)
    @Column(name = "status_bayar", nullable = false, length = 20)
    private String statusBayar;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @NotNull
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @NotNull
    @Column(name = "trx_pengajuan_pinjaman_id", nullable = false)
    private UUID trxPengajuanPinjamanId;
}
