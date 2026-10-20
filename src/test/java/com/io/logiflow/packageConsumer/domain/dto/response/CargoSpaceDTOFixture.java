package com.io.logiflow.packageConsumer.domain.dto.response;

import com.io.logiflow.packageConsumer.infrastructure.dto.response.CargoSpaceDTO;

import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dto.response.CoordinatesDTOFixture.firstCoordinate;
import static com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTOFixture.dimensionLargeSpace;

public class CargoSpaceDTOFixture {

    public static CargoSpaceDTO boxFits() {
        return new CargoSpaceDTO(UUID.fromString("a5c8f3e1-9d2b-4f6a-8c1e-3b7d5e9a2f4c"), firstCoordinate(), dimensionLargeSpace(), null);
    }
}
