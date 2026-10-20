package com.io.logiflow.packageConsumer.infrastructure.controllers.units;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.PackageCreateRequest;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.PackageFilterDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.UpdatePackageDimensionsRequestDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.PackageDTO;
import com.io.logiflow.packageConsumer.infrastructure.repository.PackageRepository;
import com.io.logiflow.packageConsumer.infrastructure.exception.ResourceNotFoundException;
import com.io.logiflow.packageConsumer.infrastructure.controllers.PackageController;
import com.io.logiflow.packageConsumer.application.service.PackageService;
import org.junit.jupiter.api.AfterEach;
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
import java.util.List;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dto.request.PackageCreateRequestFixture.firstBoxCreateRequest;
import static com.io.logiflow.packageConsumer.domain.dto.request.PackageCreateRequestFixture.largeAndHeavyBoxRequest;
import static com.io.logiflow.packageConsumer.domain.dto.request.UpdatePackageDimensionsRequestDTOFixture.fieldWeightAndWidthToUpdate;
import static com.io.logiflow.packageConsumer.domain.dto.request.UpdatePackageDimensionsRequestDTOFixture.fieldWeightToUpdate;
import static com.io.logiflow.packageConsumer.domain.dto.response.PackageFixtureDTO.*;
import static org.mockito.ArgumentMatchers.*;
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

    @MockBean
    private PackageRepository packageRepository;

    @InjectMocks
    private PackageController packageController;

    PackageDTO mockPackDto;
    PackageDTO secondMockPackDto;
    Page<PackageDTO> defaultPage;

    @BeforeEach
    public void setUp() throws Exception {

        mockPackDto = firstPackageToPack();
        secondMockPackDto = secondPackageToPack();
    }

    @AfterEach
    public void cleanUp() {
        packageRepository.deleteAll();
        packageRepository.flush();
    }

    @Test
    void shouldReturnDefaultPageableWhenNoParamsProvided() throws Exception {
        defaultPage = new PageImpl<>(List.of(mockPackDto, secondMockPackDto), PageRequest.of(1, 5), 2);
        when(packageService.getPackages(any(PackageFilterDTO.class), eq(1), eq(5))).thenReturn(defaultPage);

        mvc.perform(get("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000004"))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(1))
                .andExpect(jsonPath("$.data.pageable.pageSize").value(5));
    }

    @Test
    void shouldReturnCustomPageableWhenParamsProvided() throws Exception {
        int customPage = 2;
        int customSize = 5;

        PackageDTO mockPackage = largeBoxDTO();
        Page<PackageDTO> page = new PageImpl<>(List.of(mockPackage));

        // Konfigurujemy mock dla niestandardowych wartości 2 i 5
        when(packageService.getPackages(any(PackageFilterDTO.class), eq(customPage), eq(customSize))).thenReturn(page);


        mvc.perform(get("/api/packages")
                        .param("page", String.valueOf(customPage))
                        .param("size", String.valueOf(customSize))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("XYZ-LARGE"))
                .andExpect(jsonPath("$.data.size").value(1)) // rozmiar listy w content
                .andExpect(jsonPath("$.data.empty").value(false));
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectTrackNumber() throws Exception {
        String trackNumber = "TRK2026000000002";
        PackageDTO mockPackage = filterBoxPackDto();
        Page<PackageDTO> page = new PageImpl<>(List.of(mockPackage));

        when(packageService.getPackagesByTrackingNumber(trackNumber)).thenReturn(filterBoxPackDto());
        when(packageService.getPackages(any(PackageFilterDTO.class), eq(1), eq(5))).thenReturn(page);

        mvc.perform(get("/api/packages")
                        .param("trackingNumber", trackNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].weight").value(new BigDecimal("50.0")))
                .andExpect(jsonPath("$.data.content[0].trackingNumber").exists())
                .andExpect(jsonPath("$.data.content[0].id").isString());
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedNoCorrectTrackNumber() throws Exception {
        String trackNumber = "TRK2026000000222";
        PackageDTO mockPackage = filterBoxPackDto();
        Page<PackageDTO> page = new PageImpl<>(List.of(mockPackage));

        when(packageService.getPackagesByTrackingNumber(trackNumber)).thenReturn(null);

        when(packageService.getPackagesByTrackingNumber(trackNumber)).thenReturn(filterBoxPackDto());
        when(packageService.getPackages(any(PackageFilterDTO.class), eq(1), eq(5))).thenReturn(page);

        mvc.perform(get("/api/packages")
                        .param("trackingNumber", trackNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].weight").value(new BigDecimal("50.0")))
                .andExpect(jsonPath("$.data.content[0].trackingNumber").exists())
                .andExpect(jsonPath("$.data.content[0].id").isString());
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectUUID() throws Exception {
        UUID id = UUID.fromString("7c9e0d12-5b1d-407e-8c43-1bc2d3f4a5b6");
        defaultPage = new PageImpl<>(List.of(mockPackDto), PageRequest.of(1, 5), 2);

        when(packageService.getPackagesByUUID(id)).thenReturn(mockPackDto);
        when(packageService.getPackages(any(PackageFilterDTO.class), eq(1), eq(5))).thenReturn(defaultPage);

        mvc.perform(get("/api/packages")
                        .param("id", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].weight").value(new BigDecimal("45")))
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000004"))
                .andExpect(jsonPath("$.data.content[0].id").value(id.toString()));
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedNoUUID() throws Exception {
        UUID id = UUID.randomUUID();
        defaultPage = new PageImpl<>(List.of(), PageRequest.of(1, 5), 2);

        when(packageService.getPackagesByUUID(id)).thenReturn(null);
        when(packageService.getPackages(any(PackageFilterDTO.class), eq(1), eq(5))).thenReturn(defaultPage);

        mvc.perform(get("/api/packages")
                        .param("id", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectMinAndMaxHeight() throws Exception {
        BigDecimal minHeight = new BigDecimal("75");
        BigDecimal maxHeight = new BigDecimal("150");
        defaultPage = new PageImpl<>(List.of(mockPackDto, secondMockPackDto), PageRequest.of(1, 5), 2);

        when(packageService.getPackages(any(PackageFilterDTO.class), eq(1), eq(5))).thenReturn(defaultPage);
        mvc.perform(get("/api/packages")
                        .param("minHeight", String.valueOf(minHeight))
                        .param("maxHeight", String.valueOf(maxHeight))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].weight").value(new BigDecimal("45")))
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000004"))
                .andExpect(jsonPath("$.data.content[1].trackingNumber").value("TRK2026000000005"))
                .andExpect(jsonPath("$.data.size").value(5));
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectMinHeight() throws Exception {
        BigDecimal minHeight = new BigDecimal("100");
        defaultPage = new PageImpl<>(List.of(secondMockPackDto), PageRequest.of(1, 5), 2);

        when(packageService.getPackages(any(PackageFilterDTO.class), anyInt(), anyInt()))
                .thenReturn(defaultPage);

        mvc.perform(get("/api/packages")
                        .param("minHeight", String.valueOf(minHeight))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000005"))
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectMaxHeight() throws Exception {
        BigDecimal maxHeight = new BigDecimal("120");
        defaultPage = new PageImpl<>(List.of(mockPackDto, secondMockPackDto), PageRequest.of(1, 5), 2);

        when(packageService.getPackages(any(PackageFilterDTO.class), anyInt(), anyInt()))
                .thenReturn(defaultPage);

        mvc.perform(get("/api/packages")
                        .param("maxHeight", String.valueOf(maxHeight))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000004"))
                .andExpect(jsonPath("$.data.content[1].trackingNumber").value("TRK2026000000005"))
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectMaxHeightLess() throws Exception {
        BigDecimal maxWeight = new BigDecimal("100");
        defaultPage = new PageImpl<>(List.of(mockPackDto), PageRequest.of(1, 5), 2);

        when(packageService.getPackages(any(PackageFilterDTO.class), anyInt(), anyInt()))
                .thenReturn(defaultPage);

        mvc.perform(get("/api/packages")
                        .param("maxWeight", String.valueOf(maxWeight))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000004"))
                .andExpect(jsonPath("$.data.content[0].weight").value("45"))
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectMinAndMaxWeight() throws Exception {
        BigDecimal minWeight = new BigDecimal("45");
        BigDecimal maxWeight = new BigDecimal("120");
        defaultPage = new PageImpl<>(List.of(mockPackDto, secondMockPackDto), PageRequest.of(1, 5), 2);

        when(packageService.getPackages(any(PackageFilterDTO.class), eq(1), eq(5))).thenReturn(defaultPage);
        mvc.perform(get("/api/packages")
                        .param("minWeight", String.valueOf(minWeight))
                        .param("maxWeight", String.valueOf(maxWeight))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].weight").value(new BigDecimal("45")))
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000004"))
                .andExpect(jsonPath("$.data.content[1].trackingNumber").value("TRK2026000000005"))
                .andExpect(jsonPath("$.data.size").value(5));
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectMinWeight() throws Exception {
        BigDecimal minWeight = new BigDecimal("50");
        defaultPage = new PageImpl<>(List.of(secondMockPackDto), PageRequest.of(1, 5), 2);

        when(packageService.getPackages(any(PackageFilterDTO.class), anyInt(), anyInt()))
                .thenReturn(defaultPage);

        mvc.perform(get("/api/packages")
                        .param("minWeight", String.valueOf(minWeight))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000005"))
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectMaxWeight() throws Exception {
        BigDecimal maxWeight = new BigDecimal("120");
        defaultPage = new PageImpl<>(List.of(mockPackDto, secondMockPackDto), PageRequest.of(1, 5), 2);

        when(packageService.getPackages(any(PackageFilterDTO.class), anyInt(), anyInt()))
                .thenReturn(defaultPage);

        mvc.perform(get("/api/packages")
                        .param("maxWeight", String.valueOf(maxWeight))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000004"))
                .andExpect(jsonPath("$.data.content[1].trackingNumber").value("TRK2026000000005"))
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectMaxWeightLess() throws Exception {
        BigDecimal maxWeight = new BigDecimal("50");
        defaultPage = new PageImpl<>(List.of(mockPackDto), PageRequest.of(1, 5), 2);

        when(packageService.getPackages(any(PackageFilterDTO.class), anyInt(), anyInt()))
                .thenReturn(defaultPage);

        mvc.perform(get("/api/packages")
                        .param("maxWeight", String.valueOf(maxWeight))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000004"))
                .andExpect(jsonPath("$.data.content[0].weight").value("45"))
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }


    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectMinAndMaxDepth() throws Exception {
        BigDecimal minDepth = new BigDecimal("14");
        BigDecimal maxDepth = new BigDecimal("120");
        defaultPage = new PageImpl<>(List.of(mockPackDto, secondMockPackDto), PageRequest.of(1, 5), 2);

        when(packageService.getPackages(any(PackageFilterDTO.class), eq(1), eq(5))).thenReturn(defaultPage);
        mvc.perform(get("/api/packages")
                        .param("minDepth", String.valueOf(minDepth))
                        .param("maxDepth", String.valueOf(maxDepth))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].weight").value(new BigDecimal("45")))
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000004"))
                .andExpect(jsonPath("$.data.content[1].trackingNumber").value("TRK2026000000005"))
                .andExpect(jsonPath("$.data.size").value(5));
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectMinDepth() throws Exception {
        BigDecimal minWeight = new BigDecimal("100");
        defaultPage = new PageImpl<>(List.of(secondMockPackDto), PageRequest.of(1, 5), 2);

        when(packageService.getPackages(any(PackageFilterDTO.class), anyInt(), anyInt()))
                .thenReturn(defaultPage);

        mvc.perform(get("/api/packages")
                        .param("minDepth", String.valueOf(minWeight))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000005"))
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectMaxDepth() throws Exception {
        BigDecimal maxWeight = new BigDecimal("120");
        defaultPage = new PageImpl<>(List.of(mockPackDto, secondMockPackDto), PageRequest.of(1, 5), 2);

        when(packageService.getPackages(any(PackageFilterDTO.class), anyInt(), anyInt()))
                .thenReturn(defaultPage);

        mvc.perform(get("/api/packages")
                        .param("maxWeight", String.valueOf(maxWeight))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000004"))
                .andExpect(jsonPath("$.data.content[1].trackingNumber").value("TRK2026000000005"))
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectMaxDepthLess() throws Exception {
        BigDecimal maxDepth = new BigDecimal("22");
        defaultPage = new PageImpl<>(List.of(mockPackDto), PageRequest.of(1, 5), 2);

        when(packageService.getPackages(any(PackageFilterDTO.class), anyInt(), anyInt()))
                .thenReturn(defaultPage);

        mvc.perform(get("/api/packages")
                        .param("maxDepth", String.valueOf(maxDepth))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000004"))
                .andExpect(jsonPath("$.data.content[0].weight").value("45"))
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectMinAndMaxWidth() throws Exception {
        BigDecimal minWidth = new BigDecimal("10");
        BigDecimal maxWidth = new BigDecimal("150");
        defaultPage = new PageImpl<>(List.of(mockPackDto, secondMockPackDto), PageRequest.of(1, 5), 2);

        when(packageService.getPackages(any(PackageFilterDTO.class), eq(1), eq(5))).thenReturn(defaultPage);
        mvc.perform(get("/api/packages")
                        .param("minWidth", String.valueOf(minWidth))
                        .param("maxWidth", String.valueOf(maxWidth))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].weight").value(new BigDecimal("45")))
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000004"))
                .andExpect(jsonPath("$.data.content[1].trackingNumber").value("TRK2026000000005"))
                .andExpect(jsonPath("$.data.size").value(5));
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectMinWidth() throws Exception {
        BigDecimal minWidth = new BigDecimal("95");
        defaultPage = new PageImpl<>(List.of(secondMockPackDto), PageRequest.of(1, 5), 2);

        when(packageService.getPackages(any(PackageFilterDTO.class), anyInt(), anyInt()))
                .thenReturn(defaultPage);

        mvc.perform(get("/api/packages")
                        .param("minWidth", String.valueOf(minWidth))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000005"))
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectMaxWidth() throws Exception {
        BigDecimal maxWidth = new BigDecimal("120");
        defaultPage = new PageImpl<>(List.of(mockPackDto, secondMockPackDto), PageRequest.of(1, 5), 2);

        when(packageService.getPackages(any(PackageFilterDTO.class), anyInt(), anyInt()))
                .thenReturn(defaultPage);

        mvc.perform(get("/api/packages")
                        .param("maxWidth", String.valueOf(maxWidth))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000004"))
                .andExpect(jsonPath("$.data.content[1].trackingNumber").value("TRK2026000000005"))
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    public void shouldReturnPackageDTOWhenProvidedCorrectMaxWidthLess() throws Exception {
        BigDecimal maxWeight = new BigDecimal("100");
        defaultPage = new PageImpl<>(List.of(mockPackDto), PageRequest.of(1, 5), 2);

        when(packageService.getPackages(any(PackageFilterDTO.class), anyInt(), anyInt()))
                .thenReturn(defaultPage);

        mvc.perform(get("/api/packages")
                        .param("maxWeight", String.valueOf(maxWeight))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000004"))
                .andExpect(jsonPath("$.data.content[0].weight").value("45"))
                .andExpect(jsonPath("$.data.content.length()").value(1));
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
                .andExpect(jsonPath("$.data.id").value(generatedUuid.toString()))
                .andExpect(jsonPath("$.data.trackingNumber").exists())
                .andExpect(jsonPath("$.data.weight").value("10"))
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
                .andExpect(jsonPath("$.data.id").value(expectedId.toString()))
                .andExpect(jsonPath("$.data.weight").value(500.0))
                .andReturn();
    }

    @Test
    public void shouldUpdateAPackageToSystemSuccessfully() throws Exception {
        UUID packId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        PackageDTO savePackage = smallDTO();
        UpdatePackageDimensionsRequestDTO patchFields = fieldWeightAndWidthToUpdate();

        String jsonRequest = objectMapper.writeValueAsString(patchFields);

        when(packageService.registerNewPackage(any(PackageCreateRequest.class))).thenReturn(savePackage);
        when(packageService.updatePackageFields(eq(packId), any(UpdatePackageDimensionsRequestDTO.class))).thenReturn(smallDTOUpdate());

        mvc.perform(patch("/api/packages/{packId}", packId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isOk()) // Oczekujemy kodu 200 OK
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.trackingNumber").value("XYZ-SMALL")) // Upewniamy się, że się nie nadpisał
                .andExpect(jsonPath("$.data.dimensions.width").value(45))                     // Nowa wartość double
                .andExpect(jsonPath("$.data.weight").value(12.95));
    }

    @Test
    public void shouldUpdateOnlyAllowedFieldsAndIgnoreRest() throws Exception {
        UUID originalId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        UpdatePackageDimensionsRequestDTO patchFields = fieldWeightToUpdate();

        PackageDTO patchDto = updatePackageDTO();

        when(packageService.registerNewPackage(any(PackageCreateRequest.class))).thenReturn(patchDto);
        when(packageService.updatePackageFields(eq(originalId), any(UpdatePackageDimensionsRequestDTO.class))).thenReturn(patchDto);

        String jsonRequest = objectMapper.writeValueAsString(patchDto);

        mvc.perform(patch("/api/packages/{packId}", originalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.data.trackingNumber").value("TRK-2026-ORIGINAL")) // ignoruje próbę zmiany na HACKED
                .andExpect(jsonPath("$.data.dimensions.width").value(45));
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
}