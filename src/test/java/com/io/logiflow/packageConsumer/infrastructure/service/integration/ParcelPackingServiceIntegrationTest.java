package com.io.logiflow.packageConsumer.infrastructure.service.integration;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.repository.CargoSpaceRepository;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.domain.repository.TruckRepository;
import com.io.logiflow.packageConsumer.exception.ResourceAlreadyExistsException;
import com.io.logiflow.packageConsumer.infrastructure.controllers.integrations.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.*;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThrows;

class ParcelPackingServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private TruckRepository truckRepository;

    @Autowired
    private CargoSpaceRepository cargoSpaceRepository;

    @Autowired
    private ParcelPackingService parcelPackingService;

    private Truck savedTruck;
    private Package savedFirstPackage;
    private Package savedSecondPackages;
    private Package savedThirdPackage;
    private List<UUID> uuids;

    @BeforeEach
    void setUp() {
        savedTruck = truckRepository.save(createLoadedTruck());
        savedFirstPackage = packageRepository.save(firstBoxPackToTruck());
        savedSecondPackages = packageRepository.save(secondBoxPackToTruck());
        savedThirdPackage = packageRepository.save(thirdBoxPackToTruck());
        uuids = new ArrayList<>();
        packageRepository.flush();
    }

    @AfterEach
    void cleanUpDatabase() {
        try {
            packageRepository.deleteAll();
            packageRepository.flush();
            cargoSpaceRepository.deleteAllInBatch();
            truckRepository.deleteAll();
            truckRepository.flush();
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    void shouldReturnCorrectAddPackToTruck() {
        uuids.add(thirdBoxPackToTruck().getUuid());

        parcelPackingService.pack(uuids, savedTruck); // <- ten sam, zarządzany truck z setUp()

        Truck reloaded = truckRepository.findTruckByUuid(savedTruck.getUuid()).orElseThrow();

        assertThat(reloaded.getPackedPackages())
                .extracting("uuid")
                .contains(thirdBoxPackToTruck().getUuid());

        assertThat(reloaded.getPackedPackages()).hasSize(1);
    }

    @Test
    void shouldPackMultiplePackagesInOneCall() {
        uuids.add(thirdBoxPackToTruck().getUuid());
        uuids.add(secondBoxPackToTruck().getUuid());

        parcelPackingService.pack(uuids, savedTruck);

        Truck reloaded = truckRepository.findTruckByUuid(savedTruck.getUuid()).orElseThrow();

        assertThat(reloaded.getPackedPackages())
                .extracting(Package::getUuid)
                .containsExactlyInAnyOrder(savedSecondPackages.getUuid(), savedThirdPackage.getUuid());
    }

    @Test
    void shouldRejectLoadingPackageOntoDifferentTruckWhenAlreadyLoaded() {
        Truck otherTruck = truckRepository.save(createLoadedSecondTruck());
        uuids.add(firstBoxPackToTruck().getUuid());

        parcelPackingService.pack(uuids, otherTruck);

        assertThatThrownBy(() -> parcelPackingService.pack(uuids, otherTruck))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining(savedFirstPackage.getTrackingNumber());
    }


    @Test
    void shouldThrowExceptionWhenListUUIDIsNull() {
        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class, () -> parcelPackingService.pack(uuids, savedTruck));
        Assertions.assertEquals("The list of packages to be loaded cannot be empty!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenOnePackageNotExistsInDb() {
        uuids.add(thirdBoxPackToTruck().getUuid());
        uuids.add(filterPack().getUuid());

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class, () -> parcelPackingService.pack(uuids, savedTruck));

        Assertions.assertEquals("Unable to load: One or more packages do not exist in the database!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenProvidedPackageIsLoadedInTheTruck() {
        uuids.add(savedThirdPackage.getUuid());

        parcelPackingService.pack(uuids, savedTruck);

        assertThatThrownBy(() -> parcelPackingService.pack(uuids, savedTruck))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining(savedThirdPackage.getTrackingNumber())
                .hasMessageContaining(savedTruck.getTruckId().toString());
    }

    @Test
    void shouldSortedPackFromMaxToMin() {
        uuids.add(savedThirdPackage.getUuid());
        uuids.add(savedSecondPackages.getUuid());

        parcelPackingService.pack(uuids, savedTruck);

        Truck reloaded = truckRepository.findTruckByUuid(savedTruck.getUuid()).orElseThrow();

        assertThat(reloaded.getPackedPackages())
                .extracting(Package::getUuid)
                .containsExactly(savedSecondPackages.getUuid(), savedThirdPackage.getUuid());
    }

    @Test
    void shouldThrowExceptionWhenPackageDoesNotExist() {
        uuids.add(UUID.randomUUID()); // ID, którego nie ma w bazie

        assertThatThrownBy(() -> parcelPackingService.pack(uuids, savedTruck))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldNotModifyTruckWhenPackingFails() {
        uuids.add(savedThirdPackage.getUuid());
        parcelPackingService.pack(uuids, savedTruck);

        try {
            parcelPackingService.pack(uuids, savedTruck); // druga próba, ma się nie udać
        } catch (ResourceAlreadyExistsException ignored) {
        }

        Truck reloaded = truckRepository.findTruckByUuid(savedTruck.getUuid()).orElseThrow();

        // paczka nie mogła zostać dopisana drugi raz do tej samej ciężarówki
        assertThat(reloaded.getPackedPackages())
                .extracting(Package::getUuid)
                .containsOnlyOnce(savedThirdPackage.getUuid());
    }

    @Test
    void shouldThrowExceptionWhenTruckHasNoAvailableCargoSpaces() {
        Truck emptyTruck = truckRepository.save(createTruckWithoutCargoSpaces());
        uuids.add(savedThirdPackage.getUuid());

        assertThatThrownBy(() -> parcelPackingService.pack(uuids, emptyTruck))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining(emptyTruck.getTruckId().toString())
                .hasMessageContaining("no valid available CargoSpaces");
    }

    @Test
    void shouldThrowExceptionWhenNoCargoSpaceFitsThePackage() {
        Package oversized = packageRepository.save(createOverSizeedPackage());
        uuids.add(oversized.getUuid());

        assertThatThrownBy(() -> parcelPackingService.pack(uuids, savedTruck))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("1 package(s)")
                .hasMessageContaining(oversized.getUuid().toString());
    }
}