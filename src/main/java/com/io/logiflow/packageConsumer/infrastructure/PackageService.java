package com.io.logiflow.packageConsumer.infrastructure;

import com.io.logiflow.packageConsumer.domain.Package;
import com.io.logiflow.packageConsumer.domain.Truck;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.domain.repository.TruckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PackageService {

    private final PackageRepository packageRepository;
    private final TruckRepository truckRepository;
    private final ParcelPackingService parcelPackingService;


    @Transactional
    public Package registerNewPackage(Package pack) {
        String byTrackingNumber = packageRepository.findByTrackingNumber(pack.getTrackingNumber());
        if (byTrackingNumber == null) {
            throw new RuntimeException("The package with the number " + pack.getTrackingNumber() + " does not exist");
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
}
