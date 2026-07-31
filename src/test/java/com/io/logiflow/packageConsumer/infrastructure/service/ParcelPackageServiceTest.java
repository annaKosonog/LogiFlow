package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.domain.mapper.PackageMapper;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.domain.repository.TruckRepository;
import com.io.logiflow.packageConsumer.exception.PackageValidationException;
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

import static com.io.logiflow.packageConsumer.domain.dao.CargoSpaceFixture.mediumSpace;
import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.*;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedTruck;
import static com.io.logiflow.packageConsumer.domain.dto.PackageFixtureDTO.*;
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
    private PackageMapper packageMapper;
    @Mock
    private TruckRepository truckRepository;

    @Mock
    private PackageRepository packageRepository;

    private List<Package> saveEntity;

    @BeforeEach
    void setUp() {
        truck = createLoadedTruck();
        lenient().when(truckRepository.findById(1L)).thenReturn(Optional.of(truck));
        aPackages = new ArrayList<>();
        saveEntity = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        parcelPackingService = null;
        truck = null;
        aPackages = null;
    }

    @Test
    void testShouldPackSingleBoxCorrectly() {
        // Given
        saveEntity.add(firstBoxPackToTruck());
        when(packageRepository.saveAll(anyList())).thenReturn(saveEntity);
        when(packageRepository.findAllByUuidIn(any())).thenReturn(saveEntity);
        aPackages.add(firstBoxPackDto());

        // When
        parcelPackingService.pack(aPackages, truck);

        // Then
        assertEquals(0, aPackages.get(0).getCoordinates().getX());
        assertEquals(0, aPackages.get(0).getCoordinates().getY());
        assertEquals(0, aPackages.get(0).getCoordinates().getZ());
    }

    @Test
    void shouldPackAllBoxesIntoTruck() {
        saveEntity.add(firstBoxPackToTruck());
        saveEntity.add(secondBoxPackToTruck());

        when(packageRepository.saveAll(anyList())).thenReturn(saveEntity);
        when(packageRepository.findAllByUuidIn(any())).thenReturn(saveEntity);
        aPackages.add(firstBoxPackDto());
        aPackages.add(secondBoxPackDto());

        parcelPackingService.pack(aPackages, truck);
        assertEquals(2, aPackages.size());
    }

    @Test
    void testShouldNotPackOverweightBox() {
        BigDecimal targetWeight = new BigDecimal("121.00");

        Package heavyBox = heavyBox();
        heavyBox.setWeight(targetWeight);
        saveEntity.add(heavyBox);

        when(packageRepository.findAllByUuidIn(any())).thenReturn(saveEntity);

        PackageDTO heavyBoxDTO = heavyBoxDTO();
        heavyBoxDTO.setWeight(targetWeight);
        aPackages.add(heavyBoxDTO);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> parcelPackingService.pack(aPackages, truck));
        // Then
        assertEquals("Truck loading failed", exception.getMessage());
        verify(truckRepository, never()).save(any(Truck.class));
    }

    @Test
    void shouldSortBoxesByVolumeAscendingBeforePacking() {
        // 1. Pobieramy wejściową, nieposortowaną listę DTO z fixture: [Large, Small, Medium]
        List<PackageDTO> localPackages = sortedPackagesDTO();
        List<Package> simulatedSortedEntities = sortedPackages();

        // WHEN\
        when(packageRepository.saveAll(anyList())).thenReturn(simulatedSortedEntities);
        when(packageRepository.findAllByUuidIn(any())).thenReturn(simulatedSortedEntities);

        parcelPackingService.pack(localPackages, truck);

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
        List<PackageDTO> localPackages = sortedPackagesDTOByWeightDescending();
        List<Package> simulatedSortedEntities = sortedPackagesDaoByWeightDescending();

        when(packageRepository.saveAll(anyList())).thenReturn(simulatedSortedEntities);
        when(packageRepository.findAllByUuidIn(any())).thenReturn(simulatedSortedEntities);

        parcelPackingService.pack(localPackages, truck);
        List<Package> packedPackages = truck.getPackedPackages();

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
        PackageDTO packageDTO = boxThatFitsExactlyDto();
        saveEntity.add(packageBox);
        aPackages.add(packageDTO);

        when(packageRepository.saveAll(anyList())).thenReturn(saveEntity);
        when(packageRepository.findAllByUuidIn(any())).thenReturn(saveEntity);

        parcelPackingService.pack(aPackages, truck);
        assertEquals(4, truck.getPackedPackages().size());
    }

    @Test
    void shouldNotPackBoxWhenTooWide() {
        PackageDTO largeBoxDTO = largeBoxDTO();
        Package largePackage = largeBox();
        saveEntity.add(largePackage);

        when(packageRepository.saveAll(anyList())).thenReturn(saveEntity);
        when(packageRepository.findAllByUuidIn(any())).thenReturn(saveEntity);
        aPackages.add(largeBoxDTO);

        PackageValidationException exception = assertThrows(PackageValidationException.class, () -> parcelPackingService.pack(aPackages, truck));
        assertEquals(3, truck.getPackedPackages().size());
    }

    @Test
    void shouldNotPackBoxWhenTooTall() {
        PackageDTO heightBoxDTO = heightBoxDTO();
        Package heightPackage = heightBox();
        aPackages.add(heightBoxDTO);
        saveEntity.add(heightPackage);

        when(packageRepository.saveAll(anyList())).thenReturn(saveEntity);
        when(packageRepository.findAllByUuidIn(any())).thenReturn(saveEntity);

        PackageValidationException exception = assertThrows(PackageValidationException.class, () -> parcelPackingService.pack(aPackages, truck));
        assertEquals(3, truck.getPackedPackages().size());
        assertThat(exception);
    }

    @Test
    void shouldNotPackBoxWhenTooDeep() {
        Package deepthPackage = depthBox();
        PackageDTO depthBoxDTO = depthBoxDTO();
        aPackages.add(depthBoxDTO);
        saveEntity.add(deepthPackage);

        when(packageRepository.saveAll(anyList())).thenReturn(saveEntity);
        when(packageRepository.findAllByUuidIn(any())).thenReturn(saveEntity);

        PackageValidationException exception = assertThrows(PackageValidationException.class, () -> parcelPackingService.pack(aPackages, truck));
        assertEquals(3, truck.getPackedPackages().size());
        assertFalse(truck.getPackedPackages().contains(deepthPackage));
    }

    @Test
    void shouldThrowExceptionWhenPackageListIsNull() {
        PackageValidationException exception = assertThrows(PackageValidationException.class, () -> parcelPackingService.pack(aPackages, truck));
        assertEquals("The list of packages to be loaded cannot be empty!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSizeAreDifferent() {
        PackageDTO packageDTO = smallDTO();
        aPackages.add(packageDTO);

        when(packageRepository.findAllByUuidIn(any())).thenReturn(saveEntity);

        PackageValidationException exception = assertThrows(PackageValidationException.class, () -> parcelPackingService.pack(aPackages, truck));

        assertEquals("Unable to load: One or more packages do not exist in the database!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPackageIsAlreadyLoaded() {
        PackageDTO packageDTO = smallDTO();
        Package smallDao = smallDaoSaved();

        aPackages.add(packageDTO);
        saveEntity.add(smallDao());

        when(packageRepository.saveAll(anyList())).thenReturn(saveEntity);
        when(packageRepository.findAllByUuidIn(any())).thenReturn(saveEntity);
        parcelPackingService.pack(aPackages, truck);


        PackageValidationException exception = assertThrows(PackageValidationException.class, () -> parcelPackingService.pack(aPackages, truck));
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
