package com.io.logiflow.packageConsumer.domain.mapper;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dto.CoordinatesDTO;
import com.io.logiflow.packageConsumer.domain.dto.DimensionsDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommonMapper {

    // Mapstruct widzi, skąd wziąć dane dla DimensionsDTO, bo przekazujemy mu całą encję Package
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

    // Mapujemy płaskie pola X, Y, Z z encji na obiekt CoordinatesDTO
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