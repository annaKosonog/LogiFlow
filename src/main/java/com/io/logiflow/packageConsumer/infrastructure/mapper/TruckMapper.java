package com.io.logiflow.packageConsumer.infrastructure.mapper;

import com.io.logiflow.packageConsumer.infrastructure.dao.Truck;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.CreateTruckDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.TruckDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.TruckResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PackageMapper.class, CargoSpaceMapper.class, CommonMapper.class})
public interface TruckMapper {


    @Mapping(source = "uuid", target = "idTruck")
    @Mapping(source = "truck", target = "dimensionsDTO")
    TruckDTO toDTO(Truck truck);

    @Mapping(source = "idTruck", target = "uuid")
    Truck toEntity(TruckDTO truckDTO);


    @Mapping(source = "licensePlate", target = "licensePlate")
    @Mapping(source = "exteriorDimensions.width", target = "width")
    @Mapping(source = "exteriorDimensions.height", target = "height")
    @Mapping(source = "exteriorDimensions.depth_length", target = "length")
    Truck toEntityTruck(CreateTruckDTO createTruckDTO);


    @Mapping(target = "uuid", source ="uuid" )
    @Mapping(source = "licensePlate", target = "licensePlate")
    @Mapping(source = "truck", target = "dimensions")
    TruckResponseDTO toTruckResponseDTO(Truck truck);
}
