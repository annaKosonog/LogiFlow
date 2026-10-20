package com.io.logiflow.packageConsumer.domain;

import com.io.logiflow.packageConsumer.infrastructure.dao.Package;
import com.io.logiflow.packageConsumer.infrastructure.repository.PackageRepository;
import com.io.logiflow.packageConsumer.infrastructure.exception.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PackingRequestValidator {

    private final PackageRepository packageRepository;

    public List<Package> resolveAndValidatePackages(List<UUID> boxesToPack) {
        if (boxesToPack == null || boxesToPack.isEmpty()) {
            throw new ResourceAlreadyExistsException("The list of packages to be loaded cannot be empty!");
        }
        List<Package> existingPackages = packageRepository.findAllByUuidIn(boxesToPack);

        if (existingPackages.size() != boxesToPack.size()) {
            throw new ResourceAlreadyExistsException("Unable to load: One or more packages do not exist in the database!");
        }
        validatePackagesAreNotAlreadyLoaded(existingPackages);
        return existingPackages;
    }

    private void validatePackagesAreNotAlreadyLoaded(List<Package> packages) {
        packages.stream()
                .filter(pkg -> pkg.getTruck() != null)
                .findFirst()
                .ifPresent(pkg -> {
                    throw new ResourceAlreadyExistsException("The package with the number " + pkg.getTrackingNumber()
                            + " has already been loaded onto the truck with ID: " + pkg.getTruck().getTruckId()
                    );
                });
    }
}
