package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.exception.ResourceNotFoundException;
import com.io.logiflow.packageConsumer.infrastructure.service.PackageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.io.logiflow.packageConsumer.domain.dto.PackageFixtureDTO.largeBoxDTO;
import static com.io.logiflow.packageConsumer.domain.dto.PackageFixtureDTO.smallDTO;
import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.*;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedTruck;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PackageController.class)
class PackageControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PackageService packageService;

    @InjectMocks
    private PackageController packageController;

    @BeforeEach
    public void setUp() throws Exception {
        Truck loadedTruck = createLoadedTruck();
        objectMapper.writeValueAsString(loadedTruck);
    }

    @Test
    void shouldReturnDefaultPageableWhenNoParamsProvided() throws Exception {
        PackageDTO mockPackDto = smallDTO();

        Page<PackageDTO> page = new PageImpl<>(
                List.of(mockPackDto),
                PageRequest.of(0, 10),
                1
        );

        //Sprawdzanie czy dla domyślnych wartości (0,10) serwis zostanie wywołany
        when(packageService.getPackages(0, 10)).thenReturn(page);

        mvc.perform(get("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].trackingNumber").value("XYZ-DEFAULT"))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0)) // To sprawdza Twoje 'page' (defaultValue = 0)
                .andExpect(jsonPath("$.pageable.pageSize").value(10));
    }

    @Test
    void shouldReturnCustomPageableWhenParamsProvided() throws Exception {
        int customPage = 2;
        int customSize = 5;

        PackageDTO mockPackage = largeBoxDTO();
        Page<PackageDTO> customPageResult = new PageImpl<>(List.of(mockPackage));

        // Konfigurujemy mock dla niestandardowych wartości 2 i 5
        Mockito.when(packageService.getPackages(customPage, customSize)).thenReturn(customPageResult);

        mvc.perform(get("/api/packages")
                        .param("page", String.valueOf(customPage))
                        .param("size", String.valueOf(customSize))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].trackingNumber").value("XYZ-CUSTOM"))
                // Te asercje weryfikują czy struktura Page w JSONie ma poprawne metadane
                .andExpect(jsonPath("$.size").value(1)) // rozmiar listy w content
                .andExpect(jsonPath("$.empty").value(false));

        // Dodatkowa weryfikacja: upewniamy się, że serwis został wywołany dokładnie z tymi parametrami
        Mockito.verify(packageService, Mockito.times(1)).getPackages(customPage, customSize);
    }

    @Test
    public void shouldRegisterNewPackageToSystemSuccessfully() throws Exception {
        Package registerASmallPackage = smallDao();
        registerASmallPackage.setPackageId(5L);
        String jsonRequest = objectMapper.writeValueAsString(registerASmallPackage);

        MvcResult result = mvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();

        int status = result.getResponse().getStatus();
        assertEquals(HttpStatus.CREATED.value(), status);
    }

    @Test
    public void shouldRegisterOfLargeAndHeavyPackageToSystemSuccessfully() throws Exception {
        Package largeAndHeavyPackage = largeBox();
        largeAndHeavyPackage.setPackageId(7L);
        largeAndHeavyPackage.setWeight(BigDecimal.valueOf(500.0));

        String jsonRequest = objectMapper.writeValueAsString(largeAndHeavyPackage);
        mvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.packageId").value(7L))
                .andExpect(jsonPath("$.weight").value(500.0))
                .andReturn();
    }

    @Test
    public void shouldUpdateAPackageToSystemSuccessfully() throws Exception {
        Long packId = 5L;
        Package existingPackage = smallDao();
        existingPackage.setTrackingNumber("TRK-2026-ORIGINAL");

        when(packageService.updatePackageFields(Mockito.eq(packId), Mockito.anyMap()))
                .thenReturn(updatePackage());

        String patchJson = """
                {
                 "width": 45,
                 "weight": 12.95
                 }
                """;

        mvc.perform(patch("/api/packages/{packId}", packId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk()) // Oczekujemy kodu 200 OK
                // Sprawdzamy, czy w odpowiedzi dostaliśmy poprawne dane:
                .andExpect(jsonPath("$.packageId").value(5L))
                .andExpect(jsonPath("$.trackingNumber").value("TRK-2026-ORIGINAL")) // Upewniamy się, że się nie nadpisał
                .andExpect(jsonPath("$.width").value(45))                     // Nowa wartość double
                .andExpect(jsonPath("$.weight").value(12.95));
    }

    @Test
    public void shouldUpdateOnlyAllowedFieldsAndIgnoreRest() throws Exception {
        Long originalId = 5L;

        Package updatedPackage = updatePackage();

        Mockito.when(packageService.updatePackageFields(Mockito.any(), Mockito.any()))
                .thenReturn(updatedPackage);

        //poprawa
        String maliciousJson = """  
                {
                  "packageId": 999,
                  "trackingNumber": "HACKED-123",
                  "width": 45
                }
                """;

        mvc.perform(patch("/api/packages/{id}", originalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(maliciousJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packageId").value(5))               // ignoruje próbę zmiany na 999
                .andExpect(jsonPath("$.trackingNumber").value("TRK-2026-ORIGINAL")) // ignoruje próbę zmiany na HACKED
                .andExpect(jsonPath("$.width").value(45));
    }

    @Test
    public void shouldReturn404WhenPackageDoesNotExist() throws Exception {
        Long nonExistingId = 999L;

        when(packageService.updatePackageFields(any(), any()))
                .thenThrow(new ResourceNotFoundException("Package not found"));

        String patchJson = """
                {
                  "weight": 12.5
                }
                """;

        mvc.perform(patch("/api/packages/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isNotFound())
                .andReturn();
    }


  /*  @Test
    public void shouldRegisterNewPackageToSystemFailure() throws Exception {
        Package registerASmallPackage = small();
        registerASmallPackage.setPackageId(5L);
        registerASmallPackage.setTrackingNumber("TRK-2026-A002");

        String jsonRequest = objectMapper.writeValueAsString(registerASmallPackage);

        Mockito.doThrow(new IllegalArgumentException("The package with the number " + registerASmallPackage.getTrackingNumber() + " already exist"))
                .when(packageService).registerNewPackage(Mockito.mock(Package.class));

        mvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();
    }*/

    @Test
    public void shouldAddPackagesSuccessfully() throws Exception {
        List<Package> packageList = Arrays.asList(firstBoxPackToTruck(), secondBoxPackToTruck());

        String jsonRequest = objectMapper.writeValueAsString(packageList);
        Long truckId = 1L;

        MvcResult result = mvc.perform(post("/api/truck/{truckId}", truckId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        int status = result.getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    public void shouldReturnBadRequestWhenPackageDoesNotFit() throws Exception {
        Package gigantPackage = largeBox();
        gigantPackage.setPackageId(99L);
        gigantPackage.setTrackingNumber("TRK-OVERWEIGHT");
        gigantPackage.setWeight(new BigDecimal("5000.0"));

        List<Package> packageList = List.of(gigantPackage);
        Long truckId = 1L;

        String jsonRequest = objectMapper.writeValueAsString(packageList);

        Mockito.doThrow(new IllegalArgumentException("Truck loading failed"))
                .when(packageService).addPackagesToTruck(Mockito.anyList(), Mockito.anyLong());

        mvc.perform(post("/api/truck/{truckId}", truckId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }
}