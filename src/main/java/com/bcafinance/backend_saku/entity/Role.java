package com.bcafinance.backend_saku.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mst_role")
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Size(max = 50)
    @Column(name = "nama", nullable = false, length = 50)
    private String nama;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @NotNull
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;
}