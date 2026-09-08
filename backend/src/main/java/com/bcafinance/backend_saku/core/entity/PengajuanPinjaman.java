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
@Table(name = "trx_pengajuan_pinjaman")
@Getter
@Setter
@NoArgsConstructor
public class PengajuanPinjaman {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Size(max = 100)
    @Column(name = "nomor_pengajuan", nullable = false, unique = true, length = 100)
    private String nomorPengajuan;

    @NotNull
    @Digits(integer = 13, fraction = 2)
    @Column(name = "jumlah_pinjaman", nullable = false, precision = 15, scale = 2)
    private BigDecimal jumlahPinjaman;

    @NotNull
    @Column(name = "tenor_bulan", nullable = false)
    private Integer tenorBulan;

    @NotNull
    @Size(max = 50)
    @Column(name = "tujuan_pinjaman", nullable = false, length = 50)
    private String tujuanPinjaman;

    @NotNull
    @Digits(integer = 1, fraction = 2)
    @Column(name = "bunga", nullable = false, precision = 3, scale = 2)
    private BigDecimal bunga;

    @NotNull
    @Digits(integer = 13, fraction = 2)
    @Column(name = "biaya_admin", nullable = false, precision = 15, scale = 2)
    private BigDecimal biayaAdmin;

    @NotNull
    @Column(name = "skor_kesehatan", nullable = false)
    private Integer skorKesehatan;

    @NotNull
    @Size(max = 20)
    @Column(name = "status_pengajuan", nullable = false, length = 20)
    private String statusPengajuan;

    @NotNull
    @Size(max = 1000)
    @Column(name = "catatan_review", nullable = false, length = 1000)
    private String catatanReview;


    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @NotNull
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @NotNull
    @Column(name = "mst_branch_id", nullable = false)
    private UUID mstBranchId;

    @NotNull
    @Column(name = "mst_customer_id", nullable = false)
    private UUID mstCustomerId;

    @NotNull
    @Column(name = "trx_scoring_customer_id", nullable = false)
    private UUID trxScoringCustomerId;
}
