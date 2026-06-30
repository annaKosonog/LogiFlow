package com.io.logiflow.packageConsumer.domain.mapper;

import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.TruckDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PackageMapper.class, CargoSpaceMapper.class})
public interface TruckMapper {


    @Mapping(source = "uuid", target = "idTruck")
    TruckDTO toDTO(Truck truck);

    @Mapping(source = "idTruck", target = "uuid")
    Truck toEntity(TruckDTO truckDTO);
}
