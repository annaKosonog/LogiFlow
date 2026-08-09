package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.CargoSpace;
import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.exception.PackageValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ParcelPackingService {

    private final PackageRepository packageRepository;
    private static final boolean[] ALLOWED_ROTATIONS = {false, true};


    public void pack(List<UUID> boxesToPack, Truck truck) {
        List<Package> existingPackages = resolveAndValidatePackages(boxesToPack);
        sortedPack(existingPackages);
        List<CargoSpace> finalCargoSpaces = new ArrayList<>(truck.getEmptyCargoSpaces().stream()
                .filter(zone -> zoneFitsWithinTruck(zone, truck))
                .toList());

        if (finalCargoSpaces.isEmpty()) {
            throw new PackageValidationException("Truck ID: " + truck.getTruckId() + " has no valid available CargoSpaces!");
        }

        List<Package> alreadyLoaded = new ArrayList<>(packageRepository.findAllByTruck(truck));

        int currentOrder = startingPackingOrder(alreadyLoaded);

        List<UUID> unpacked = new ArrayList<>();

        //Pętla pakowania paczek
        for (Package aPackage : existingPackages) {
            boolean success = packSinglePackage(aPackage, truck, finalCargoSpaces, alreadyLoaded);
            if (!success) {
                unpacked.add(aPackage.getUuid());
                aPackage.setPackingOrder(0);
            }
            aPackage.setPackingOrder(currentOrder++);
        }
        packageRepository.saveAll(existingPackages);
        // JEŚLI SĄ NIESPAKOWANE PACZKI -> RZUCAMY WYJĄTEK
        if (!unpacked.isEmpty()) {
            throw new PackageValidationException(
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

    private boolean zoneFitsWithinTruck(CargoSpace zone, Truck truck) {
        boolean fits = zone.getPositionX() >= 0 && zone.getPositionY() >= 0 && zone.getPositionZ() >= 0
                && zone.getPositionX() + zone.getWidth() <= truck.getWidth()
                && zone.getPositionY() + zone.getHeight() <= truck.getHeight()
                && zone.getPositionZ() + zone.getDepth() <= truck.getLength();

        if (!fits) {
            System.out.printf("ZONE REJECTED! Zone (WxHxD): %dx%dx%d vs Truck (WxHxL): %dx%dx%d%n",
                    zone.getWidth(), zone.getHeight(), zone.getDepth(),
                    truck.getWidth(), truck.getHeight(), truck.getLength());
        }
        return fits;
    }

    private List<Package> resolveAndValidatePackages(List<UUID> boxesToPack) {
        if (boxesToPack == null || boxesToPack.isEmpty()) {
            throw new PackageValidationException("The list of packages to be loaded cannot be empty!");
        }
        List<Package> existingPackages = packageRepository.findAllByUuidIn(boxesToPack);

        if (existingPackages.size() != boxesToPack.size()) {
            throw new PackageValidationException("Unable to load: One or more packages do not exist in the database!");
        }
        validatePackagesAreNotAlreadyLoaded(existingPackages);
        return existingPackages;
    }

    //Sortowanie paczek od najmniejszej do największej a potem bierzemy cięższe paczki
    private void sortedPack(List<Package> boxesToPack) {
        boxesToPack.sort(
                Comparator.comparingInt((Package b) -> b.getWidth() * b.getHeight() * b.getDepth())
                        .reversed()
                        .thenComparing(Package::getWeight, Comparator.reverseOrder())
        );
    }

    private boolean packSinglePackage(Package aPackage, Truck truck, List<CargoSpace> finalCargoSpaces,
                                      List<Package> alreadyLoaded) {
        boolean packed = tryPlacePackage(aPackage, truck, finalCargoSpaces, alreadyLoaded);
        truck.load(aPackage);

        if (!packed) {
            truck.unload(aPackage);
            resetPackageCoordinates(aPackage);
        }
        return packed;
    }

    private boolean tryPlacePackage(Package aPackage, Truck truck,
                                    List<CargoSpace> finalCargoSpaces, List<Package> alreadyLoaded) {
        for (CargoSpace cargoSpace : finalCargoSpaces) {
            List<int[]> candidates = generateCandidatePoints(cargoSpace, truck.getPackedPackages());
            candidates.sort(
                    Comparator.<int[]>comparingInt(c -> c[1])   // Y — grawitacja (dół najpierw)
                            .thenComparingInt(c -> c[2])         // Z — tył
                            .thenComparingInt(c -> c[0])         // X — bok
            );

            for (int[] point : candidates) {
                int x = point[0], y = point[1], z = point[2];

                Boolean bestRotation = pickBestRotation(aPackage, cargoSpace, x, y, z);
                if (bestRotation == null) {
                    continue; // żadna orientacja nie pasuje w tym punkcie
                }

                aPackage.setCoordX(x);
                aPackage.setCoordY(y);
                aPackage.setCoordZ(z);

                // Jeśli paczka nachodzi na inne, resetujemy jej pozycję i sprawdzamy KOLEJNĄ przestrzeń
                if (overlapsWithLoadedPackages(aPackage, alreadyLoaded)) {
                    resetPackageCoordinates(aPackage);
                    continue;
                }
                aPackage.setTruck(truck);
                alreadyLoaded.add(aPackage);
                return true;
            }
        }

        // Nie znaleziono miejsca w żadnej strefie/orientacji — czyścimy flagę obrotu razem ze współrzędnymi
        aPackage.setRotated(false);
        return false;
    }

    //Sprawdzamy czy paczka pasuje do przestrzeni
// Sprawdza obie orientacje w DANYM punkcie i zwraca tę, która pasuje CIAŚNIEJ
// (mniej zmarnowanego miejsca w strefie). Zwraca null, jeśli żadna nie pasuje.
// Przy remisie preferuje orientację NIEobróconą (false) — po co obracać bez korzyści.
    private Boolean pickBestRotation(Package aPackage, CargoSpace zone, int x, int y, int z) {
        Boolean best = null;
        int bestWaste = Integer.MAX_VALUE;

        for (boolean rotated : ALLOWED_ROTATIONS) {
            aPackage.setRotated(rotated);

            if (!fitsWithinZoneBounds(x, y, z, aPackage, zone)) {
                continue;
            }

            int waste = wastedMargin(aPackage, zone);
            if (waste < bestWaste) {
                bestWaste = waste;
                best = rotated;
            }
        }
        return best;
    }

    // Suma nadmiaru szerokości i głębokości paczki względem strefy — im mniej, tym ciaśniejsze dopasowanie.
    private int wastedMargin(Package b, CargoSpace zone) {
        int widthMargin = zone.getWidth() - b.getEffectiveWidth();
        int depthMargin = zone.getDepth() - b.getEffectiveDepth();
        return Math.max(widthMargin, depthMargin);
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

    private List<int[]> generateCandidatePoints(CargoSpace zone, List<Package> loadedPackages) {
        List<int[]> candidates = new ArrayList<>();
        candidates.add(new int[]{zone.getPositionX(), zone.getPositionY(), zone.getPositionZ()});

        for (Package p : loadedPackages) {
            if (!isWithinZone(p, zone)) {
                continue; // interesują nas tylko paczki leżące w TEJ konkretnej strefie
            }
            candidates.add(new int[]{p.getCoordX() + p.getEffectiveWidth(), p.getCoordY(), p.getCoordZ()});
            candidates.add(new int[]{p.getCoordX(), p.getCoordY() + p.getHeight(), p.getCoordZ()});
            candidates.add(new int[]{p.getCoordX(), p.getCoordY(), p.getCoordZ() + p.getEffectiveDepth()});
        }
        return candidates;
    }

    private boolean isWithinZone(Package p, CargoSpace zone) {
        return p.getCoordX() >= zone.getPositionX() && p.getCoordX() < zone.getPositionX() + zone.getWidth()
                && p.getCoordY() >= zone.getPositionY() && p.getCoordY() < zone.getPositionY() + zone.getHeight()
                && p.getCoordZ() >= zone.getPositionZ() && p.getCoordZ() < zone.getPositionZ() + zone.getDepth();
    }

    private boolean fitsWithinZoneBounds(int x, int y, int z, Package b, CargoSpace zone) {
        return x >= zone.getPositionX()
                && y >= zone.getPositionY()
                && z >= zone.getPositionZ()
                && x + b.getEffectiveWidth() <= zone.getPositionX() + zone.getWidth()
                && y + b.getHeight() <= zone.getPositionY() + zone.getHeight()
                && z + b.getEffectiveDepth() <= zone.getPositionZ() + zone.getDepth();
    }

    private boolean boxesOverlap(Package a, Package b) {
        boolean separatedOnX = a.getCoordX() + a.getEffectiveWidth() <= b.getCoordX()
                || b.getCoordX() + b.getEffectiveWidth() <= a.getCoordX();

        boolean separatedOnY = a.getCoordY() + a.getHeight() <= b.getCoordY()
                || b.getCoordY() + b.getHeight() <= a.getCoordY();

        boolean separatedOnZ = a.getCoordZ() + a.getEffectiveDepth() <= b.getCoordZ()
                || b.getCoordZ() + b.getEffectiveDepth() <= a.getCoordZ();

        return !(separatedOnX || separatedOnY || separatedOnZ);
    }

    private boolean overlapsWithLoadedPackages(Package candidate, List<Package> loadedPackages) {
        for (Package loaded : loadedPackages) {
            if (loaded.getUuid().equals(candidate.getUuid())) {
                continue; // nie porównujemy paczki samej ze sobą
            }
            if (boxesOverlap(candidate, loaded)) {
                return true;
            }
        }
        return false;
    }

    private void resetPackageCoordinates(Package pkg) {
        pkg.setCoordX(-1);
        pkg.setCoordY(-1);
        pkg.setCoordZ(-1);
        pkg.setRotated(false);
    }
}
