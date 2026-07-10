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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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

    public TruckDTO getTruckByFilterUUID(UUID uuid) {
        validateFiltersUuid(uuid);
        return truckRepository.findTruckByUuid(uuid)
                .map(truckMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Truck with UUID number " + uuid +
                        "not found"));
    }


    public List<TruckDTO> getTrucksByLengthAndWeight(int length, BigDecimal maxPayload) {
        validateFilterByDimension(length, maxPayload);
        if (length != -1) {
            return truckRepository.findTruckByLength(length)
                    .stream()
                    .map(truckMapper::toDTO)
                    .toList();
        }

        if (maxPayload != null) {
            return truckRepository.findTruckByMaxPayload(maxPayload)
                    .stream()
                    .map(truckMapper::toDTO)
                    .toList();
        }
        throw new IllegalArgumentException("Provide at least one filter.");
    }

    private void validateFiltersUuid(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("Provide uuid.");
        }
    }

    private void validateFilterByDimension(int length, BigDecimal maxPayload) {
        if (length == 0 && (maxPayload == null || maxPayload.compareTo(BigDecimal.ZERO) <= 0)) {
            throw new IllegalArgumentException("Length or weight cannot be equals -1 or null.");
        }
    }
}
