package com.bcafinance.backend_saku.features.customer.dto;

import java.util.UUID;

public record RegisterStepResponse(UUID customerId, int step, String message) {
}
