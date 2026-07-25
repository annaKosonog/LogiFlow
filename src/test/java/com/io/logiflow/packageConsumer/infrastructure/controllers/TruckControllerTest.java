package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dto.TruckDTO;
import com.io.logiflow.packageConsumer.infrastructure.service.TruckService;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.*;
import static com.io.logiflow.packageConsumer.domain.dto.TruckDTOFixture.createLoadedTruckDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void shouldReturnDefaultPageableWhenNoParamsProvided() throws Exception {
        TruckDTO mockPackDto = createLoadedTruckDTO();

        Page<TruckDTO> page = new PageImpl<>(
                List.of(mockPackDto),
                PageRequest.of(0, 10),
                1
        );

        //Sprawdzanie czy dla domyślnych wartości (0,10) serwis zostanie wywołany
        when(truckService.getTrucks(0, 10)).thenReturn(page);

        mvc.perform(get("/api/trucks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].idTruck").value("528e0004-a6f9-4018-bcba-87e20ad08606"))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0)) // To sprawdza Twoje 'page' (defaultValue = 0)
                .andExpect(jsonPath("$.pageable.pageSize").value(10));
    }

    @Test
    void shouldReturnCustomPageableWhenParamsProvided() throws Exception {
        int customPage = 2;
        int customSize = 5;

        TruckDTO mockPackDto = createLoadedTruckDTO();

        Page<TruckDTO> page = new PageImpl<>(
                List.of(mockPackDto));

        // Konfigurujemy mock dla niestandardowych wartości 2 i 5
        when(truckService.getTrucks(customPage, customSize)).thenReturn(page);

        mvc.perform(get("/api/trucks")
                        .param("page", String.valueOf(customPage))
                        .param("size", String.valueOf(customSize))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].idTruck").value("528e0004-a6f9-4018-bcba-87e20ad08606"))
                .andExpect(jsonPath("$.size").value(1)) // rozmiar listy w content
                .andExpect(jsonPath("$.empty").value(false));

        // Dodatkowa weryfikacja: upewniamy się, że serwis został wywołany dokładnie z tymi parametrami
        Mockito.verify(truckService, Mockito.times(1)).getTrucks(customPage, customSize);
    }


    @Test
    public void shouldAddPackagesToTruckSuccessfully() throws Exception {
        List<Package> packageList = Arrays.asList(firstBoxPackToTruck(), secondBoxPackToTruck());

        String jsonRequest = objectMapper.writeValueAsString(packageList);
        UUID truckId = UUID.fromString("528e0004-a6f9-4018-bcba-87e20ad08606");

        MvcResult result = mvc.perform(post("/api/trucks/{truckId}", truckId)
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
        gigantPackage.setUuid(UUID.randomUUID());
        gigantPackage.setTrackingNumber("TRK-OVERWEIGHT");
        gigantPackage.setWeight(new BigDecimal("5000.0"));

        List<Package> packageList = List.of(gigantPackage);
        UUID truckId = UUID.randomUUID();

        String jsonRequest = objectMapper.writeValueAsString(packageList);

        Mockito.doThrow(new IllegalArgumentException("Truck loading failed"))
                .when(truckService).addPackagesToTruck(Mockito.anyList(), Mockito.any(UUID.class));

        mvc.perform(post("/api/trucks/{truckId}", truckId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }


}
