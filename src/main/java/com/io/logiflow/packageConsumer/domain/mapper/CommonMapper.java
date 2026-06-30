package com.io.logiflow.packageConsumer.domain.mapper;

import com.io.logiflow.packageConsumer.domain.dto.CoordinatesDTO;
import com.io.logiflow.packageConsumer.domain.dto.DimensionsDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommonMapper {
    default DimensionsDTO mapDimensionsDTO(DimensionsDTO dimensionsDTO) {
        if (dimensionsDTO == null) {
            return null;
        }

        return new DimensionsDTO(
                dimensionsDTO.getWidth(),
                dimensionsDTO.getHeight(),
                dimensionsDTO.getDepth_length()
        );
    }

    default CoordinatesDTO mapCoordinates(CoordinatesDTO coordinates) {
        if (coordinates == null) {
            return null;
        }

        return new CoordinatesDTO(
                coordinates.getX(),
                coordinates.getY(),
                coordinates.getZ()
        );
    }
}
