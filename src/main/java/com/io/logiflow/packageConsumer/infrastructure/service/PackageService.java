package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.domain.mapper.PackageMapper;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.domain.repository.TruckRepository;
import com.io.logiflow.packageConsumer.exception.PackageValidationException;
import com.io.logiflow.packageConsumer.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PackageService {

    private final PackageRepository packageRepository;
    private final TruckRepository truckRepository;
    private final ParcelPackingService parcelPackingService;
    private final PackageMapper packageMapper;


    @Transactional
    public PackageDTO registerNewPackage(PackageDTO pack) {
        String byTrackingNumber = packageRepository.findByTrackingNumber(pack.getTrackingNumber());
        if (byTrackingNumber != null) {
            throw new RuntimeException("The package with the number " + pack.getTrackingNumber() + " already exist");
        }
        Package entity = packageMapper.toEntity(pack);
        if (pack.getId() != null) {
            throw new PackageValidationException("Cannot register a new package: ID must be null. Provided: "
                    + pack.getId());
        }
        entity.setUuid(UUID.randomUUID());
        Package savedEntity = packageRepository.save(entity);
        return packageMapper.toDTO(savedEntity);
    }

    @Transactional
    public void addPackagesToTruck(List<Package> packages, Long truckId) {
        Truck truck = truckRepository.findById(truckId)
                .orElseThrow(() -> new RuntimeException("The truck with the id " + truckId + " does not exist"));

        parcelPackingService.pack(packages, truck);
        truckRepository.save(truck);
    }

    public Page<PackageDTO> getPackages(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Package> packPage = packageRepository.findAll(pageable);
        return packPage.map(packageMapper::toDTO);
    }

    @Transactional
    public Package updatePackageFields(Long packId, Map<String, Object> fields) { //waga, wymiary, adres dostawy
        Package existingPackage = packageRepository.findById(packId)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found"));

        // Sprawdzamy czy w JSONIE przesłano konkretne klucze i bezpiecznie je przepisujemy
        if (fields.containsKey("width")) {
            existingPackage.setWidth(((Number) fields.get("width")).intValue());
        }
        if (fields.containsKey("height")) {
            existingPackage.setHeight(((Number) fields.get("height")).intValue());
        }
        if (fields.containsKey("depth")) {
            existingPackage.setDepth(((Number) fields.get("depth")).intValue());
        }
        if (fields.containsKey("weight")) {
            Object weightValue = fields.get("weight");
            existingPackage.setWeight(new java.math.BigDecimal(weightValue.toString()));
        }
        return packageRepository.save(existingPackage);
    }
}
