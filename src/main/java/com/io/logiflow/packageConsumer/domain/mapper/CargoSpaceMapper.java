package com.io.logiflow.packageConsumer.domain.mapper;

import com.io.logiflow.packageConsumer.domain.dao.CargoSpace;
import com.io.logiflow.packageConsumer.domain.dto.CargoSpaceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TruckMapper.class, CommonMapper.class})
public interface CargoSpaceMapper {

    @Mapping(source = "uuid", target = "idCargo")
    CargoSpaceDTO toDTO(CargoSpace space);

    @Mapping(source = "idCargo", target = "uuid")
    CargoSpace toEntity(CargoSpaceDTO spaceDTO);


}
