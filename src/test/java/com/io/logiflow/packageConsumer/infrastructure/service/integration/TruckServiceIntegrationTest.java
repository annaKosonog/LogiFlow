package com.io.logiflow.packageConsumer.infrastructure.service.integration;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.request.PackingRequestDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.TruckDTO;
import com.io.logiflow.packageConsumer.domain.dto.request.TruckFilterDto;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.domain.repository.TruckRepository;
import com.io.logiflow.packageConsumer.exception.ResourceNotFoundException;
import com.io.logiflow.packageConsumer.infrastructure.controllers.integrations.AbstractIntegrationTest;
import com.io.logiflow.packageConsumer.infrastructure.service.TruckService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.firstBoxPackToTruck;
import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.thirdBoxPackToTruck;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedSecondTruck;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedTruck;
import static com.io.logiflow.packageConsumer.domain.dto.request.PackingRequestDTOFixture.randomUUIDTruck;
import static com.io.logiflow.packageConsumer.domain.dto.request.PackingRequestDTOFixture.thirdPackingToPack;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

public class TruckServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TruckRepository truckRepository;

    @Autowired
    private PackageRepository packageRepository;

    TruckFilterDto filter = new TruckFilterDto();

    Package savedFirstPackage;
    Package savedThirdPackage;

    Truck savedFirstTruck;
    Truck savedSecondTruck;


    @Autowired
    private TruckService truckService;

    @BeforeEach
    public void setUp() {
        packageRepository.deleteAll();
        truckRepository.deleteAll();

        savedFirstPackage = packageRepository.save(firstBoxPackToTruck());
        savedThirdPackage = packageRepository.save(thirdBoxPackToTruck());

        savedFirstTruck = truckRepository.save(createLoadedTruck());
        savedSecondTruck = truckRepository.save(createLoadedSecondTruck());
        packageRepository.flush();
        truckRepository.flush();
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
        truckService.addPackagesToTruck(toPack);

        Truck reloaded = truckRepository.findTruckByUuid(savedFirstTruck.getUuid())
                .orElseThrow(() -> new AssertionError("Nie znaleziono ciężarówki o UUID: " + savedThirdPackage.getUuid()));

        assertThat(reloaded.getPackedPackages())
                .extracting(Package::getUuid)
                .contains(savedThirdPackage.getUuid());
    }

    @Test
    void shouldThrowExceptionWhenTruckDoesNotExist() {
        PackingRequestDTO randomUUID = randomUUIDTruck();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> truckService.addPackagesToTruck(randomUUID));
        Assertions.assertEquals("The truck with the id " + randomUUID.truckId() + " does not exist", exception.getMessage());
    }
}

