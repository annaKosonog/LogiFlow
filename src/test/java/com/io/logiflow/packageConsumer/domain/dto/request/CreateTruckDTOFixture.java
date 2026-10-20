package com.io.logiflow.packageConsumer.domain.dto.request;

import com.io.logiflow.packageConsumer.infrastructure.dto.request.CreateTruckDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.DimensionsDTO;

import java.math.BigDecimal;

public class CreateTruckDTOFixture {

    public static CreateTruckDTO createFirstTruck() {
        return new CreateTruckDTO("WI 1234E", new BigDecimal("200.00"), new DimensionsDTO(250, 220, 600), new DimensionsDTO(230, 200, 595));
    }

    public static CreateTruckDTO createSecondTruck() {
        return new CreateTruckDTO("W0 1234", new BigDecimal("200.00"), new DimensionsDTO(178, 190, 430), new DimensionsDTO(150, 150, 400));
    }

    public static CreateTruckDTO createThirdTruck() {
        return new CreateTruckDTO("KR 12345", new BigDecimal(200), new DimensionsDTO(250, 220, 600), new DimensionsDTO(230, 200, 590));
    }

    public static CreateTruckDTO createEmptyWeightTruck() {
        return new CreateTruckDTO("PO 1234A", null, new DimensionsDTO(150, 220, 600), new DimensionsDTO(1300, 200, 600));
    }

    public static CreateTruckDTO createNegativeWidthTruck() {
        return new CreateTruckDTO("PO 1234A", new BigDecimal("150"), new DimensionsDTO(-150, 220, 600), new DimensionsDTO(150, 220, 600));
    }

    public static CreateTruckDTO createNullLicensePlateTruck() {
        return new CreateTruckDTO(null, new BigDecimal("150"), new DimensionsDTO(150, 220, 600), new DimensionsDTO(130, 200, 600));
    }
}
