package com.io.logiflow.packageConsumer.infrastructure.service.parcel_preparation;

import com.io.logiflow.packageConsumer.domain.PackingRequestValidator;
import com.io.logiflow.packageConsumer.infrastructure.dao.Package;
import com.io.logiflow.packageConsumer.infrastructure.dao.Truck;
import com.io.logiflow.packageConsumer.infrastructure.repository.PackageJpaRepository;
import com.io.logiflow.packageConsumer.infrastructure.exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.firstBoxPackToTruck;
import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.thirdBoxPackToTruck;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedTruck;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PackingRequestValidatorTest {

    @Mock
    private PackageJpaRepository packageJpaRepository;

    @InjectMocks
    private PackingRequestValidator packingRequestValidator;

    List<UUID> uuids;
    List<Package> packageList;

    @BeforeEach
    void setUp() {
        uuids = new ArrayList<>();
        packageList = new ArrayList<>();
        packageJpaRepository.flush();
    }

    @AfterEach
    void cleanUpDatabase() {
        try {
            packageJpaRepository.deleteAll();
            packageJpaRepository.flush();
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    void shouldThrowExceptionWhenListUUIDIsNull() {
        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class, () -> packingRequestValidator.resolveAndValidatePackages(uuids));
        Assertions.assertEquals("The list of packages to be loaded cannot be empty!", exception.getMessage());
    }

    @Test
    void shouldReturnListWhenAllPackageExistsInDb() {
        packageJpaRepository.save(firstBoxPackToTruck());
        packageJpaRepository.save(thirdBoxPackToTruck());
        packageList.add(firstBoxPackToTruck());
        packageList.add(thirdBoxPackToTruck());

        uuids.add(UUID.fromString("3a2b1c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d"));
        uuids.add(UUID.fromString("d81f3a6c-5e97-4b2d-8c04-f7a2b9e1d365"));
        when(packageJpaRepository.findAllByUuidIn(anyList())).thenReturn(packageList);
        List<Package> result = packingRequestValidator.resolveAndValidatePackages(uuids);
        Assertions.assertEquals(2, result.size());
    }


    @Test
    void shouldThrowExceptionWhenOnePackageNotExistsInDb() {
        packageJpaRepository.save(firstBoxPackToTruck());
        packageList.add(firstBoxPackToTruck());
        uuids.add(UUID.fromString("3a2b1c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d"));
        uuids.add(UUID.fromString("7f3a2b1c-9d84-4e6f-b521-8a7c3d2e1f90"));
        when(packageJpaRepository.findAllByUuidIn(anyList())).thenReturn(packageList);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class, () -> packingRequestValidator.resolveAndValidatePackages(uuids));
        Assertions.assertEquals("Unable to load: One or more packages do not exist in the database!", exception.getMessage());
    }

    @Test
    void shouldPackMultiplePackagesThatIsLoadedOntoTheTruck() {
        Package packageAlreadyLoaded = firstBoxPackToTruck();
        packageJpaRepository.save(firstBoxPackToTruck());
        Truck loadedTruck = createLoadedTruck();
        packageList.add(packageAlreadyLoaded);
        loadedTruck.setPackedPackages(packageList);
        packageAlreadyLoaded.setTruck(loadedTruck);

        uuids.add(UUID.fromString("3a2b1c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d"));
        when(packageJpaRepository.findAllByUuidIn(anyList())).thenReturn(packageList);
        ResourceAlreadyExistsException message = assertThrows(ResourceAlreadyExistsException.class, () -> packingRequestValidator.resolveAndValidatePackages(uuids));

        assertEquals("The package with the number " + firstBoxPackToTruck().getTrackingNumber()
                + " has already been loaded onto the truck with ID: " + loadedTruck.getTruckId(), message.getMessage());
    }


}