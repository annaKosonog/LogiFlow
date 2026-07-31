package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.domain.dto.TruckDTO;
import com.io.logiflow.packageConsumer.domain.dto.TruckFilterDto;
import com.io.logiflow.packageConsumer.domain.mapper.TruckMapper;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.domain.repository.TruckRepository;
import com.io.logiflow.packageConsumer.exception.ResourceNotFoundException;
import com.io.logiflow.packageConsumer.infrastructure.specification.TruckSpecifications;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.firstBoxPackToTruck;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedTruck;
import static com.io.logiflow.packageConsumer.domain.dto.PackageFixtureDTO.firstBoxPackDto;
import static org.junit.jupiter.api.Assertions.*;
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
        lenient().when(truckRepository.findTruckByUuid(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))).thenReturn(Optional.of(truck));
        aPackages = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        truck = null;
        aPackages = null;
    }

    @Test
    void shouldReturnPaginatedTrucks() {
        int pageNumber = 1;
        int pageSize = 1;

        PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        TruckFilterDto filter = new TruckFilterDto();
        Truck savedTruck = new Truck();
        List<Truck> truckList = List.of(savedTruck);

        PageImpl<Truck> mockPageFromDb = new PageImpl<>(truckList, pageable, 1);

        TruckDTO dto = new TruckDTO();

        when(truckRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockPageFromDb);

        when(truckMapper.toDTO(savedTruck)).thenReturn(dto);

        Page<TruckDTO> resultTrucks = truckService.getTrucks(filter, pageNumber, pageSize);

        assertNotNull(resultTrucks);
        assertEquals(2, resultTrucks.getTotalElements(), "Total elements in page should be 1");
        assertEquals(1, resultTrucks.getContent().size(), "Page content size should match mapped elements count");
        assertEquals(dto, resultTrucks.getContent().get(0), "The content should contain the mapped DTO");
    }

    @Test
    void shouldReturnEmptyPageWhenNoTrucksExist() {
        int page = 15;
        int size = 10;

        Page<Truck> emptyPage = Page.empty();

        when(truckRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        Page<TruckDTO> resultPage = truckService.getTrucks(null, page, size);

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty(), "The returned page should be empty");
        assertEquals(0, resultPage.getTotalElements(), "Total elements should be 0");
        assertEquals(0, resultPage.getContent().size(), "Content list size should be 0");

        // Upewniamy się, że mapper nigdy nie został wywołany (bo nie było czego mapować)
        verifyNoInteractions(truckMapper);
    }

    @Test
    void shouldThrowExceptionWhenTruckIdNotExists() {
        UUID truckId = UUID.randomUUID();

        when(truckRepository.findTruckByUuid(truckId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> truckService.addPackagesToTruck(aPackages, truckId));

        assertEquals("The truck with the id " + truckId + " does not exist", exception.getMessage());
    }

    @Test
    void getTrucks_shouldConvertPageNumberCorrectly_forPageableCalculation() {
        // given: sprawdzamy że page-1 jest poprawnie przekazywane (page 3 -> index 2)
        int page = 3;
        int size = 5;
        Pageable expectedPageable = PageRequest.of(page - 1, size);
        TruckFilterDto filter = new TruckFilterDto();
        Specification<Truck> mockedSpec = mock(Specification.class);
        Page<Truck> truckPage = new PageImpl<>(List.of(), expectedPageable, 0);

        try (MockedStatic<TruckSpecifications> mockedStatic = mockStatic(TruckSpecifications.class)) {
            mockedStatic.when(() -> TruckSpecifications.buildSpecification(filter))
                    .thenReturn(mockedSpec);
            when(truckRepository.findAll(eq(mockedSpec), eq(expectedPageable)))
                    .thenReturn(truckPage);

            // when
            truckService.getTrucks(filter, page, size);

            // then
            verify(truckRepository).findAll(mockedSpec, PageRequest.of(2, 5));
        }
    }


    @Test
    void shouldReturnPackagesWhichAddToTruck() {
        UUID truckId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        List<Package> uuidList = new ArrayList<>();
        uuidList.add(firstBoxPackToTruck());
        aPackages.add(firstBoxPackDto());

        truck.setPackedPackages(uuidList);

        truckService.addPackagesToTruck(aPackages, truckId);

        assertEquals(1, truck.getPackedPackages().size());
    }


}