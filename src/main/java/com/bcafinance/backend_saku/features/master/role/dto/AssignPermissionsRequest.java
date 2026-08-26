package com.bcafinance.backend_saku.features.master.role.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignPermissionsRequest {

    @NotNull(message = "List permission IDs tidak boleh null")
    private List<UUID> permissionIds;
}
