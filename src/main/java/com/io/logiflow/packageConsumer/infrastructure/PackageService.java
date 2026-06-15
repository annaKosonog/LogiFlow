package com.io.logiflow.packageConsumer.infrastructure;

import com.io.logiflow.packageConsumer.domain.Package;
import com.io.logiflow.packageConsumer.domain.Truck;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.domain.repository.TruckRepository;
import com.io.logiflow.packageConsumer.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PackageService {

    private final PackageRepository packageRepository;
    private final TruckRepository truckRepository;
    private final ParcelPackingService parcelPackingService;


    @Transactional
    public Package registerNewPackage(Package pack) {
        String byTrackingNumber = packageRepository.findByTrackingNumber(pack.getTrackingNumber());
        if (byTrackingNumber != null) {
            throw new RuntimeException("The package with the number " + pack.getTrackingNumber() + " already exist");
        }
        packageRepository.save(pack);
        return pack;
    }

    @Transactional
    public void addPackagesToTruck(List<Package> packages, Long truckId) {
        Truck truck = truckRepository.findById(truckId)
                .orElseThrow(() -> new RuntimeException("The truck with the id " + truckId + " does not exist"));

        parcelPackingService.pack(packages, truck);
        truckRepository.save(truck);
    }

    public List<Package> getPackages() {
        return packageRepository.findAll();
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
