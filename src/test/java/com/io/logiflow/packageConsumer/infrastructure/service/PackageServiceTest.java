package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dto.PackageCreateRequest;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.domain.mapper.PackageCreateRequestMapper;
import com.io.logiflow.packageConsumer.domain.mapper.PackageMapper;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.domain.repository.TruckRepository;
import com.io.logiflow.packageConsumer.exception.PackageValidationException;
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

import java.util.List;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.firstBoxPackToTruck;
import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.smallDao;
import static com.io.logiflow.packageConsumer.domain.dto.PackageCreateRequestTest.smallRequest;
import static com.io.logiflow.packageConsumer.domain.dto.PackageFixtureDTO.smallDTO;
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

        Package expectedEntity = smallDao();
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
        int pageNumber = 0;
        int pageSize = 1;

        PageRequest pageable = PageRequest.of(pageNumber, pageSize);

        Package packageEntity = new Package();
        List<Package> entityList = List.of(packageEntity);
        Page<Package> mockPageFromDb = new PageImpl<>(entityList, pageable, 1);

        PackageDTO packageDto = new PackageDTO();

        when(packageRepository.findAll(pageable)).thenReturn(mockPageFromDb);
        when(packageMapper.toDTO(packageEntity)).thenReturn(packageDto);

        Page<PackageDTO> resultPage = packageService.getPackages(pageNumber, pageSize);

        // 3. THEN (Asercje i weryfikacja)
        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements(), "Total elements in page should be 1");
        assertEquals(1, resultPage.getContent().size(), "Page content size should match mapped elements count");
        assertEquals(packageDto, resultPage.getContent().get(0), "The content should contain the mapped DTO");

        // Sprawdzamy, czy repozytorium zostało wywołane z dokładnie takimi parametrami stronicowania
        verify(packageRepository).findAll(pageable);
    }

    @Test
    void shouldThrownExceptionWhenPageSizeIsInvalid() {
        int pageNumber = 0;
        int invalidSize = 0;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            packageService.getPackages(pageNumber, invalidSize);
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
        Pageable pageable = PageRequest.of(page, size);

        // Tworzymy całkowicie pustą stronę za pomocą Collections.emptyList()
        Page<Package> emptyPageFromDb = new PageImpl<>(List.of(), pageable, 0);

        // Konfigurujemy mock repozytorium, by zwrócił pustą stronę
        when(packageRepository.findAll(pageable)).thenReturn(emptyPageFromDb);

        // When
        Page<PackageDTO> resultPage = packageService.getPackages(page, size);

        // Then
        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty(), "The returned page should be empty");
        assertEquals(0, resultPage.getTotalElements(), "Total elements should be 0");
        assertEquals(0, resultPage.getContent().size(), "Content list size should be 0");

        // Upewniamy się, że mapper nigdy nie został wywołany (bo nie było czego mapować)
        verifyNoInteractions(packageMapper);
    }

}





