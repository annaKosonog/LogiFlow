package com.io.logiflow.packageConsumer.domain.dto.response;

import com.io.logiflow.packageConsumer.infrastructure.dto.response.CoordinatesDTO;

public class CoordinatesDTOFixture {

    public static CoordinatesDTO firstCoordinate() {
        return new CoordinatesDTO(0, 0, 0);
    }

}
