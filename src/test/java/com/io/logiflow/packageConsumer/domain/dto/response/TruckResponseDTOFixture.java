package com.io.logiflow.packageConsumer.domain.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public class TruckResponseDTOFixture {

    public static TruckResponseDTO firstCreateTruckResponse() {
        return new TruckResponseDTO(UUID.fromString("528e0004-a6f9-4018-bcba-87e20ad08606"), "WI 1234E", new DimensionsDTO(250, 220, 600), new BigDecimal(200));
    }

    public static TruckResponseDTO secondCreateTruckResponse() {
        return new TruckResponseDTO(UUID.fromString("c771f079-fb74-4811-b491-e78673c41e0d"), "W0 1234", new DimensionsDTO(178, 190, 430), new BigDecimal(150));
    }

    public static TruckResponseDTO thirdCreateTruckResponse() {
        return new TruckResponseDTO(UUID.fromString("528e0004-a6f9-4018-bcba-87e20ad08606"), "KR 12345", new DimensionsDTO(250, 220, 600), new BigDecimal(200));
    }

    public static TruckResponseDTO nullLicensePlateTruck() {
        return new TruckResponseDTO(UUID.randomUUID(), null, new DimensionsDTO(150, 220, 600), new BigDecimal("150"));
    }

}
