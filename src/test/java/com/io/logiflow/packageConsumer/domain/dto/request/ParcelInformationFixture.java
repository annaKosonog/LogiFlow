package com.io.logiflow.packageConsumer.domain.dto.request;

import com.io.logiflow.packageConsumer.infrastructure.dto.request.ParcelInformation;

import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dto.response.CoordinatesDTOFixture.firstCoordinate;
import static com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTOFixture.dimensionMediumDTO;

public class ParcelInformationFixture {

    public static ParcelInformation firstInformation() {
        return new ParcelInformation(UUID.fromString("c8d23e1f-7b8a-4c2d-9e5f-1a3b5c7d9e2f"), firstCoordinate(), dimensionMediumDTO());
    }

}
