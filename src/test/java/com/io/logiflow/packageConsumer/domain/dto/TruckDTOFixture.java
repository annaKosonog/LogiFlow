package com.io.logiflow.packageConsumer.domain.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dto.DimensionsDTOFixture.dimensionTruck;

public class TruckDTOFixture {

    public static TruckDTO createLoadedTruck() {
        TruckDTO dto = new TruckDTO();
        dto.setIdTruck(UUID.fromString("528e0004-a6f9-4018-bcba-87e20ad08606"));
        dto.setDimensionsDTO(dimensionTruck());
        dto.setMaxPayload(new BigDecimal("200"));
        dto.setCurrentWeight(new BigDecimal("80"));
        dto.setPackedPackages(List.of());
        dto.setSummarySpace(List.of());
        return dto;
    }
}
