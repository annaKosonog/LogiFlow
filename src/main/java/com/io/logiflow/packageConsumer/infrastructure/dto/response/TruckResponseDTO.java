package com.io.logiflow.packageConsumer.infrastructure.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record TruckResponseDTO(
        UUID uuid,
        String licensePlate,
        DimensionsDTO dimensions,
        BigDecimal maxPayload
) {
}
