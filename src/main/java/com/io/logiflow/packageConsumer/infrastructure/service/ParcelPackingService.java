package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.CargoSpace;
import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.exception.PackageValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ParcelPackingService {

    private final PackageRepository packageRepository;

    @Transactional
    public void pack(List<PackageDTO> boxesToPack, Truck entityTruck) {
        if (boxesToPack == null || boxesToPack.isEmpty()) {
            throw new PackageValidationException("The list of packages to be loaded cannot be empty!");
        }
        List<UUID> uuids = boxesToPack.stream()
                .map(PackageDTO::getId)
                .toList();

        List<Package> existingPackages = packageRepository.findAllByUuidIn(uuids);
        if (existingPackages.size() != uuids.size()) {
            throw new PackageValidationException("Unable to load: One or more packages do not exist in the database!");
        }

        validatePackagesAreNotAlreadyLoaded(existingPackages);
        sortedPack(existingPackages);
        List<CargoSpace> finalCargoSpaces = entityTruck.getEmptyCargoSpaces();
        entityTruck.setEmptyCargoSpaces(finalCargoSpaces);

        for (Package aPackage : existingPackages) {
            //Sprawdzamy wagę // BRAMKARZ: Jeśli ciężarówka nie udźwignie tej paczki, odrzucamy ją OD RAZU!
            entityTruck.load(aPackage); // Próba sprawdzenia wagi

            boolean packed = false;

            for (int i = 0; i < finalCargoSpaces.size(); i++) {
                CargoSpace cargoSpace = finalCargoSpaces.get(i);
                if (checksThatThePackageFits(aPackage, cargoSpace)) {
                    cargoSpace.setTruck(entityTruck);
                    aPackage.placeInSpace(cargoSpace); //Paczka trafia do cieżarówki i następuje przypisanie współrzędnych paczce
                    splitSpace(cargoSpace, aPackage, finalCargoSpaces);
                    packed = true;
                    break;
                }
            }
            if (!packed) {
                entityTruck.unload(aPackage);
            }
        }
        entityTruck.setTruckId(entityTruck.getTruckId());
        existingPackages.forEach(pkg -> pkg.setTruck(entityTruck));
        packageRepository.saveAll(existingPackages);
    }


    //Sortowanie paczek od najmniejszej do największej a potem bierzemy cięższe paczki
    private void sortedPack(List<Package> boxesToPack) {
        boxesToPack.sort(
                Comparator.comparingInt((Package b) -> b.getWidth() * b.getHeight() * b.getDepth())
                        .thenComparing(Package::getWeight, Comparator.reverseOrder())
        );
    }

    private boolean checksThatThePackageFits(Package aPackage, CargoSpace cargoSpace) {
        return aPackage.getWidth() <= cargoSpace.getWidth() &&
                aPackage.getHeight() <= cargoSpace.getHeight() &&
                aPackage.getDepth() <= cargoSpace.getDepth();
    }

    private void splitSpace(CargoSpace s, Package b, List<CargoSpace> cargoSpaces) { // podział przestrzeni (pusta - pełna = nowa)
        cargoSpaces.remove(s); // Usuniecie starego pudła

        // Generujemy 3 nowe przestrzenie, które powstały wokół paczki wewnątrz starego bloku 's'
        // 1. Przestrzeń OBOK paczki (po szerokości, idziemy po X)
        if (s.getWidth() - b.getWidth() > 0) {
            cargoSpaces.add(0, new CargoSpace(s.getPositionX() + b.getWidth(), s.getPositionY(), s.getPositionZ(), s.getWidth() - b.getWidth(), s.getHeight(), s.getDepth()));
        }
        // 2. Przestrzeń NAD paczką (po wysokości)
        if (s.getHeight() - b.getHeight() > 0) {
            cargoSpaces.add(0, new CargoSpace(s.getPositionX(), s.getPositionY() + b.getHeight(), s.getPositionZ(), s.getWidth(), s.getHeight() - b.getHeight(), s.getDepth()));
        }
        // 3. Przestrzeń PRZED paczką (po długości/głębokości)
        if (s.getDepth() - b.getDepth() > 0) {
            cargoSpaces.add(0, new CargoSpace(s.getPositionX(), s.getPositionY(), s.getPositionZ() + b.getDepth(), s.getWidth(), s.getHeight(), s.getDepth() - b.getDepth()));
        }

    }

    private void validatePackagesAreNotAlreadyLoaded(List<Package> packages) {
        packages.stream()
                .filter(pkg -> pkg.getTruck() != null)
                .findFirst()
                .ifPresent(pkg -> {
                    throw new PackageValidationException("The package with the number " + pkg.getTrackingNumber()
                            + " has already been loaded onto the truck with ID: " + pkg.getTruck().getTruckId()
                    );
                });
    }
}
