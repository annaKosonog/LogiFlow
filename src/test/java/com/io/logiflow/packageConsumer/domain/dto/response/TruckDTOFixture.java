package com.io.logiflow.packageConsumer.domain.dto.response;

import com.io.logiflow.packageConsumer.infrastructure.dto.response.TruckDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTOFixture.dimensionSecondTruck;
import static com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTOFixture.dimensionTruck;

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
        dto.setIdTruck(UUID.fromString("c771f079-fb74-4811-b491-e78673c41e0d"));
        dto.setDimensionsDTO(dimensionSecondTruck());
        dto.setMaxPayload(new BigDecimal("200"));
        dto.setCurrentWeight(new BigDecimal("80"));
        dto.setPackedPackages(List.of());
        dto.setSummarySpace(List.of());
        return dto;
    }

}
