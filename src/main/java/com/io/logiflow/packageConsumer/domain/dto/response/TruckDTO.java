package com.io.logiflow.packageConsumer.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TruckDTO {
    private UUID idTruck;
    private DimensionsDTO dimensionsDTO;
    private BigDecimal maxPayload;
    private BigDecimal currentWeight;
    private List<PackageDTO> packedPackages;
    private List<CargoSpaceDTO> summarySpace;
}
