package com.io.logiflow.packageConsumer.infrastructure.service.integration;

import com.io.logiflow.packageConsumer.infrastructure.dao.Package;
import com.io.logiflow.packageConsumer.infrastructure.dao.Truck;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.CreateTruckDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.PackingRequestDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.TruckFilterDto;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.PackingResultDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.TruckDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.TruckResponseDTO;
import com.io.logiflow.packageConsumer.infrastructure.repository.PackageRepository;
import com.io.logiflow.packageConsumer.infrastructure.repository.TruckRepository;
import com.io.logiflow.packageConsumer.infrastructure.exception.ResourceAlreadyExistsException;
import com.io.logiflow.packageConsumer.infrastructure.exception.ResourceNotFoundException;
import com.io.logiflow.packageConsumer.infrastructure.controllers.integrations.AbstractIntegrationTest;
import com.io.logiflow.packageConsumer.application.service.TruckService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.*;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedSecondTruck;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedTruck;
import static com.io.logiflow.packageConsumer.domain.dto.request.CreateTruckDTOFixture.*;
import static com.io.logiflow.packageConsumer.domain.dto.request.PackingRequestDTOFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

public class TruckServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TruckRepository truckRepository;

    @Autowired
    private PackageRepository packageRepository;

    TruckFilterDto filter = new TruckFilterDto();

    Package savedFirstPackage;
    Package savedSecondPackage;

    Truck savedFirstTruck;
    Truck savedSecondTruck;
    List<UUID> uuids;


    @Autowired
    private TruckService truckService;

    @BeforeEach
    public void setUp() {
        savedFirstTruck = truckRepository.save(createLoadedTruck());
        savedSecondTruck = truckRepository.save(createLoadedSecondTruck());

        savedFirstPackage = packageRepository.save(firstBoxPackToTruck());
        savedSecondPackage = packageRepository.save(secondBoxPackToTruck());
        uuids = new ArrayList<>();
    }

    @AfterEach
    void cleanUpDatabase() {
        truckRepository.deleteAll();
        packageRepository.deleteAll();
    }

    @Autowired
    private Environment environment;

    @Test
    void checkLiquibaseContext() {
        System.out.println(
                "Liquibase contexts = " +
                        environment.getProperty("spring.liquibase.contexts")
        );
    }


    @Test
    void shouldReturnAllTruck() {
        int page = 1;
        int size = 5;

        Page<TruckDTO> result = truckService.getTrucks(filter, page, size);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(TruckDTO::getIdTruck)
                .containsExactly(UUID.fromString("528e0004-a6f9-4018-bcba-87e20ad08606"),
                        UUID.fromString("c771f079-fb74-4811-b491-e78673c41e0d"));

        assertThat(result.getNumber()).isEqualTo(0);          // numer bieżącej strony
        assertThat(result.getSize()).isEqualTo(5);            // żądany rozmiar strony
        assertThat(result.getTotalElements()).isEqualTo(2);   // wszystkich rekordów w bazie
        assertThat(result.isLast()).isTrue();
    }

    @Test
    void shouldReturnOnlyTruckWhichUUIDProvided() {
        int page = 1;
        int size = 5;
        filter.setUuid(UUID.fromString("c771f079-fb74-4811-b491-e78673c41e0d"));

        Page<TruckDTO> result = truckService.getTrucks(filter, page, size);

        assertThat(result.getContent())
                .filteredOn(dto -> dto.getIdTruck().toString().equals("c771f079-fb74-4811-b491-e78673c41e0d"))
                .singleElement()
                .satisfies(dto -> {
                            assertThat(dto.getDimensionsDTO().getHeight()).isEqualTo(190);
                            assertThat(dto.getDimensionsDTO().getWidth()).isEqualTo(178);
                            assertThat(dto.getMaxPayload().equals(new BigDecimal(200)));
                        }
                );
    }

    @Test
    void shouldReturnTruckBetweenMinMaxLength() {
        int page = 1;
        int size = 5;
        filter.setMaxLength(600);
        filter.setMinLength(400);

        Page<TruckDTO> result = truckService.getTrucks(filter, page, size);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(TruckDTO::getIdTruck)
                .containsExactly(UUID.fromString("528e0004-a6f9-4018-bcba-87e20ad08606"),
                        UUID.fromString("c771f079-fb74-4811-b491-e78673c41e0d"));
    }


    @Test
    void shouldAddPackagesToTruck() {
        PackingRequestDTO toPack = thirdPackingToPack();
        packageRepository.save(thirdBoxPackToTruck());

        PackingResultDTO result = truckService.addPackagesToTruck(toPack);

        Assertions.assertEquals(UUID.fromString("d81f3a6c-5e97-4b2d-8c04-f7a2b9e1d365"), result.packedPackageUuids().getFirst());
    }

    @Test
    void shouldThrowExceptionWhenTruckDoesNotExist() {
        PackingRequestDTO randomUUID = randomUUIDTruck();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> truckService.addPackagesToTruck(randomUUID));
        Assertions.assertEquals("The truck with the id " + randomUUID.truckId() + " does not exist", exception.getMessage());
    }

    @Test
    void shouldReturnAddedTruck() {
        // given
        CreateTruckDTO thirdTruck = createThirdTruck();

        // when
        TruckResponseDTO addedTruck = truckService.createTruck(thirdTruck);

        // then

        assertThat(thirdTruck.maxPayload()).isEqualTo(addedTruck.maxPayload());
        assertThat(thirdTruck.licensePlate()).isEqualTo("KR 12345");
    }

    @Test
    void shouldThrowExceptionWhenProvidedParameterWeightIsNull() {
        CreateTruckDTO emptyWeight = createEmptyWeightTruck();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> truckService.createTruck(emptyWeight));
        Assertions.assertEquals("Dimensions and weight are required", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenOnesProvidedParameterIsNegative() {
        CreateTruckDTO negativeWidthTruck = createNegativeWidthTruck();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> truckService.createTruck(negativeWidthTruck));
        Assertions.assertEquals("Exterior width must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenProvidedLicensePlateIsNull() {
        CreateTruckDTO createTruck = createNullLicensePlateTruck();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> truckService.createTruck(createTruck));
        Assertions.assertEquals("license_plate is required", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenProvidedLicensePlateIsExists() {
        ResourceAlreadyExistsException exception = Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> truckService.createTruck(createFirstTruck()));
        Assertions.assertEquals("The truck with license plate WI 1234E already exists", exception.getMessage());
    }

    @Test
    void shouldReturnCorrectAddPackToTruck() {
        packageRepository.save(thirdBoxPackToTruck());
        uuids.add(thirdBoxPackToTruck().getUuid());
        truckService.addPackagesToTruck(thirdPackingToPack());

        List<Package> packagesOnTruck = packageRepository.findAll().stream()
                .filter(p -> p.getTruck() != null)
                .filter(p -> p.getTruck().getUuid().equals(savedFirstTruck.getUuid()))
                .toList();

        assertThat(packagesOnTruck)
                .extracting(Package::getUuid)
                .contains(thirdBoxPackToTruck().getUuid());

        assertThat(packagesOnTruck).hasSize(1);
    }

    @Test
    void shouldPackMultiplePackagesInOneCall() {
        packageRepository.save(packageNotSaveInDb());
        uuids.add(UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d"));
        uuids.add(UUID.fromString("7f3a2b1c-9d84-4e6f-b521-8a7c3d2e1f90"));

        PackingResultDTO result = truckService.addPackagesToTruck(multiPacked());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.packedPackageUuids().size());
    }
}

