package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dto.PackageCreateRequest;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.domain.dto.PackageFilterDTO;
import com.io.logiflow.packageConsumer.domain.mapper.PackageCreateRequestMapper;
import com.io.logiflow.packageConsumer.domain.mapper.PackageMapper;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.filterPack;
import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.firstBoxPackToTruck;
import static com.io.logiflow.packageConsumer.domain.dto.PackageCreateRequestTest.smallRequest;
import static com.io.logiflow.packageConsumer.domain.dto.PackageFixtureDTO.filterBoxPackDto;
import static com.io.logiflow.packageConsumer.domain.dto.PackageFixtureDTO.smallDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PackageServiceTest {

    @Mock
    private PackageRepository packageRepository;

    @Mock
    private PackageMapper packageMapper;

    @Mock
    private PackageCreateRequestMapper createRequestMapper;

    @InjectMocks
    private PackageService packageService;

    @Test
    void shouldReturnCorrectRegisteredPack() {
        PackageCreateRequest inputRequest = smallRequest();
        PackageDTO inputDto = smallDTO();

        Package savedEntity = firstBoxPackToTruck();

        when(packageRepository.findByTrackingNumber(any())).thenReturn(null);
        when(createRequestMapper.toPackageDao(any(PackageCreateRequest.class))).thenReturn(savedEntity);
        when(packageRepository.save(any(Package.class))).thenReturn(savedEntity);
        when(packageMapper.toDTO(any(Package.class))).thenReturn(inputDto);

        packageService.registerNewPackage(inputRequest);
        ArgumentCaptor<Package> packageCaptor = ArgumentCaptor.forClass(Package.class);

        verify(createRequestMapper, times(1)).toPackageDao(inputRequest);
        verify(packageMapper).toDTO(packageCaptor.capture());
        verify(packageRepository).save(any(Package.class));
    }

    @Test
    void shouldGenerateUUIDWhenPackageDtoIdIsNull() {
        long seq = 5L;
        PackageCreateRequest createRequest = smallRequest();
        PackageDTO inputDto = smallDTO();
        inputDto.setId(null);

        Package mappedEntity = new Package();
        mappedEntity.setUuid(null);
        mappedEntity.setTrackingNumber(inputDto.getTrackingNumber());

        when(packageRepository.getNextTrackingSequenceValue()).thenReturn(seq);
        when(createRequestMapper.toPackageDao(any(PackageCreateRequest.class))).thenReturn(mappedEntity);
        when(packageRepository.findByTrackingNumber(inputDto.getTrackingNumber())).thenReturn(null);
        when(packageRepository.save(any(Package.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(packageMapper.toDTO(any(Package.class))).thenReturn(inputDto);

        packageService.registerNewPackage(createRequest);

        ArgumentCaptor<Package> packageCaptor = ArgumentCaptor.forClass(Package.class);
        verify(packageRepository).save(packageCaptor.capture());

        Package savedEntity = packageCaptor.getValue();
        assertNotNull(savedEntity.getUuid(), "The entity UUID should be automatically generated and not null");
    }

    @Test
    void shouldReturnPaginatedPackages() {
        int pageNumber = 1;
        int pageSize = 1;

        PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        PackageFilterDTO filter = new PackageFilterDTO();

        Package packageEntity = new Package();
        List<Package> entityList = List.of(packageEntity);
        Page<Package> mockPageFromDb = new PageImpl<>(entityList, pageable, 1);

        PackageDTO packageDto = new PackageDTO();

        when(packageRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockPageFromDb);

        when(packageMapper.toDTO(packageEntity))
                .thenReturn(packageDto);

        Page<PackageDTO> result = packageService.getPackages(filter, pageNumber, pageSize);

        // 3. THEN (Asercje i weryfikacja)
        // 3. THEN (Asercje wyniku)
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(packageDto);
        assertThat(result.getTotalElements()).isEqualTo(2);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(packageRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Pageable captured = pageableCaptor.getValue();
        assertThat(captured.getPageNumber()).isEqualTo(pageNumber - 1); // Oczekujemy 1
        assertThat(captured.getPageSize()).isEqualTo(pageSize);
    }

    @Test
    void shouldReturnPackageBySpecification() {
        // 1. GIVEN (Przygotowanie danych i mocków)
        int inputPage = 1;  // Strona oczekiwana przez użytkownika (1-based)
        int pageSize = 1;

        // Serwis przelicza page - 1, więc do repozytorium trafi strona 0
        PageRequest expectedPageable = PageRequest.of(inputPage - 1, pageSize);

        PackageFilterDTO filter = new PackageFilterDTO();
        filter.setMinWeight(new BigDecimal("10.5"));
        filter.setMaxWeight(new BigDecimal("50.0"));

        Package packageEntity = new Package();
        PackageDTO packageDto = new PackageDTO();

        Page<Package> mockPageFromDb = new PageImpl<>(List.of(packageEntity), expectedPageable, 1);

        // Rejestrujemy zachowanie mocków ZANIM wywołamy serwis
        when(packageRepository.findAll(any(Specification.class), eq(expectedPageable)))
                .thenReturn(mockPageFromDb);
        when(packageMapper.toDTO(packageEntity))
                .thenReturn(packageDto);

        // 2. WHEN (Wywołanie metody serwisu)
        Page<PackageDTO> result = packageService.getPackages(filter, inputPage, pageSize);

        // 3. THEN (Asercje i weryfikacja)
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(packageDto);

        // Weryfikujemy czy repozytorium zostało wywołane z odpowiednią paginacją
        verify(packageRepository).findAll(any(Specification.class), eq(expectedPageable));
    }


    @Test
    void shouldNullFilterDoesNotThrow() {
        int pageNumber = 1;
        int invalidSize = 0;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            packageService.getPackages(null, pageNumber, invalidSize);
        });

        // Weryfikujemy, czy komunikat błędu jest jasny
        assertEquals("Page size must not be less than one", exception.getMessage());

        // Gwarancja: Repozytorium nie powinno być w ogóle wywołane
        verifyNoInteractions(packageRepository);
    }

    @Test
    void shouldReturnEmptyPageWhenNoPackagesExist() {

        int page = 15;
        int size = 10;
        Page<Package> emptyPageFromDb = Page.empty();

        when(packageRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPageFromDb);
        Page<PackageDTO> resultPage = packageService.getPackages(null, page, size);

        // Then
        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty(), "The returned page should be empty");
        assertEquals(0, resultPage.getTotalElements(), "Total elements should be 0");
        assertEquals(0, resultPage.getContent().size(), "Content list size should be 0");

        // Upewniamy się, że mapper nigdy nie został wywołany
        verifyNoInteractions(packageMapper);
    }

    @Test
    void shouldReturnTrackNumberIfUserProvidedCorrectDate() {
        String trackingNumber = "TRK2026000000002";

        when(packageRepository.findPackageByTrackingNumber(any())).thenReturn(Optional.of(filterPack()));
        when(packageMapper.toDTO(any(Package.class))).thenReturn(filterBoxPackDto());

        PackageDTO packagesByFilter = packageService.getPackagesByTrackingNumber(trackingNumber);

        assertEquals(trackingNumber, packagesByFilter.getTrackingNumber());
        assertEquals(new BigDecimal("50.0"), packagesByFilter.getWeight());
        verify(packageMapper, times(1)).toDTO(any(Package.class));
    }

    @Test
    void shouldThrowExceptionWhenTrackingNumberFormatIsShort() {
        String trackingNumber = "TRK20260000002";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> packageService.getPackagesByTrackingNumber(trackingNumber));

        assertEquals("Invalid tracking number format.", exception.getMessage());
        verify(packageRepository, never()).findByTrackingNumber(trackingNumber);
    }

    @Test
    void shouldThrowExceptionWhenTrackingNumberFormatIsNotStartedTRK() {
        String trackingNumber = "KRT20260000002";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> packageService.getPackagesByTrackingNumber(trackingNumber));

        assertEquals("Invalid tracking number format.", exception.getMessage());
        verify(packageRepository, never()).findByTrackingNumber(trackingNumber);
    }

    @Test
    void shouldThrowExceptionWhenTrackingNumberNotFound() {
        String trackingNumber = "TRK2026000000000";

        when(packageRepository.findPackageByTrackingNumber(any())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> packageService.getPackagesByTrackingNumber(trackingNumber));

        assertEquals("Package with tracking number " + trackingNumber + " not found", exception.getMessage());
    }

    @Test
    void shouldReturnUUIDIfUserProvidedCorrectDate() {
        UUID uuid = filterBoxPackDto().getId();

        when(packageRepository.findPackageByUuid(any())).thenReturn(Optional.of(filterPack()));
        when(packageMapper.toDTO(any(Package.class))).thenReturn(filterBoxPackDto());

        PackageDTO packagesByFilter = packageService.getPackagesByUUID(uuid);

        assertNotNull(uuid);
        assertEquals("TRK2026000000002", packagesByFilter.getTrackingNumber());

        verify(packageRepository, times(1)).findPackageByUuid(uuid);
    }

    @Test
    void shouldThrowExceptionWhenWeDoNotProvidedUUID() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> packageService.getPackagesByUUID(null));
        assertEquals("Provide uuid. ", exception.getMessage());
        verify(packageRepository, never()).findPackageByUuid(null);
        verify(packageRepository, never()).findPackageByTrackingNumber(null);
    }

    @Test
    void shouldThrowExceptionWhenWeDoNotProvidedTrackingNumber() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> packageService.getPackagesByTrackingNumber(null));
        assertEquals("Tracking number cannot be null or blank.", exception.getMessage());
        verify(packageRepository, never()).findPackageByUuid(null);
        verify(packageRepository, never()).findPackageByTrackingNumber(null);
    }

    @Test
    void shouldThrowExceptionWhenUUIDNotFound() {
        UUID uuid = filterBoxPackDto().getId();
        when(packageRepository.findPackageByUuid(any())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> packageService.getPackagesByUUID(uuid));

        assertEquals("Package with UUID number " + uuid + " not found", exception.getMessage());
    }
}





