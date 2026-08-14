package com.bcafinance.backend_saku.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mst_role_permission")
@Getter
@Setter
@NoArgsConstructor
public class RolePermission {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "mst_permission_id", nullable = false)
    private UUID mstPermissionId;

    @NotNull
    @Column(name = "mst_role_id", nullable = false)
    private UUID mstRoleId;
}