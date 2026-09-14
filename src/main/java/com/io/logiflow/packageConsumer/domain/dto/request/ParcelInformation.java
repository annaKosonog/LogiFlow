package com.io.logiflow.packageConsumer.domain.dto.request;

import com.io.logiflow.packageConsumer.domain.dto.response.CoordinatesDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTO;

import java.util.UUID;

public record ParcelInformation(UUID packageUUID, CoordinatesDTO coordinatesDTO, DimensionsDTO dimensionsDTO) {
}
