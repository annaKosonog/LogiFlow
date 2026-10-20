package com.io.logiflow.packageConsumer.infrastructure.mapper;

import com.io.logiflow.packageConsumer.infrastructure.dao.Package;
import com.io.logiflow.packageConsumer.infrastructure.dao.Truck;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.PackageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
    @Mapping(source = "dimensions.width", target = "width")
    @Mapping(source = "dimensions.height", target = "height")
    @Mapping(source = "dimensions.depth_length", target = "depth")
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

    List<Package> toEntityList(List<PackageDTO> dtoList);
}
