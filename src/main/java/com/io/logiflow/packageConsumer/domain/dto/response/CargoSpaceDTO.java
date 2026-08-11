package com.io.logiflow.packageConsumer.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CargoSpaceDTO {
    private UUID idCargo;
    private CoordinatesDTO coordinatesDTO;
    private DimensionsDTO dimensionsDTO;
    private TruckDTO truck;

}
