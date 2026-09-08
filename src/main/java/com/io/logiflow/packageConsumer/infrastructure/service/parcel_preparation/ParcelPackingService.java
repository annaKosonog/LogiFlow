package com.io.logiflow.packageConsumer.infrastructure.service.parcel_preparation;

import com.io.logiflow.packageConsumer.domain.dao.CargoSpace;
import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.exception.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ParcelPackingService {
    public static final int UNPACKED_PACKING_ORDER = 0;

    private final PackageRepository packageRepository;
    private final PlacementStrategy placementStrategy;
    private final PackingRequestValidator packingRequestValidator;
    private final CargoSpaceFilter cargoSpaceFilter;
    private final PackagePlacer packagePlacer;


    @Transactional
    public void pack(List<UUID> boxesToPack, Truck truck) {
        List<Package> existingPackages = packingRequestValidator.resolveAndValidatePackages(boxesToPack);
        placementStrategy.sortedPack(existingPackages);

        List<CargoSpace> validZones = cargoSpaceFilter.filterValidZones(truck);
        List<Package> alreadyLoaded = new ArrayList<>(truck.getPackedPackages());
        int currentOrder = startingPackingOrder(alreadyLoaded);

        List<UUID> unpacked = new ArrayList<>();

        //package packing loop
        for (Package aPackage : existingPackages) {
            boolean success = packagePlacer.packSinglePackage(aPackage, truck, validZones, alreadyLoaded);
            if (!success) {
                aPackage.setPackingOrder(UNPACKED_PACKING_ORDER);
                unpacked.add(aPackage.getUuid());
            } else {
                aPackage.setPackingOrder(currentOrder++);
            }
        }
        packageRepository.saveAll(existingPackages);
        // If they are unpacked, it throws an exception
        if (!unpacked.isEmpty()) {
            throw new ResourceAlreadyExistsException(
                    "Packing incomplete! The following " + unpacked.size() + " package(s) could not fit: " + unpacked
            );
        }
    }

    private static int startingPackingOrder(List<Package> alreadyLoaded) {
        return alreadyLoaded.stream()
                .filter(p -> p.getCoordX() >= 0)
                .mapToInt(Package::getPackingOrder)
                .max()
                .orElse(0) + 1;
    }
}
