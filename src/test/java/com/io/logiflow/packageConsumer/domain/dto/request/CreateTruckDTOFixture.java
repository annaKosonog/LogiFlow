package com.io.logiflow.packageConsumer.domain.dto.request;

import com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTO;

import java.math.BigDecimal;

public class CreateTruckDTOFixture {

    public static CreateTruckDTO createFirstTruck() {
        return new CreateTruckDTO("WI 1234E", new DimensionsDTO(250, 220, 600), new BigDecimal(200));
    }

    public static CreateTruckDTO createSecondTruck() {
        return new CreateTruckDTO("W0 1234", new DimensionsDTO(178, 190, 430), new BigDecimal(200));
    }
}
