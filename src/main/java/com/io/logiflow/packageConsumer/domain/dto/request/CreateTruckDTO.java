package com.io.logiflow.packageConsumer.domain.dto.request;

import com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateTruckDTO(
        @NotBlank(message = "License plate is required")
        String licensePlate,

        @NotNull(message = "Max payload is required")
        @Positive(message = "Max payload must be positive")
        BigDecimal maxPayload,

        @NotNull(message = "Exterior dimensions are required")
        @Valid
        DimensionsDTO exteriorDimensions,

        @NotNull(message = "Cargo space dimensions are required")
        @Valid
        DimensionsDTO cargoSpaceDimensions
) {
}