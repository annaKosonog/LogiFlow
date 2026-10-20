package com.io.logiflow.packageConsumer.infrastructure.service.parcel_preparation;

import com.io.logiflow.packageConsumer.domain.CargoGrid;
import com.io.logiflow.packageConsumer.domain.PackingRequestValidator;
import com.io.logiflow.packageConsumer.domain.ParcelPlacementFinder;
import com.io.logiflow.packageConsumer.application.service.TruckPackingExecutor;
import com.io.logiflow.packageConsumer.infrastructure.dao.Package;
import com.io.logiflow.packageConsumer.infrastructure.dao.Truck;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.PackingRequestDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.ParcelInformation;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.ProcessFailedRequestDto;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.CoordinatesDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.PackingResultDTO;
import com.io.logiflow.packageConsumer.infrastructure.mapper.ProcessFailedMapper;
import com.io.logiflow.packageConsumer.infrastructure.repository.PackageRepository;
import com.io.logiflow.packageConsumer.infrastructure.repository.ProcessFailedRepository;
import com.io.logiflow.packageConsumer.infrastructure.repository.TruckRepository;
import com.io.logiflow.packageConsumer.infrastructure.exception.ResourceNotFoundException;
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
import java.util.Optional;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.*;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedSecondTruck;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedTruck;
import static com.io.logiflow.packageConsumer.domain.dto.request.PackingRequestDTOFixture.*;
import static com.io.logiflow.packageConsumer.domain.dto.request.ProcessFailedRequestDTOFixture.onlyOneElementDoNotFits;
import static com.io.logiflow.packageConsumer.domain.dto.response.CoordinatesDTOFixture.firstCoordinate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TruckPackingExecutorTest {

    @Mock
    TruckRepository truckRepository;

    @Mock
    PackageRepository packageRepository;

    @Mock
    ProcessFailedRepository processFailedRepository;

    @Mock
    ProcessFailedMapper processFailedMapper;

    @Mock
    ParcelPlacementFinder placementFinder;

    @Mock
    PackingRequestValidator packingRequestValidator;

    @InjectMocks
    TruckPackingExecutor truckPackingExecutor;

    PackingRequestDTO aFirstBoxToPack;
    PackingRequestDTO aSecondBoxToPack;
    Truck truck;

    Package package1;

    List<Package> packagesToPack;

    List<UUID> uuids;

    List<ParcelInformation> parcelInformation;

    CargoGrid grid;


    @BeforeEach
    void setUp() {
        truck = createLoadedTruck();
        aFirstBoxToPack = firstPackingToPack();
        aSecondBoxToPack = secondPackingToPack();
        package1 = firstBoxPackToTruck();
        uuids = new ArrayList<>();
        packagesToPack = new ArrayList<>();
        parcelInformation = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        truck = new Truck();
        uuids.clear();
    }


    @Test
    void shouldThrowExceptionWhenNotFoundTruckByUuid() {
        PackingRequestDTO truckUuidDoesNotExists = randomUUIDTruck();
        ResourceNotFoundException message = assertThrows(ResourceNotFoundException.class, () -> truckPackingExecutor.tryAddPackages(truckUuidDoesNotExists));
        assertEquals("The truck with the id " + truckUuidDoesNotExists.truckId() + " does not exist", message.getMessage());
    }

    @Test
    void shouldReturnOnePackageThatShouldBeAddPack() {
        packageRepository.save(firstBoxPackToTruck());
        UUID truckUuid = UUID.fromString("528e0004-a6f9-4018-bcba-87e20ad08606");
        UUID packageUuid = UUID.fromString("3a2b1c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d");
        uuids.add(packageUuid);
        packagesToPack.add(package1);

        when(truckRepository.findTruckByUuid(truckUuid)).thenReturn(Optional.of(truck));
        when(packingRequestValidator.resolveAndValidatePackages(anyList())).thenReturn(List.of(package1));
        when(placementFinder.findFreePosition(
                any(CargoGrid.class),
                eq(package1),
                eq(truck.getWidth()),
                eq(truck.getHeight()),
                eq(truck.getLength())
        )).thenReturn(Optional.of(firstCoordinate()));

        PackingResultDTO result = truckPackingExecutor.tryAddPackages(aFirstBoxToPack);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.packedPackageUuids().size());
    }

    @Test
    void shouldReturnMultiplyPackageThatShouldBeAddPack() {
        Package package2 = thirdBoxPackToTruck();
        UUID truckUuid = UUID.fromString("c771f079-fb74-4811-b491-e78673c41e0d");
        Truck loadedTruck = createLoadedSecondTruck();

        packagesToPack.add(package1);
        packagesToPack.add(package2);

        when(truckRepository.findTruckByUuid(truckUuid)).thenReturn(Optional.of(loadedTruck));
        when(packingRequestValidator.resolveAndValidatePackages(anyList())).thenReturn(packagesToPack);
        when(placementFinder.findFreePosition(
                any(CargoGrid.class),
                eq(package1),
                eq(loadedTruck.getWidth()),
                eq(loadedTruck.getHeight()),
                eq(loadedTruck.getLength())
        )).thenReturn(Optional.of(firstCoordinate()));

        when(placementFinder.findFreePosition(
                any(CargoGrid.class),
                eq(package2),
                eq(loadedTruck.getWidth()),
                eq(loadedTruck.getHeight()),
                eq(loadedTruck.getLength())
        )).thenReturn(Optional.of(new CoordinatesDTO(2, 0, 0)));


        PackingResultDTO result = truckPackingExecutor.tryAddPackages(multiPacked());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.packedPackageUuids().size());
    }

    @Test
    void shouldReturnPackagesThatPackedAndUnPacked() {
        Package package2 = secondBoxPackToTruck();
        Package package3 = thirdBoxPackToTruck();
        packagesToPack.add(package1);
        packagesToPack.add(package2);
        packagesToPack.add(package3);

        when(truckRepository.findTruckByUuid(any())).thenReturn(Optional.of(truck));
        when(packingRequestValidator.resolveAndValidatePackages(anyList())).thenReturn(packagesToPack);
        when(processFailedMapper.toEntityRequest(any(ProcessFailedRequestDto.class))).thenReturn(onlyOneElementDoNotFits());

        when(placementFinder.findFreePosition(
                any(CargoGrid.class),
                eq(package1),
                eq(truck.getWidth()),
                eq(truck.getHeight()),
                eq(truck.getLength())
        )).thenReturn(Optional.of(firstCoordinate()));

        when(placementFinder.findFreePosition(
                any(CargoGrid.class),
                eq(package2),
                eq(truck.getWidth()),
                eq(truck.getHeight()),
                eq(truck.getLength())
        )).thenReturn(Optional.empty());

        when(placementFinder.findFreePosition(
                any(CargoGrid.class),
                eq(package3),
                eq(truck.getWidth()),
                eq(truck.getHeight()),
                eq(truck.getLength())
        )).thenReturn(Optional.of(new CoordinatesDTO(2, 0, 0)));

        PackingResultDTO result = truckPackingExecutor.tryAddPackages(multiPacked());

        Assertions.assertNotNull(result);
        assertEquals(2, result.packedPackageUuids().size());
        assertEquals("173f5e17-c963-4ce9-8ebd-fa7f0f3bd940", result.processFailedUuid().toString());
    }
}