package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.domain.dto.TruckDTO;
import com.io.logiflow.packageConsumer.domain.mapper.TruckMapper;
import com.io.logiflow.packageConsumer.domain.repository.TruckRepository;
import com.io.logiflow.packageConsumer.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TruckService {

    private final TruckRepository truckRepository;
    private final ParcelPackingService parcelPackingService;
    private final TruckMapper truckMapper;

    public Page<TruckDTO> getTrucks(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Truck> savedTruck = truckRepository.findAll(pageable);
        return savedTruck.map(truckMapper::toDTO);
    }

    @Transactional
    public void addPackagesToTruck(List<PackageDTO> packages, Long truckId) {
        Truck truck = truckRepository.findById(truckId)
                .orElseThrow(() -> new ResourceNotFoundException("The truck with the id " + truckId + " does not exist"));
        parcelPackingService.pack(packages, truck);
        truckRepository.save(truck);
    }

}
