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
@Table(name = "trx_audit_log")
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Size(max = 20)
    @Column(name = "action", nullable = false, length = 20)
    private String action;

    @NotNull
    @Size(max = 20)
    @Column(name = "entity", nullable = false, length = 20)
    private String entity;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    private Integer entityId;

    @NotNull
    @Size(max = 225)
    @Column(name = "description", nullable = false, length = 225)
    private String description;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @NotNull
    @Column(name = "mst_karyawan_id", nullable = false)
    private UUID mstKaryawanId;
}