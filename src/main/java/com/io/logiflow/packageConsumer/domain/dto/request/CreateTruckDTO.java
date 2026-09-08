package com.io.logiflow.packageConsumer.domain.dto.request;

import com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTO;

import java.math.BigDecimal;

public record CreateTruckDTO(
        String licensePlate,
        DimensionsDTO exteriorDimensions,
        BigDecimal maxPayload,
        DimensionsDTO cargoSpaceDimensions
) {
}