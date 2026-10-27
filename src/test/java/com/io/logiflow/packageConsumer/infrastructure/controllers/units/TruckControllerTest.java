package com.io.logiflow.packageConsumer.infrastructure.controllers.units;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.io.logiflow.packageConsumer.infrastructure.dao.Package;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.CreateTruckDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.PackingRequestDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.TruckFilterDto;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.TruckDTO;
import com.io.logiflow.packageConsumer.infrastructure.repository.TruckJpaRepository;
import com.io.logiflow.packageConsumer.infrastructure.controllers.TruckController;
import com.io.logiflow.packageConsumer.application.service.TruckService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.largeBox;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedSecondTruck;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedTruck;
import static com.io.logiflow.packageConsumer.domain.dto.request.CreateTruckDTOFixture.createThirdTruck;
import static com.io.logiflow.packageConsumer.domain.dto.request.PackingRequestDTOFixture.firstPackingToPack;
import static com.io.logiflow.packageConsumer.domain.dto.response.PackingResultDTOFixture.firstAddPacking;
import static com.io.logiflow.packageConsumer.domain.dto.response.TruckDTOFixture.createLoadedTruckDTO;
import static com.io.logiflow.packageConsumer.domain.dto.response.TruckDTOFixture.createSecondLoadedTruckDTO;
import static com.io.logiflow.packageConsumer.domain.dto.response.TruckResponseDTOFixture.thirdCreateTruckResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TruckController.class)
public class TruckControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private TruckService truckService;

    @MockBean
    private TruckJpaRepository truckJpaRepository;

    TruckDTO mockTruckDto;
    TruckDTO secondMockTruckDto;
    Page<TruckDTO> defaultPage;

    @BeforeEach
    void setUp() {
        mockTruckDto = createLoadedTruckDTO();
        secondMockTruckDto = createSecondLoadedTruckDTO();
    }

    @AfterEach
    void cleanUp() {
        truckJpaRepository.deleteAll();
        truckJpaRepository.flush();
    }

    @Test
    void shouldReturnDefaultPageableWhenNoParamsProvided() throws Exception {
        defaultPage = new PageImpl<>(List.of(mockTruckDto, secondMockTruckDto), PageRequest.of(1, 5), 2);

        //Sprawdzanie czy dla domyślnych wartości (0,10) serwis zostanie wywołany
        when(truckService.getTrucks(any(TruckFilterDto.class), eq(1), eq(5))).thenReturn(defaultPage);

        mvc.perform(get("/api/trucks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].idTruck").value("528e0004-a6f9-4018-bcba-87e20ad08606"))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(1)) // To sprawdza Twoje 'page' (defaultValue = 0)
                .andExpect(jsonPath("$.data.pageable.pageSize").value(5));
    }

    @Test
    void shouldReturnCustomPageableWhenParamsProvided() throws Exception {
        defaultPage = new PageImpl<>(List.of(mockTruckDto, secondMockTruckDto), PageRequest.of(1, 5), 2);

        int customPage = 2;
        int customSize = 5;

        TruckDTO mockPackDto = createLoadedTruckDTO();

        // Konfigurujemy mock dla niestandardowych wartości 2 i 5
        when(truckService.getTrucks(any(TruckFilterDto.class), eq(customPage), eq(customSize))).thenReturn(defaultPage);

        mvc.perform(get("/api/trucks")
                        .param("page", String.valueOf(customPage))
                        .param("size", String.valueOf(customSize))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].idTruck").value("528e0004-a6f9-4018-bcba-87e20ad08606"))
                .andExpect(jsonPath("$.data.pageable.pageSize").value(5)) // rozmiar listy w content
                .andExpect(jsonPath("$.data.empty").value(false));
    }

    @Test
    public void shouldAddPackagesToTruckSuccessfully() throws Exception {
        PackingRequestDTO toPack = firstPackingToPack();

        when(truckService.addPackagesToTruck(any(PackingRequestDTO.class))).thenReturn(firstAddPacking());

        String jsonRequest = objectMapper.writeValueAsString(toPack);


        MvcResult result = mvc.perform(post("/api/trucks/pack")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();

        int status = result.getResponse().getStatus();
        assertEquals(201, status);
    }

    @Test
    public void shouldReturnBadRequestWhenPackageDoesNotFit() throws Exception {
        Package gigantPackage = largeBox();
        gigantPackage.setUuid(UUID.randomUUID());
        gigantPackage.setTrackingNumber("TRK-OVERWEIGHT");
        gigantPackage.setWeight(new BigDecimal("5000.0"));

        List<UUID> packageList = List.of(gigantPackage.getUuid());
        UUID truckId = UUID.randomUUID();
        PackingRequestDTO request = new PackingRequestDTO(packageList, truckId);

        String jsonRequest = objectMapper.writeValueAsString(request);

        Mockito.doThrow(new IllegalArgumentException("Truck loading failed"))
                .when(truckService).addPackagesToTruck(Mockito.any(PackingRequestDTO.class));

        mvc.perform(post("/api/trucks/pack")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnTruckDtoWhenProvidedCorrectUUID() throws Exception {
        UUID id = UUID.fromString("528e0004-a6f9-4018-bcba-87e20ad08606");
        defaultPage = new PageImpl<>(List.of(mockTruckDto), PageRequest.of(1, 5), 2);

        when(truckJpaRepository.findTruckByUuid(id)).thenReturn(Optional.of(createLoadedTruck()));
        when(truckService.getTrucks(any(TruckFilterDto.class), eq(1), eq(5))).thenReturn(defaultPage);

        mvc.perform(get("/api/trucks")
                        .param("idTruck", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].dimensionsDTO.width").value(250))
                .andExpect(jsonPath("$.data.content[0].currentWeight").value(new BigDecimal("80")));
    }

    @Test
    public void shouldReturnTruckDTOWhenProvidedNoUUID() throws Exception {
        UUID id = UUID.randomUUID();
        defaultPage = new PageImpl<>(List.of(), PageRequest.of(1, 5), 2);

        when(truckJpaRepository.findTruckByUuid(id)).thenReturn(Optional.empty());
        when(truckService.getTrucks(any(TruckFilterDto.class), eq(1), eq(5))).thenReturn(defaultPage);

        mvc.perform(get("/api/trucks")
                        .param("idTruck", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    @Test
    public void shouldReturnTruckDTOWhenProvidedCorrectMinAndMaxHeight() throws Exception {

        BigDecimal minHeight = new BigDecimal("190");
        BigDecimal maxHeight = new BigDecimal("225");
        defaultPage = new PageImpl<>(List.of(mockTruckDto, secondMockTruckDto), PageRequest.of(1, 5), 2);

        when(truckJpaRepository.findAll()).thenReturn(List.of(createLoadedTruck(), createLoadedSecondTruck()));
        when(truckService.getTrucks(any(TruckFilterDto.class), eq(1), eq(5))).thenReturn(defaultPage);


        mvc.perform(get("/api/trucks")
                        .param("minHeight", String.valueOf(minHeight))
                        .param("maxHeight", String.valueOf(maxHeight))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].idTruck").value("528e0004-a6f9-4018-bcba-87e20ad08606"))
                .andExpect(jsonPath("$.data.content[1].idTruck").value("c771f079-fb74-4811-b491-e78673c41e0d"))
                .andExpect(jsonPath("$.data.size").value(5));
    }

    @Test
    public void shouldReturnTruckDTOWhenProvidedCorrectMinHeight() throws Exception {
        BigDecimal minHeight = new BigDecimal("225");
        defaultPage = new PageImpl<>(List.of(mockTruckDto), PageRequest.of(1, 5), 2);

        when(truckJpaRepository.findAll()).thenReturn(List.of(createLoadedTruck()));
        when(truckService.getTrucks(any(TruckFilterDto.class), eq(1), eq(5))).thenReturn(defaultPage);


        mvc.perform(get("/api/trucks")
                        .param("minHeight", String.valueOf(minHeight))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].idTruck").value("528e0004-a6f9-4018-bcba-87e20ad08606"))
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    @Test
    public void shouldReturnTruckDTOWhenProvidedCorrectMaxHeight() throws Exception {
        BigDecimal maxHeight = new BigDecimal("10");
        defaultPage = new PageImpl<>(List.of(), PageRequest.of(1, 5), 2);

        when(truckJpaRepository.findAll()).thenReturn(List.of(createLoadedTruck(), createLoadedSecondTruck()));
        when(truckService.getTrucks(any(TruckFilterDto.class), eq(1), eq(5))).thenReturn(defaultPage);

        mvc.perform(get("/api/trucks")
                        .param("maxHeight", String.valueOf(maxHeight))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(0));
    }

    @Test
    public void shouldReturnANewTruckWhenProvidedCorrectParameter() throws Exception {
        CreateTruckDTO aNewTruck = createThirdTruck();
        when(truckService.createTruck(any(CreateTruckDTO.class))).thenReturn(thirdCreateTruckResponse());

        String jsonRequest = objectMapper.writeValueAsString(aNewTruck);
        mvc.perform(post("/api/trucks")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.licensePlate").value("KR 12345"))
                .andExpect(jsonPath("$.data.dimensions.width").value(250))
                .andExpect(jsonPath("$.data.maxPayload").value(200));
    }
}
