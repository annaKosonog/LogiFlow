package com.io.logiflow.packageConsumer.infrastructure.service.integration;

import com.io.logiflow.packageConsumer.domain.dto.request.PackageCreateRequest;
import com.io.logiflow.packageConsumer.domain.dto.request.PackageFilterDTO;
import com.io.logiflow.packageConsumer.domain.dto.request.UpdatePackageDimensionsRequestDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.PackageDTO;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.exception.ResourceNotFoundException;
import com.io.logiflow.packageConsumer.infrastructure.controllers.integrations.AbstractIntegrationTest;
import com.io.logiflow.packageConsumer.infrastructure.service.PackageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.firstBoxPackToTruck;
import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.secondBoxPackToTruck;
import static com.io.logiflow.packageConsumer.domain.dto.request.PackageCreateRequestFixture.*;
import static com.io.logiflow.packageConsumer.domain.dto.request.UpdatePackageDimensionsRequestDTOFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class PackingServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PackageRepository packageRepository;
    @Autowired
    private PackageService packageService;

    @Autowired
    private JdbcTemplate jdbc;

    PackageFilterDTO filter = new PackageFilterDTO();


    @BeforeEach
    public void setUp() {
        packageRepository.save(firstBoxPackToTruck());
        packageRepository.save(secondBoxPackToTruck());
    }

    @AfterEach
    public void cleanUp() {
        try {
            packageRepository.deleteAll();
            packageRepository.flush();
        } catch (Exception e) {
            throw e;
        }
    }


    @Test
    void shouldReturnANewPackage() {
        PackageCreateRequest request = smallRequest();
        System.out.println(packageRepository.findAll());
        System.out.println("--------------------------------------");

        PackageDTO newPackage = packageService.registerNewPackage(request);

        assertEquals(1, newPackage.getDimensions().getWidth());
        assertEquals(1, newPackage.getDimensions().getHeight());
        assertThat(newPackage.getId()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenDimensionIsNull() {
        PackageCreateRequest request = createPackageWithoutDimension();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> packageService.registerNewPackage(request));
        assertEquals("Length must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenWeightIsNull() {
        PackageCreateRequest request = firstBoxCreateRequest();
        request.setWeight(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> packageService.registerNewPackage(request));
        assertEquals("Dimensions and weight are required", exception.getMessage());
    }

    @Test
    void shouldThrowWhenNewPackageRequestWidthIsNegative() {
        PackageCreateRequest request = requestFirst();
        request.getDimensions().setWidth(-1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> packageService.registerNewPackage(request));
        assertEquals("Width must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowWhenNewPackageRequestHeightIsNegative() {
        PackageCreateRequest request = requestFirst();
        request.getDimensions().setHeight(-1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> packageService.registerNewPackage(request));

        assertEquals("Height must be greater than zero", exception.getMessage());

    }

    @Test
    void shouldThrowWhenNewPackageRequestDepthIsNegative() {
        DimensionsDTO d = new DimensionsDTO();
        d.setWidth(10);
        d.setHeight(10);
        d.setDepth_length(-10);

        PackageCreateRequest request = new PackageCreateRequest();
        request.setWeight(BigDecimal.valueOf(2));
        request.setDimensions(d);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> packageService.registerNewPackage(request));
        assertEquals("Length must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowWhenNewPackageRequestWeightIsNegative() {
        PackageCreateRequest request = requestFirst();
        request.setWeight(new BigDecimal(-1));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> packageService.registerNewPackage(request));
        assertEquals("Weight must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldReturnPackagesWhenProvideTrackingNumberToFilter() {
        int page = 1;
        int size = 5;

        filter.setTrackingNumber("TRK2026000000001");
        Page<PackageDTO> result = packageService.getPackages(filter, page, size);

        assertThat(result.getContent())
                .filteredOn(dto -> dto.getTrackingNumber().equals("TRK2026000000001"))
                .singleElement()
                .satisfies(dto -> {
                            assertThat(dto.getDimensions().getHeight()).isEqualTo(1);
                            assertThat(dto.getId()).isEqualTo(UUID.fromString("3a2b1c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d"));
                            assertThat(dto.getWeight()).isEqualByComparingTo(new BigDecimal("10.00"));
                        }
                );
    }

    @Test
    void shouldReturnDefaultPageWhenTrackingNumberDoNotExists() {
        int page = 1;
        int size = 5;

        filter.setTrackingNumber(null);
        Page<PackageDTO> result = packageService.getPackages(filter, page, size);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(PackageDTO::getId)
                .containsExactly(UUID.fromString("3a2b1c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d"),
                        UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d"));

        assertThat(result.getNumber()).isEqualTo(0);          // numer bieżącej strony
        assertThat(result.getSize()).isEqualTo(5);            // żądany rozmiar strony
        assertThat(result.getTotalElements()).isEqualTo(2);   // wszystkich rekordów w bazie
        assertThat(result.isLast()).isTrue();

    }

    @Test
    void shouldThrowExceptionWhenTrackingNumberFormatIsTooShort() {
        String trackingNumber = "TRK20260000002";
        filter.setTrackingNumber(trackingNumber);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> packageService.getPackagesByTrackingNumber(trackingNumber));
        assertEquals("Invalid tracking number format.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTrackingNumberNotFound() {
        String trackingNumber = "TRK2026000000000";
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> packageService.getPackagesByTrackingNumber(trackingNumber));
        assertEquals("Package with tracking number " + trackingNumber + " not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTrackingNumberIsNullOrBlank() {
        String trackingNumber = "";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> packageService.getPackagesByTrackingNumber(trackingNumber));
        assertEquals("Tracking number cannot be null or blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTrackingNumberNotStartedToTRK() {
        String trackingNumber = "KRT20260000002";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> packageService.getPackagesByTrackingNumber(trackingNumber));
        assertEquals("Invalid tracking number format.", exception.getMessage());
    }

    @Test
    void shouldReturnUUIDIfUserProvidedCorrectDate() {
        UUID packageId = UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");

        PackageDTO packagesByUUID = packageService.getPackagesByUUID(packageId);
        assertNotNull(packageId);
        assertEquals("TRK2026000000002", packagesByUUID.getTrackingNumber());
        assertThat(packagesByUUID.getWeight()).isEqualByComparingTo("30");
    }

    @Test
    void shouldThrowExceptionWhenPackageWithUUIDNumberNotFound() {
        UUID packId = UUID.randomUUID();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> packageService.getPackagesByUUID(packId));
        assertEquals("Package with UUID number " + packId + " not found", exception.getMessage());
    }

    @Test
    void shouldReturnPackageDTOWhenProvidedCorrectDataToUpdate() {
        UUID packageId = UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");
        UpdatePackageDimensionsRequestDTO fields = updateAllFields();

        PackageDTO updatePackageFields = packageService.updatePackageFields(packageId, fields);

        assertEquals(15, updatePackageFields.getDimensions().getWidth());
        assertEquals(12, updatePackageFields.getDimensions().getHeight());
        assertEquals(120, updatePackageFields.getDimensions().getDepth_length());
        assertThat(updatePackageFields.getWeight()).isEqualByComparingTo("87");
        assertEquals("TRK2026000000002", updatePackageFields.getTrackingNumber());
    }

    @Test
    void shouldReturnPackageDTOWhenProvidedOneParameterToUpdate() {
        UUID packageId = UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");
        UpdatePackageDimensionsRequestDTO fields = secondFieldWeightToUpdate();

        PackageDTO updatePackageFields = packageService.updatePackageFields(packageId, fields);
        assertThat(updatePackageFields.getWeight()).isEqualByComparingTo("127");
        assertEquals("TRK2026000000002", updatePackageFields.getTrackingNumber());
    }

    @Test
    void shouldThrowExceptionWhenIdoNotProvidedZeroParameterToUpdate() {
        UUID packageId = UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");
        UpdatePackageDimensionsRequestDTO fields = emptyFieldsToUpdate();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> packageService.updatePackageFields(packageId, fields));

        assertEquals("At least one field must be provided", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIProvidedPackIdThatDoNotExistDBToUpdate() {
        UUID packageId = UUID.randomUUID();
        UpdatePackageDimensionsRequestDTO fields = secondFieldWeightToUpdate();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> packageService.updatePackageFields(packageId, fields));
        assertEquals("Package not found", exception.getMessage());
    }


    @Test
    void shouldThrowExceptionWhenFieldWeightIsNegative() {
        UUID packageId = UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");

        UpdatePackageDimensionsRequestDTO fields = negativeWeightToUpdate();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> packageService.updatePackageFields(packageId, fields));
        assertThat(exception.getMessage())
                .contains("Weight")
                .contains(" must be greater than zero");
    }

    @Test
    void shouldReturnPackageDTOWhenProvidedCorrectUUID() {
        UUID packageId = UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");

        PackageDTO packagesByUUID = packageService.getPackagesByUUID(packageId);

        assertEquals("TRK2026000000002", packagesByUUID.getTrackingNumber());
        assertEquals(5, packagesByUUID.getDimensions().getDepth_length());
        assertEquals(0, packagesByUUID.getCoordinates().x());
        assertEquals("30.00", packagesByUUID.getWeight().toString());
        assertNull(packagesByUUID.getTruckID());
    }

    @Test
    void shouldThrowExceptionWhenUUIDIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> packageService.getPackagesByUUID(null));
        assertEquals("Provide uuid. ", exception.getMessage());
    }
}