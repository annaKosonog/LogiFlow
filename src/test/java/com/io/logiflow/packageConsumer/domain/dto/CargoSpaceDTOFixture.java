package com.io.logiflow.packageConsumer.domain.dto;

import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dto.CoordinatesDTOFixture.firstCoordinate;
import static com.io.logiflow.packageConsumer.domain.dto.DimensionsDTOFixture.dimensionLargeSpace;

public class CargoSpaceDTOFixture {

    public static CargoSpaceDTO boxFits() {
        return new CargoSpaceDTO(UUID.randomUUID(), firstCoordinate(), dimensionLargeSpace(), null);
    }
}
