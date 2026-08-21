package com.bcafinance.backend_saku.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trx_dokumen_pinjaman")
@Getter
@Setter
@NoArgsConstructor
public class DokumenPinjaman {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Size(max = 20)
    @Column(name = "doc_type", nullable = false, length = 20)
    private String docType;

    @NotNull
    @Size(max = 100)
    @Column(name = "file_url", nullable = false, length = 100)
    private String fileUrl;

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