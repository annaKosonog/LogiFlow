package com.io.logiflow.packageConsumer.infrastructure.service.parcel_preparation;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.request.PackingRequestDTO;
import com.io.logiflow.packageConsumer.domain.dto.request.ParcelInformation;
import com.io.logiflow.packageConsumer.domain.dto.response.CoordinatesDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTO;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.domain.repository.TruckRepository;
import com.io.logiflow.packageConsumer.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TruckPackingExecutor {


    private final TruckRepository truckRepository;
    private final PackageRepository packageRepository;
    private final ParcelPlacementFinder placementFinder;
    private final PackingRequestValidator packingRequestValidator;

    public Truck tryAddPackages(PackingRequestDTO packages) {
        Truck truck = truckRepository.findTruckByUuid(packages.truckId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "The truck with the id " + packages.truckId() + " does not exist"));

        List<Package> newPackages = packingRequestValidator.resolveAndValidatePackages(packages.packageId());

        List<ParcelInformation> placedParcels = truck.getPackedPackages().stream()
                .map(p -> new ParcelInformation(p.getUuid(),
                        new CoordinatesDTO(p.getCoordX(), p.getCoordY(), p.getCoordZ()),
                        new DimensionsDTO(p.getWidth(), p.getHeight(), p.getDepth())))
                .toList();

        CargoGrid grid = CargoGrid.fromPlacedParcels(truck.getWidth(), truck.getHeight(), truck.getLength(), placedParcels);

        int nextPackingOrder = startingPackingOrder(truck.getPackedPackages());

        for (Package pkg : newPackages) {
            CoordinatesDTO position = placementFinder
                    .findFreePosition(grid, pkg, truck.getWidth(), truck.getHeight(), truck.getLength())
                    .orElseThrow(() -> new IllegalStateException("No space for the package:: " + pkg.getUuid()));

            pkg.setCoordX(position.x());
            pkg.setCoordY(position.y());
            pkg.setCoordZ(position.z());

            DimensionsDTO dimension = new DimensionsDTO(pkg.getEffectiveWidth(), pkg.getHeight(), pkg.getEffectiveDepth());
            grid.markOccupied(new ParcelInformation(pkg.getUuid(), position, dimension));

            truck.load(pkg);
            pkg.setTruck(truck);
            pkg.setPackingOrder(nextPackingOrder++);
        }

        packageRepository.saveAll(newPackages);
        return truckRepository.save(truck);
    }

    private static int startingPackingOrder(List<Package> alreadyLoaded) {
        return alreadyLoaded.stream()
                .filter(p -> p.getCoordX() >= 0)
                .mapToInt(Package::getPackingOrder)
                .max()
                .orElse(0) + 1;
    }
}


