package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.domain.dto.TruckDTO;
import com.io.logiflow.packageConsumer.domain.dto.TruckFilterDto;
import com.io.logiflow.packageConsumer.domain.mapper.TruckMapper;
import com.io.logiflow.packageConsumer.domain.repository.TruckRepository;
import com.io.logiflow.packageConsumer.exception.ResourceNotFoundException;
import com.io.logiflow.packageConsumer.infrastructure.specification.TruckSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TruckService {

    private final TruckRepository truckRepository;
    private final ParcelPackingService parcelPackingService;
    private final TruckMapper truckMapper;

    public Page<TruckDTO> getTrucks(TruckFilterDto filter, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Specification<Truck> truckSpecification = TruckSpecifications.buildSpecification(filter);
        Page<Truck> savedTruck = truckRepository.findAll(truckSpecification, pageable);
        return savedTruck.map(truckMapper::toDTO);
    }

    @Transactional
    public void addPackagesToTruck(List<PackageDTO> packages, UUID truckId) {
        Truck truck = truckRepository.findTruckByUuid(truckId)
                .orElseThrow(() -> new ResourceNotFoundException("The truck with the id " + truckId + " does not exist"));
        parcelPackingService.pack(packages, truck);
        truckRepository.save(truck);
    }
}