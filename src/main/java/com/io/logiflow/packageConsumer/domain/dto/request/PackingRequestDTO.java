package com.io.logiflow.packageConsumer.domain.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PackingRequestDTO(
        @NotEmpty(message = "Package list cannot be empty")
        @Size(max = 30)
        List<UUID> packageId,
        @NotNull(message = "Truck ID is required")
        UUID truckId
) {
}
