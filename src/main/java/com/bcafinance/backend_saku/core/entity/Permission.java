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
@Table(name = "mst_permission")
@Getter
@Setter
@NoArgsConstructor
public class Permission {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Size(max = 50)
    @Column(name = "nama", nullable = false, length = 50)
    private String nama;

    @NotNull
    @Size(max = 50)
    @Column(name = "resource", nullable = false, length = 50)
    private String resource;

    @NotNull
    @Size(max = 50)
    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @NotNull
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @NotNull
    @Column(name = "mst_menu_id", nullable = false)
    private UUID mstMenuId;
}