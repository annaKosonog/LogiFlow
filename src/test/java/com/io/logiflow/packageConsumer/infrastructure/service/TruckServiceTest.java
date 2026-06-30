package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.domain.dto.TruckDTO;
import com.io.logiflow.packageConsumer.domain.mapper.TruckMapper;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.domain.repository.TruckRepository;
import com.io.logiflow.packageConsumer.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.firstBoxPackToTruck;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedTruck;
import static com.io.logiflow.packageConsumer.domain.dto.PackageFixtureDTO.firstBoxPackDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TruckServiceTest {

    @Mock
    private TruckRepository truckRepository;

    @Mock
    private ParcelPackingService parcelPackingService;

    @Mock
    private TruckMapper truckMapper;

    @Mock
    private PackageRepository packageRepository;

    @InjectMocks
    private TruckService truckService;

    private Truck truck;

    private List<PackageDTO> aPackages;

    @BeforeEach
    void setUp() {
        truck = createLoadedTruck();
        lenient().when(truckRepository.findById(1L)).thenReturn(Optional.of(truck));
        aPackages = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        truck = null;
        aPackages = null;
    }

    @Test
    void shouldReturnPaginatedTrucks() {
        int pageNumber = 0;
        int pageSize = 1;

        PageRequest pageable = PageRequest.of(pageNumber, pageSize);

        Truck savedTruck = new Truck();
        List<Truck> truckList = List.of(savedTruck);

        PageImpl<Truck> mockPageFromDb = new PageImpl<>(truckList, pageable, 1);

        TruckDTO dto = new TruckDTO();

        when(truckRepository.findAll(pageable)).thenReturn(mockPageFromDb);
        when(truckMapper.toDTO(savedTruck)).thenReturn(dto);

        Page<TruckDTO> resultTrucks = truckService.getTrucks(pageNumber, pageSize);

        assertNotNull(resultTrucks);
        assertEquals(1, resultTrucks.getTotalElements(), "Total elements in page should be 1");
        assertEquals(1, resultTrucks.getContent().size(), "Page content size should match mapped elements count");
        assertEquals(dto, resultTrucks.getContent().get(0), "The content should contain the mapped DTO");
    }

    @Test
    void shouldThrowExceptionWhenPageSizeIsInvalid() {
        int pageNumber = 0;
        int pageSize = 0;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> truckService.getTrucks(pageNumber, pageSize));

        assertEquals("Page size must not be less than one", exception.getMessage());
        verifyNoInteractions(truckRepository);
    }

    @Test
    void shouldReturnEmptyPageWhenNoTrucksExist() {
        int page = 15;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);

        Page<Truck> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(truckRepository.findAll(pageable)).thenReturn(emptyPage);
        Page<TruckDTO> resultPage = truckService.getTrucks(page, size);

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty(), "The returned page should be empty");
        assertEquals(0, resultPage.getTotalElements(), "Total elements should be 0");
        assertEquals(0, resultPage.getContent().size(), "Content list size should be 0");

        // Upewniamy się, że mapper nigdy nie został wywołany (bo nie było czego mapować)
        verifyNoInteractions(truckMapper);
    }

    @Test
    void shouldThrowExceptionWhenTruckIdNotExists() {
        Long truckId = 5L;

        when(truckRepository.findById(truckId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> truckService.addPackagesToTruck(aPackages, truckId));

        assertEquals("The truck with the id " + truckId + " does not exist", exception.getMessage());
    }

    @Test
    void shouldReturnPackagesWhichAddToTruck() {
        Long truckId = 1L;
        List<Package> uuidList = new ArrayList<>();
        uuidList.add(firstBoxPackToTruck());
        aPackages.add(firstBoxPackDto());

        truck.setPackedPackages(uuidList);

        truckService.addPackagesToTruck(aPackages, truckId);

        assertEquals(1, truck.getPackedPackages().size());
    }

}