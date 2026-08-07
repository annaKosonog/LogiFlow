package com.io.logiflow.packageConsumer.domain.dto;

import java.util.List;
import java.util.UUID;

public record PackingRequestDTO(
        List<UUID> packageId,
        UUID truckId
) {
}
