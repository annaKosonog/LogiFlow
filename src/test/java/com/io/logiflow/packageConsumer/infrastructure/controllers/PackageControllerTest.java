package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.io.logiflow.packageConsumer.domain.dto.PackageCreateRequest;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.domain.mapper.PackageMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedTruck;
import static com.io.logiflow.packageConsumer.domain.dto.PackageCreateRequestTest.firstBoxCreateRequest;
import static com.io.logiflow.packageConsumer.domain.dto.PackageCreateRequestTest.largeAndHeavyBoxRequest;
import static com.io.logiflow.packageConsumer.domain.dto.PackageFixtureDTO.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @MockBean
    private PackageMapper packageMapper;

    @BeforeEach
    public void setUp() throws Exception {
        createLoadedTruck();
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
                .andExpect(jsonPath("$.content[0].trackingNumber").value("XYZ-SMALL"))
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
                .andExpect(jsonPath("$.content[0].trackingNumber").value("XYZ-LARGE"))
                // Te asercje weryfikują czy struktura Page w JSONie ma poprawne metadane
                .andExpect(jsonPath("$.size").value(1)) // rozmiar listy w content
                .andExpect(jsonPath("$.empty").value(false));

        // Dodatkowa weryfikacja: upewniamy się, że serwis został wywołany dokładnie z tymi parametrami
        Mockito.verify(packageService, Mockito.times(1)).getPackages(customPage, customSize);
    }

    @Test
    public void shouldRegisterNewPackageToSystemSuccessfully() throws Exception {
        PackageCreateRequest inputDto = firstBoxCreateRequest();

        String jsonRequest = objectMapper.writeValueAsString(inputDto);
        UUID generatedUuid = UUID.randomUUID();


        PackageDTO mockResult = new PackageDTO();
        mockResult.setId(generatedUuid);
        mockResult.setTrackingNumber("TRK-2026-A001");
        mockResult.setWeight(new BigDecimal("10"));

        Mockito.when(packageService.registerNewPackage(Mockito.any(PackageCreateRequest.class)))
                .thenReturn(mockResult);

        mvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(generatedUuid.toString()))
                .andExpect(jsonPath("$.trackingNumber").exists())
                .andExpect(jsonPath("$.weight").value("10"))
                .andReturn();
    }


    @Test
    public void shouldRegisterOfLargeAndHeavyPackageToSystemSuccessfully() throws Exception {
        UUID expectedId = UUID.randomUUID();
        PackageCreateRequest inputRequest = largeAndHeavyBoxRequest();


        PackageDTO outputDTO = new PackageDTO();
        outputDTO.setId(expectedId);
        outputDTO.setWeight(BigDecimal.valueOf(500.0));

        Mockito.when(packageService.registerNewPackage(Mockito.any(PackageCreateRequest.class)))
                .thenReturn(outputDTO);

        String jsonRequest = objectMapper.writeValueAsString(inputRequest);
        mvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedId.toString()))
                .andExpect(jsonPath("$.weight").value(500.0))
                .andReturn();
    }

    @Test
    public void shouldUpdateAPackageToSystemSuccessfully() throws Exception {
        UUID packId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        PackageDTO patchDto = smallDTO();
        patchDto.getDimensions().setWidth(45);
        patchDto.setWeight(new BigDecimal("12.95"));

        String jsonRequest = objectMapper.writeValueAsString(patchDto);
        when(packageService.registerNewPackage(any(PackageCreateRequest.class))).thenReturn(patchDto);
        when(packageService.updatePackageFields(Mockito.eq(packId), Mockito.anyMap())).thenReturn(patchDto);

        mvc.perform(patch("/api/packages/{packId}", packId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isOk()) // Oczekujemy kodu 200 OK
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.trackingNumber").value("XYZ-SMALL")) // Upewniamy się, że się nie nadpisał
                .andExpect(jsonPath("$.dimensions.width").value(45))                     // Nowa wartość double
                .andExpect(jsonPath("$.weight").value(12.95));
    }

    @Test
    public void shouldUpdateOnlyAllowedFieldsAndIgnoreRest() throws Exception {
        UUID originalId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        Map<String, Object> fieldsToUpdate = new HashMap<>();
        fieldsToUpdate.put("weight", 750.0);
        fieldsToUpdate.put("trackingNumber", "HACKED-123");

        PackageDTO patchDto = updatePackageDTO();

        when(packageService.registerNewPackage(any(PackageCreateRequest.class))).thenReturn(patchDto);
        when(packageService.updatePackageFields(Mockito.eq(originalId), Mockito.anyMap())).thenReturn(patchDto);

        String jsonRequest = objectMapper.writeValueAsString(patchDto);

        mvc.perform(patch("/api/packages/{packId}", originalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.trackingNumber").value("TRK-2026-ORIGINAL")) // ignoruje próbę zmiany na HACKED
                .andExpect(jsonPath("$.dimensions.width").value(45));
    }

    @Test
    public void shouldReturn404WhenPackageDoesNotExist() throws Exception {
        UUID nonExistingId = UUID.randomUUID();
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setWeight(new BigDecimal("12.5"));

        when(packageService.updatePackageFields(any(UUID.class), any()))
                .thenThrow(new ResourceNotFoundException("Package not found"));

        String patchJson = objectMapper.writeValueAsString(packageDTO);

        mvc.perform(patch("/api/packages/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectTrackNumber() throws Exception {
        String trackNumber = "TRK2026000000002";

        when(packageService.getPackagesByTrackingNumber(trackNumber)).thenReturn(filterBoxPackDto());

        mvc.perform(get("/api/packages/tracking/{trackingNumber}", trackNumber))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weight").value(new BigDecimal("50.0")))
                .andExpect(jsonPath("$.trackingNumber").exists())
                .andExpect(jsonPath("$.id").isString());
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectUUID() throws Exception {
        UUID uuid = filterBoxPackDto().getId();

        when(packageService.getPackagesByUUID(uuid)).thenReturn(filterBoxPackDto());

        mvc.perform(get("/api/packages/{uuid}", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").value("TRK2026000000002"));
    }
}