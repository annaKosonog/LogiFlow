package com.io.logiflow.packageConsumer.domain.mapper;

import com.io.logiflow.packageConsumer.domain.dao.CargoSpace;
import com.io.logiflow.packageConsumer.domain.dto.response.CargoSpaceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TruckMapper.class, CommonMapper.class})
public interface CargoSpaceMapper {

    @Mapping(source = "uuid", target = "idCargo")
    @Mapping(source = "space", target = "coordinatesDTO")
    @Mapping(source = "space", target = "dimensionsDTO")
    @Mapping(source = "space.depth", target = "dimensionsDTO.depth_length")
    @Mapping(source = "space.height", target = "dimensionsDTO.height")
    @Mapping(source = "space.width", target = "dimensionsDTO.width")
    CargoSpaceDTO toDTO(CargoSpace space);

    @Mapping(source = "idCargo", target = "uuid")
    CargoSpace toEntity(CargoSpaceDTO spaceDTO);


}
