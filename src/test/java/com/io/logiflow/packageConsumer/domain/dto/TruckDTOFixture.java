package com.io.logiflow.packageConsumer.domain.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dto.DimensionsDTOFixture.dimensionSecondTruck;
import static com.io.logiflow.packageConsumer.domain.dto.DimensionsDTOFixture.dimensionTruck;

public class TruckDTOFixture {

    public static TruckDTO createLoadedTruckDTO() {
        TruckDTO dto = new TruckDTO();
        dto.setIdTruck(UUID.fromString("528e0004-a6f9-4018-bcba-87e20ad08606"));
        dto.setDimensionsDTO(dimensionTruck());
        dto.setMaxPayload(new BigDecimal("200"));
        dto.setCurrentWeight(new BigDecimal("80"));
        dto.setPackedPackages(List.of());
        dto.setSummarySpace(List.of());
        return dto;
    }

    public static TruckDTO createSecondLoadedTruckDTO() {
        TruckDTO dto = new TruckDTO();
        dto.setIdTruck(UUID.fromString("2b8ecd55-c636-4ba0-8192-42c9e8a14355"));
        dto.setDimensionsDTO(dimensionSecondTruck());
        dto.setMaxPayload(new BigDecimal("200"));
        dto.setCurrentWeight(new BigDecimal("80"));
        dto.setPackedPackages(List.of());
        dto.setSummarySpace(List.of());
        return dto;
    }

}
