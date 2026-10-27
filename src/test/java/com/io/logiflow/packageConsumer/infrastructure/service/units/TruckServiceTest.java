package com.io.logiflow.packageConsumer.infrastructure.service.units;

import com.io.logiflow.packageConsumer.infrastructure.dao.Truck;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.CreateTruckDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.PackingRequestDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.TruckFilterDto;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.DimensionsDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.PackageDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.TruckDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.TruckResponseDTO;
import com.io.logiflow.packageConsumer.infrastructure.mapper.TruckMapper;
import com.io.logiflow.packageConsumer.infrastructure.repository.PackageJpaRepository;
import com.io.logiflow.packageConsumer.infrastructure.repository.TruckJpaRepository;
import com.io.logiflow.packageConsumer.infrastructure.exception.ResourceAlreadyExistsException;
import com.io.logiflow.packageConsumer.application.service.CargoSpaceService;
import com.io.logiflow.packageConsumer.application.service.TruckService;
import com.io.logiflow.packageConsumer.application.service.TruckPackingExecutor;
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

import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedSecondTruck;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedTruck;
import static com.io.logiflow.packageConsumer.domain.dto.request.CreateTruckDTOFixture.*;
import static com.io.logiflow.packageConsumer.domain.dto.request.PackingRequestDTOFixture.thirdPackingToPack;
import static com.io.logiflow.packageConsumer.domain.dto.response.CargoSpaceDTOFixture.boxFits;
import static com.io.logiflow.packageConsumer.domain.dto.response.TruckResponseDTOFixture.secondCreateTruckResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TruckServiceTest {

    @Mock
    private TruckJpaRepository truckJpaRepository;


    @Mock
    TruckMapper truckMapper;

    @Mock
    private PackageJpaRepository packageJpaRepository;

    @Mock
    private CargoSpaceService cargoSpaceService;

    @Mock
    TruckPackingExecutor truckPackingExecutor;

    @InjectMocks
    private TruckService truckService;

    private Truck truck;

    private List<PackageDTO> aPackages;

    @BeforeEach
    void setUp() {
        truck = createLoadedTruck();
        lenient().when(truckJpaRepository.findTruckByUuid(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))).thenReturn(Optional.of(truck));
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

        when(truckJpaRepository.findAll(any(Specification.class), any(Pageable.class)))
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

        when(truckJpaRepository.findAll(any(Specification.class), any(Pageable.class)))
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
            when(truckJpaRepository.findAll(eq(mockedSpec), eq(expectedPageable)))
                    .thenReturn(truckPage);

            truckService.getTrucks(filter, page, size);

            verify(truckJpaRepository).findAll(mockedSpec, PageRequest.of(2, 5));
        }
    }


    @Test
    void shouldReturnPackagesWhichAddToTruck() {
        PackingRequestDTO toPack = thirdPackingToPack();

        truckService.addPackagesToTruck(toPack);

        assertEquals(3, truck.getPackedPackages().size());
    }

    @Test
    void shouldReturnCorrectCreateTruck() {
        CreateTruckDTO createTruck = createSecondTruck();
        String license_plate = "W0 1234";

        when(truckJpaRepository.existsByLicensePlate(license_plate)).thenReturn(false);
        when(truckJpaRepository.save(any(Truck.class))).thenReturn(createLoadedSecondTruck());
        when(truckMapper.toTruckResponseDTO(any(Truck.class))).thenReturn(secondCreateTruckResponse());
        when(cargoSpaceService.addNewCargoSpaceToTruck(any(Truck.class), any(DimensionsDTO.class))).thenReturn(boxFits());

        TruckResponseDTO addTruck = truckService.createTruck(createTruck);

        assertEquals(license_plate, addTruck.licensePlate());
        assertThat(addTruck.uuid())
                .isEqualTo(UUID.fromString("c771f079-fb74-4811-b491-e78673c41e0d"));

        verify(truckJpaRepository).save(any(Truck.class));
        verify(cargoSpaceService).addNewCargoSpaceToTruck(any(Truck.class), any(DimensionsDTO.class));
    }

    @Test
    void shouldThrowExceptionWhenProvidedNegativeParameter() {
        CreateTruckDTO weightTruck = createNegativeWidthTruck();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> truckService.createTruck(weightTruck));
        assertEquals("Exterior width must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenProvidedNullWeightParameter() {
        CreateTruckDTO weightTruck = createEmptyWeightTruck();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> truckService.createTruck(weightTruck));
        assertEquals("Dimensions and weight are required", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLicensePlateIsExists() {
        String license_plate = "WI 1234E";

        when(truckJpaRepository.existsByLicensePlate(license_plate)).thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class, () -> truckService.createTruck(createFirstTruck()));
        assertEquals("The truck with license plate WI 1234E already exists", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLicensePlateIsNull() {
        CreateTruckDTO createTruck = createNullLicensePlateTruck();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> truckService.createTruck(createTruck));
        assertEquals("license_plate is required", exception.getMessage());
    }
}