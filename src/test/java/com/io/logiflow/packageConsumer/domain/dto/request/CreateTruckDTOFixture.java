package com.io.logiflow.packageConsumer.domain.dto.request;

import com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTO;

import java.math.BigDecimal;

public class CreateTruckDTOFixture {

    public static CreateTruckDTO createFirstTruck() {
        return new CreateTruckDTO("WI 1234E", new DimensionsDTO(250, 220, 600), new BigDecimal("200.00"));
    }

    public static CreateTruckDTO createSecondTruck() {
        return new CreateTruckDTO("W0 1234", new DimensionsDTO(178, 190, 430), new BigDecimal(200));
    }

    public static CreateTruckDTO createThirdTruck() {
        return new CreateTruckDTO("KR 12345", new DimensionsDTO(250, 220, 600), new BigDecimal(200));
    }

    public static CreateTruckDTO createEmptyWeightTruck() {
        return new CreateTruckDTO("PO 1234A", new DimensionsDTO(150, 220, 600), null);
    }

    public static CreateTruckDTO createNegativeWidthTruck() {
        return new CreateTruckDTO("PO 1234A", new DimensionsDTO(-150, 220, 600), new BigDecimal("150"));
    }
    public static CreateTruckDTO createNullLicensePlateTruck() {
        return new CreateTruckDTO(null, new DimensionsDTO(150, 220, 600), new BigDecimal("150"));
    }
}
