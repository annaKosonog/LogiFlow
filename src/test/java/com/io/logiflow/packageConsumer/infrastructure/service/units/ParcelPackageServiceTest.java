package com.io.logiflow.packageConsumer.infrastructure.service.units;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.response.PackageDTO;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.domain.repository.TruckRepository;
import com.io.logiflow.packageConsumer.exception.PackageValidationException;
import com.io.logiflow.packageConsumer.infrastructure.service.ParcelPackingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.CargoSpaceFixture.mediumSpace;
import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.*;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedTruck;
import static com.io.logiflow.packageConsumer.domain.dto.response.PackageFixtureDTO.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParcelPackageServiceTest {

    @InjectMocks
    private ParcelPackingService parcelPackingService;
    private Truck truck;
    private List<PackageDTO> aPackages;

    @Mock
    private TruckRepository truckRepository;

    @Mock
    private PackageRepository packageRepository;

    private List<Package> saveEntity;

    private List<UUID> uuids;

    @BeforeEach
    void setUp() {
        truck = createLoadedTruck();
        lenient().when(truckRepository.findById(1L)).thenReturn(Optional.of(truck));
        aPackages = new ArrayList<>();
        uuids = new ArrayList<>();
        saveEntity = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        parcelPackingService = null;
        truck = null;
        aPackages = null;
        uuids = null;
    }

    @Test
    void testShouldPackSingleBoxCorrectly() {
        // Given
        saveEntity.add(thirdBoxPackToTruck());
        when(packageRepository.saveAll(anyList())).thenReturn(saveEntity);
        when(packageRepository.findAllByUuidIn(any())).thenReturn(saveEntity);


        // When
        parcelPackingService.pack(List.of(thirdBoxPackToTruck().getUuid()), createLoadedTruck());

        // Then
        assertEquals(3, createLoadedTruck().getPackedPackages().size());
        assertThat(UUID.fromString("f7f4b7d2-660c-4430-bcec-ce3f9dd32c36").equals(thirdBoxPackToTruck().getUuid()));
    }

    @Test
    void shouldPackAllBoxesIntoTruck() {
        uuids.add(firstBoxPackToTruck().getUuid());
        uuids.add(secondBoxPackToTruck().getUuid());

        saveEntity.add(firstBoxPackToTruck());
        saveEntity.add(secondBoxPackToTruck());

        when(packageRepository.saveAll(anyList())).thenReturn(saveEntity);
        when(packageRepository.findAllByUuidIn(any())).thenReturn(saveEntity);

        parcelPackingService.pack(uuids, truck);
        assertEquals(5, truck.getPackedPackages().size());
    }

    @Test
    void testShouldNotPackOverweightBox() {
        BigDecimal targetWeight = new BigDecimal("121.00");

        Package heavyBox = heavyBox();
        heavyBox.setWeight(targetWeight);
        saveEntity.add(heavyBox);
        uuids.add(heavyBox.getUuid());

        when(packageRepository.findAllByUuidIn(any())).thenReturn(saveEntity);

        PackageDTO heavyBoxDTO = heavyBoxDTO();
        heavyBoxDTO.setWeight(targetWeight);
        aPackages.add(heavyBoxDTO);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> parcelPackingService.pack(uuids, truck));
        // Then
        assertEquals("Truck loading failed", exception.getMessage());
        verify(truckRepository, never()).save(any(Truck.class));
    }

    @Test
    void shouldSortBoxesByVolumeAscendingBeforePacking() {
        // 1. Pobieramy wejściową, nieposortowaną listę DTO z fixture: [Large, Small, Medium]
        List<Package> simulatedSortedEntities = sortedPackages();
        uuids.add(sortedPackages().get(0).getUuid());
        uuids.add(sortedPackages().get(1).getUuid());
        uuids.add(sortedPackages().get(2).getUuid());

        // WHEN
        when(packageRepository.saveAll(anyList())).thenReturn(simulatedSortedEntities);
        when(packageRepository.findAllByUuidIn(any())).thenReturn(simulatedSortedEntities);

        parcelPackingService.pack(uuids, truck);

        // THEN
        List<Package> packedPackages = truck.getPackedPackages();

        // Filtrujemy tylko paczki zaczynające się od "XYZ-" (nowo dodane) i sprawdzamy ich kolejność
        assertThat(packedPackages)
                .filteredOn(p -> p.getTrackingNumber() != null && p.getTrackingNumber().startsWith("XYZ-"))
                .extracting(Package::getWidth, Package::getWeight)
                .containsExactly(
                        tuple(5, new BigDecimal("30")), // 3. LARGE  (szerokość 5, waga 30.0)
                        tuple(2, new BigDecimal("20")), // 2. MEDIUM (szerokość 2, waga 30.0)
                        tuple(1, new BigDecimal("10")) // 1. SMALL  (szerokość 1, waga 10.0)
                );
    }

    @Test
    void shouldSortBoxesWithSameVolumeByWeightDescending() {
        List<Package> simulatedSortedEntities = sortedPackagesDaoByWeightDescending();
        uuids.add(simulatedSortedEntities.get(0).getUuid());
        uuids.add(simulatedSortedEntities.get(1).getUuid());

        when(packageRepository.saveAll(anyList())).thenReturn(simulatedSortedEntities);
        when(packageRepository.findAllByUuidIn(any())).thenReturn(simulatedSortedEntities);

        parcelPackingService.pack(uuids, truck);
        truck.getPackedPackages();

        List<BigDecimal> newWeightsSorted = truck.getPackedPackages().stream()
                .map(Package::getWeight)
                .filter(w -> w.compareTo(new BigDecimal("20")) == 0 || w.compareTo(new BigDecimal("5")) == 0)
                .toList();

        assertThat(newWeightsSorted).containsExactly(
                new BigDecimal("20"),
                new BigDecimal("5"));
    }

    @Test
    void shouldPackBoxWhenItFitsExactlyInSpace() {
        Package packageBox = boxThatFitsExactly(mediumSpace());

        uuids.add(packageBox.getUuid());

        PackageDTO packageDTO = boxThatFitsExactlyDto();
        saveEntity.add(packageBox);
        aPackages.add(packageDTO);

        when(packageRepository.saveAll(anyList())).thenReturn(saveEntity);
        when(packageRepository.findAllByUuidIn(any())).thenReturn(saveEntity);

        parcelPackingService.pack(uuids, truck);
        assertEquals(4, truck.getPackedPackages().size());
    }

    @Test
    void shouldNotPackBoxWhenTooWide() {
        Package largePackage = largeBox();
        uuids.add(largePackage.getUuid());
        saveEntity.add(largePackage);

        when(packageRepository.saveAll(anyList())).thenReturn(saveEntity);
        when(packageRepository.findAllByUuidIn(any())).thenReturn(saveEntity);


        PackageValidationException exception = assertThrows(PackageValidationException.class, () -> parcelPackingService.pack(uuids, truck));
        assertEquals(3, truck.getPackedPackages().size());
    }

    @Test
    void shouldNotPackBoxWhenTooTall() {
        Package heightPackage = heightBox();
        uuids.add(heightPackage.getUuid());
        saveEntity.add(heightPackage);

        when(packageRepository.saveAll(anyList())).thenReturn(saveEntity);
        when(packageRepository.findAllByUuidIn(any())).thenReturn(saveEntity);

        PackageValidationException exception = assertThrows(PackageValidationException.class, () -> parcelPackingService.pack(uuids, truck));
        assertEquals(3, truck.getPackedPackages().size());
        assertThat(exception);
    }

    @Test
    void shouldNotPackBoxWhenTooDeep() {
        Package deepthPackage = depthBox();
        PackageDTO depthBoxDTO = depthBoxDTO();
        uuids.add(deepthPackage.getUuid());
        saveEntity.add(deepthPackage);

        when(packageRepository.saveAll(anyList())).thenReturn(saveEntity);
        when(packageRepository.findAllByUuidIn(any())).thenReturn(saveEntity);

        PackageValidationException exception = assertThrows(PackageValidationException.class, () -> parcelPackingService.pack(uuids, truck));
        assertEquals(3, truck.getPackedPackages().size());
        assertFalse(truck.getPackedPackages().contains(deepthPackage));
    }

    @Test
    void shouldThrowExceptionWhenPackageListIsNull() {
        PackageValidationException exception = assertThrows(PackageValidationException.class, () -> parcelPackingService.pack(uuids, truck));
        assertEquals("The list of packages to be loaded cannot be empty!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSizeAreDifferent() {
        uuids.add(smallDao().getUuid());
        uuids.add(thirdBoxPackToTruck().getUuid());

        when(packageRepository.findAllByUuidIn(any())).thenReturn(saveEntity);

        PackageValidationException exception = assertThrows(PackageValidationException.class, () -> parcelPackingService.pack(uuids, truck));

        assertEquals("Unable to load: One or more packages do not exist in the database!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPackageIsAlreadyLoaded() {
        Package smallDao = smallDaoSaved();
        uuids.add(smallDao.getUuid());

        saveEntity.add(smallDao());

        when(packageRepository.saveAll(anyList())).thenReturn(saveEntity);
        when(packageRepository.findAllByUuidIn(any())).thenReturn(saveEntity);
        parcelPackingService.pack(uuids, truck);


        PackageValidationException exception = assertThrows(PackageValidationException.class, () -> parcelPackingService.pack(uuids, truck));
        assertEquals("The package with the number " + smallDao.getTrackingNumber() +
                " has already been loaded onto the truck with ID: " + smallDao.getTruck().getTruckId(), exception.getMessage());
    }

    @Test
    void boxesOverlap_shouldReturnFalse_whenBoxesAreSeparatedOnXAxis() {
        Package a = boxAt(0, 0, 0, "30.00");
        Package b = boxAt(300, 0, 0, "50.00");

        boolean result = ReflectionTestUtils.invokeMethod(parcelPackingService, "boxesOverlap", a, b);

        assertThat(result).isFalse();
    }

}
