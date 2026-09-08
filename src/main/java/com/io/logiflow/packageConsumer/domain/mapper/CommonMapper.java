package com.io.logiflow.packageConsumer.domain.mapper;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.response.CoordinatesDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommonMapper {


    default DimensionsDTO toDimensionsDTO(Package pack) {
        if (pack == null) {
            return null;
        }
        return new DimensionsDTO(
                pack.getWidth(),
                pack.getHeight(),
                pack.getDepth()
        );
    }

    default DimensionsDTO toDimensionsDTO(Truck truck) {
        if (truck == null) {
            return null;
        }
        return new DimensionsDTO(
                truck.getWidth(),
                truck.getHeight(),
                truck.getLength()
        );
    }


    // Map fields X, Y, Z z to object CoordinatesDTO
    default CoordinatesDTO toCoordinatesDTO(Package pack) {
        if (pack == null) {
            return null;
        }
        return new CoordinatesDTO(
                pack.getCoordX(),
                pack.getCoordY(),
                pack.getCoordZ()
        );
    }
}