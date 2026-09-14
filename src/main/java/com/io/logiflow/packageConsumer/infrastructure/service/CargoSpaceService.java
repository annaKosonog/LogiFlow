package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.CargoSpace;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.response.CargoSpaceDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTO;
import com.io.logiflow.packageConsumer.domain.mapper.CargoSpaceMapper;
import com.io.logiflow.packageConsumer.domain.repository.CargoSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CargoSpaceService {

    private final CargoSpaceMapper cargoSpaceMapper;
    private final CargoSpaceRepository cargoSpaceRepository;

    public CargoSpaceDTO addNewCargoSpaceToTruck(Truck truck, DimensionsDTO dimension) {
        CargoSpace entity = buildCargoSpaceToTruck(truck, dimension);
        CargoSpace savedEntity = cargoSpaceRepository.save(entity);
        return cargoSpaceMapper.toDTO(savedEntity);
    }

    private CargoSpace buildCargoSpaceToTruck(Truck truck, DimensionsDTO dimensions) {
        CargoSpace space = new CargoSpace();
        space.setUuid(UUID.randomUUID());
        space.setTruck(truck);
        space.setPositionX(0);
        space.setPositionY(0);
        space.setPositionZ(0);
        space.setDepth(dimensions.getDepth_length());
        space.setHeight(dimensions.getHeight());
        space.setWidth(dimensions.getWidth());
        return space;
    }

}
