package com.io.logiflow.packageConsumer.application.service;

import com.io.logiflow.packageConsumer.domain.CargoGrid;
import com.io.logiflow.packageConsumer.domain.PackingRequestValidator;
import com.io.logiflow.packageConsumer.domain.ParcelPlacementFinder;
import com.io.logiflow.packageConsumer.infrastructure.dao.Package;
import com.io.logiflow.packageConsumer.infrastructure.dao.ProcessFailed;
import com.io.logiflow.packageConsumer.infrastructure.dao.Truck;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.PackingRequestDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.ParcelInformation;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.ProcessFailedRequestDto;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.CoordinatesDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.DimensionsDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.PackingResultDTO;
import com.io.logiflow.packageConsumer.infrastructure.mapper.ProcessFailedMapper;
import com.io.logiflow.packageConsumer.infrastructure.repository.PackageJpaRepository;
import com.io.logiflow.packageConsumer.infrastructure.repository.ProcessFailedRepository;
import com.io.logiflow.packageConsumer.infrastructure.repository.TruckRepository;
import com.io.logiflow.packageConsumer.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TruckPackingExecutor {


    private final TruckRepository truckRepository;
    private final PackageJpaRepository packageJpaRepository;
    private final ProcessFailedRepository processFailedRepository;
    private final ProcessFailedMapper processFailedMapper;
    private final ParcelPlacementFinder placementFinder;
    private final PackingRequestValidator packingRequestValidator;

    @Transactional
    public PackingResultDTO tryAddPackages(PackingRequestDTO packages) {
        Truck truck = truckRepository.findTruckByUuid(packages.truckId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "The truck with the id " + packages.truckId() + " does not exist"));

        List<Package> newPackages = packingRequestValidator.resolveAndValidatePackages(packages.packageId());
        newPackages.forEach(Package::getPackageId);

        List<ParcelInformation> placedParcels = truck.getPackedPackages().stream()
                .map(p -> new ParcelInformation(p.getUuid(),
                        new CoordinatesDTO(p.getCoordX(), p.getCoordY(), p.getCoordZ()),
                        new DimensionsDTO(p.getWidth(), p.getHeight(), p.getDepth())))
                .toList();

        CargoGrid grid = CargoGrid.fromPlacedParcels(truck.getWidth(), truck.getHeight(), truck.getLength(), placedParcels);

        int nextPackingOrder = startingPackingOrder(truck.getPackedPackages());

        List<Package> successfullyPlaced = new ArrayList<>();
        List<UUID> unplacedPackageUuids = new ArrayList<>();
        ProcessFailed entityRequest = new ProcessFailed();

        for (Package pkg : newPackages) {
            Optional<CoordinatesDTO> position = placementFinder
                    .findFreePosition(grid, pkg, truck.getWidth(), truck.getHeight(), truck.getLength());

            if (position.isEmpty()) {
                unplacedPackageUuids.add(pkg.getUuid());
                continue; // nie przerywamy — próbujemy zapakować resztę partii
            }

            CoordinatesDTO coords = position.get();
            pkg.setCoordX(coords.x());
            pkg.setCoordY(coords.y());
            pkg.setCoordZ(coords.z());

            DimensionsDTO dimension = new DimensionsDTO(pkg.getEffectiveWidth(), pkg.getHeight(), pkg.getEffectiveDepth());
            grid.markOccupied(new ParcelInformation(pkg.getUuid(), coords, dimension));

            truck.load(pkg);
            pkg.setTruck(truck);
            pkg.setPackingOrder(nextPackingOrder++);
            successfullyPlaced.add(pkg);


            if (!unplacedPackageUuids.isEmpty()) {
                ProcessFailedRequestDto processFailedRequestDto = new ProcessFailedRequestDto(unplacedPackageUuids);
                entityRequest = processFailedMapper.toEntityRequest(processFailedRequestDto);
                processFailedRepository.save(entityRequest);
            }
        }

        packageJpaRepository.saveAll(newPackages);

        truckRepository.save(truck);

        List<UUID> packedUuids = successfullyPlaced.stream()
                .map(Package::getUuid)
                .toList();


        return new PackingResultDTO(truck.getUuid(), packedUuids, entityRequest.getUuid());
    }

    private static int startingPackingOrder(List<Package> alreadyLoaded) {
        return alreadyLoaded.stream()
                .filter(p -> p.getCoordX() >= 0)
                .mapToInt(Package::getPackingOrder)
                .max()
                .orElse(0) + 1;
    }
}


