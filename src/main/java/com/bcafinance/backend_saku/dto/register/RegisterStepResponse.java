package com.bcafinance.backend_saku.dto.register;

import java.util.UUID;

public record RegisterStepResponse(UUID customerId, int step, String message) {
}
