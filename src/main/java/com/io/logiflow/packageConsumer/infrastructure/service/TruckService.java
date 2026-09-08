package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.request.CreateTruckDTO;
import com.io.logiflow.packageConsumer.domain.dto.request.PackingRequestDTO;
import com.io.logiflow.packageConsumer.domain.dto.request.TruckFilterDto;
import com.io.logiflow.packageConsumer.domain.dto.response.TruckDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.TruckResponseDTO;
import com.io.logiflow.packageConsumer.domain.mapper.TruckMapper;
import com.io.logiflow.packageConsumer.domain.repository.TruckRepository;
import com.io.logiflow.packageConsumer.exception.ResourceAlreadyExistsException;
import com.io.logiflow.packageConsumer.exception.ResourceNotFoundException;
import com.io.logiflow.packageConsumer.infrastructure.service.parcel_preparation.ParcelPackingService;
import com.io.logiflow.packageConsumer.infrastructure.specification.TruckSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.validator.DimensionValidator.validateDimensions;
import static com.io.logiflow.packageConsumer.domain.validator.DimensionValidator.validateNotBlank;

@Service
@RequiredArgsConstructor
public class TruckService {

    private final TruckRepository truckRepository;
    private final ParcelPackingService parcelPackingService;
    private final CargoSpaceService cargoSpaceService;
    private final TruckMapper truckMapper;

    @Transactional
    public Page<TruckDTO> getTrucks(TruckFilterDto filter, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Specification<Truck> truckSpecification = TruckSpecifications.buildSpecification(filter);
        Page<Truck> savedTruck = truckRepository.findAll(truckSpecification, pageable);
        return savedTruck.map(truckMapper::toDTO);
    }

    @Transactional
    public void addPackagesToTruck(PackingRequestDTO request) {
        Truck truck = truckRepository.findTruckByUuid(request.truckId())
                .orElseThrow(() -> new ResourceNotFoundException("The truck with the id " + request.truckId() + " does not exist"));
        parcelPackingService.pack(request.packageId(), truck);
    }

    @Transactional
    public TruckResponseDTO createTruck(CreateTruckDTO newTruck) {
        validateNewTruck(newTruck);
        validateDimensions(newTruck.exteriorDimensions(), "Exterior");
        validateDimensions(newTruck.cargoSpaceDimensions(), "Cargo space");
        validateNotBlank(newTruck.licensePlate(), "license_plate");
        if (truckRepository.existsByLicensePlate(newTruck.licensePlate())) {
            throw new ResourceAlreadyExistsException("The truck with license plate " + newTruck.licensePlate() + " already exists");
        }

        Truck truck = buildTruck(newTruck);
        Truck savedEntity = truckRepository.save(truck);
        cargoSpaceService.addNewCargoSpaceToTruck(savedEntity, newTruck.cargoSpaceDimensions());

        return truckMapper.toTruckResponseDTO(savedEntity);
    }

    private void validateNewTruck(CreateTruckDTO truck) {
        if (truck == null || truck.exteriorDimensions() == null || truck.maxPayload() == null) {
            throw new IllegalArgumentException("Dimensions and weight are required");
        }
    }

    private Truck buildTruck(CreateTruckDTO request) {
        Truck truck = new Truck();
        truck.setUuid(UUID.randomUUID());
        truck.setLicensePlate(request.licensePlate());
        truck.setMaxPayload(request.maxPayload());
        truck.setWidth(request.exteriorDimensions().getWidth());
        truck.setHeight(request.exteriorDimensions().getHeight());
        truck.setLength(request.exteriorDimensions().getDepth_length());
        return truck;
    }


}