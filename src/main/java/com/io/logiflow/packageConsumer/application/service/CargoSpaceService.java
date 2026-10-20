package com.io.logiflow.packageConsumer.application.service;

import com.io.logiflow.packageConsumer.infrastructure.dao.CargoSpace;
import com.io.logiflow.packageConsumer.infrastructure.dao.Truck;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.CargoSpaceDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.DimensionsDTO;
import com.io.logiflow.packageConsumer.infrastructure.mapper.CargoSpaceMapper;
import com.io.logiflow.packageConsumer.infrastructure.repository.CargoSpaceRepository;
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
