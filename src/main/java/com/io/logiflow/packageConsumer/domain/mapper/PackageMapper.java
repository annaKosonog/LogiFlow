package com.io.logiflow.packageConsumer.domain.mapper;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.CoordinatesDTO;
import com.io.logiflow.packageConsumer.domain.dto.DimensionsDTO;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PackageMapper {


    @Mapping(source = "uuid", target = "id")
    @Mapping(source = "truck", target = "truckID")
    @Mapping(source = "pack", target = "dimensions")
    @Mapping(source = "pack", target = "coordinates")
    PackageDTO toDTO(Package pack);

    default DimensionsDTO mapDimensionsDTO(Package pack) {
        if (pack == null) {
            return null;
        }

        return new DimensionsDTO(
                pack.getWidth(),
                pack.getHeight(),
                pack.getDepth(),
                pack.getWeight()
        );
    }

    default CoordinatesDTO mapCoordinatesDTO(Package pack) {
        if (pack == null) {
            return null;
        }

        return new CoordinatesDTO(
                pack.getX(),
                pack.getY(),
                pack.getZ()
        );
    }

    @Mapping(source = "id", target = "uuid")
    @Mapping(source = "truckID", target = "truck")
    Package toEntity(PackageDTO packageDTO);

    default Long mapTruckToId(Truck truck) {
        return truck != null ? truck.getTruckId() : null;
    }

    default Truck mapIdToTruck(Long truckId) {
        if (truckId == null) {
            return null;
        }
        Truck truck = new Truck();
        truck.setTruckId(truckId);
        return truck;
    }

    @AfterMapping
    default void mapDtoFieldsToEntity(PackageDTO dto, @MappingTarget Package entity) {
        if (dto == null) {
            return;
        }
        if (dto.getDimensions() != null) {
            entity.setWidth(dto.getDimensions().getWidth());
            entity.setHeight(dto.getDimensions().getHeight());
            entity.setDepth(dto.getDimensions().getDepth());
            entity.setWeight(dto.getDimensions().getWeight());
        }

        if (dto.getCoordinates() != null) {
            entity.setX(dto.getCoordinates().getX());
            entity.setY(dto.getCoordinates().getY());
            entity.setZ(dto.getCoordinates().getZ());
        }
    }
}
