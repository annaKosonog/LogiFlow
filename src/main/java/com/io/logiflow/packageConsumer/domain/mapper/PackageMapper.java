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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = CommonMapper.class)
public interface PackageMapper {

    @Mapping(source = "uuid", target = "id")
    @Mapping(source = "truck", target = "truckID")
    @Mapping(source = "pack", target = "dimensions")
    @Mapping(source = "pack", target = "coordinates")
    PackageDTO toDTO(Package pack);


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
            entity.setDepth(dto.getDimensions().getDepth_length());
            entity.setWeight(dto.getWeight());
        }

        if (dto.getCoordinates() != null) {
            entity.setX(dto.getCoordinates().getX());
            entity.setY(dto.getCoordinates().getY());
            entity.setZ(dto.getCoordinates().getZ());
        }
    }

    List<Package> toEntityList(List<PackageDTO> dtoList);
}
