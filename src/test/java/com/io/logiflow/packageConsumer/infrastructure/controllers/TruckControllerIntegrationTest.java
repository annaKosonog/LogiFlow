package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dto.PackageFixtureDTO.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TruckControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnResourcesWithPagination() throws Exception {
        mockMvc.perform(get("/api/trucks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3));
    }

    @Test
    void shouldUseDefaultPaginationValues() throws Exception {
        mockMvc.perform(get("/api/trucks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0))          // numer strony
                .andExpect(jsonPath("$.size").value(10))           // rozmiar strony
                .andExpect(jsonPath("$.content", hasSize(6)))               // Lista elementów na stronie, tutaj 6
                .andExpect(jsonPath("$.first").value(true))    // Czy to pierwsza strona 0 (pierwsza)
                .andExpect(jsonPath("$.last").value(true));  //Czy to jest ostatnia strona z paginacji true tak
    }

    @Test
    void shouldReturnTruckByFilterLength() throws Exception {
        mockMvc.perform(get("/api/trucks/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("length", "600"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].idTruck").value("493afc07-d433-4cfa-8947-0c6100c127df"))
                .andExpect(jsonPath("$.data[1].idTruck").value("2b8ecd55-c636-4ba0-8192-42c9e8a14355"))
                .andExpect(jsonPath("$.data.size()").value(2));
    }

    @Test
    void shouldReturn200WhenParamLengthDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/trucks/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("length", "1250"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(0));
    }

    @Test
    void shouldReturn500WhenFieldLengthHasWrongType() throws Exception {
        mockMvc.perform(get("/api/trucks/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("length", "Ala ma kota"))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Wystąpił nieoczekiwany błąd systemu"));
    }

    @Test
    void shouldReturn404WhenLengthIsNegative() throws Exception {
        mockMvc.perform(get("/api/trucks/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("length", "-2"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Length must be greater than 0."));
    }

    @Test
    void shouldReturn404WhenWeProvidedDefaultValueOnly() throws Exception {
        mockMvc.perform(get("/api/trucks/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("length", "-1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Provide at least one filter."));
    }

    @Test
    void shouldReturnWeightWhenDataIsCorrect() throws Exception {
        mockMvc.perform(get("/api/trucks/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("weight", "3500"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(1))
                .andExpect(jsonPath("$.data[0].idTruck").value("493afc07-d433-4cfa-8947-0c6100c127df"))
                .andExpect(jsonPath("$.data[0].dimensionsDTO.width").value(240));
    }

    @Test
    void shouldReturn200WhenParamWeightDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/trucks/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("weight", "1234"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(0));
    }

    @Test
    void shouldReturn500WhenFieldWeightHasWrongType() throws Exception {
        mockMvc.perform(get("/api/trucks/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("weight", "Ala ma kota"))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Wystąpił nieoczekiwany błąd systemu"));
    }

    @Test
    void shouldReturnExceptionWhenWeightIsNegative() throws Exception {
        mockMvc.perform(get("/api/trucks/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("weight", "-1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Weight cannot be null or negative."));
    }

    @Test
    void shouldReturn200WhenWeProvidedTwoParametersLengthDefaultValueAndWeightCorrectParametr() throws Exception {
        mockMvc.perform(get("/api/trucks/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("weight", "3500")
                        .param("length", "-1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(1))
                .andExpect(jsonPath("$.data[0].idTruck").value("493afc07-d433-4cfa-8947-0c6100c127df"))
                .andExpect(jsonPath("$.data[0].dimensionsDTO.width").value(240));
    }

    @Test
    void shouldReturn200WhenAddNewPackageToTruckThatIsNotAssignedToATruck() throws Exception {
        UUID truckId = UUID.fromString("bb541dbd-3206-4f7e-b23c-ae6f9af78db5");
        List<PackageDTO> listPackageToPack = new ArrayList<>();
        listPackageToPack.add(thirdPackageToPack());

        String body = objectMapper.writeValueAsString(listPackageToPack);


        mockMvc.perform(post("/api/trucks/{truckId}", truckId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("The packing process has been successfully completed for the truck with ID " + truckId));


    }

    @Test
    void shouldReturn409WhenAddPackageToTruckWhichAdd() throws Exception {
        UUID truckId = UUID.fromString("bb541dbd-3206-4f7e-b23c-ae6f9af78db5");

        List<PackageDTO> listPackageToPack = new ArrayList<>();
        listPackageToPack.add(firstPackageToPack());
        listPackageToPack.add(secondPackageToPack());

        String body = objectMapper.writeValueAsString(listPackageToPack);


        mockMvc.perform(post("/api/trucks/{truckId}", truckId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("The package with the number TRK2026000000004 has already been loaded onto the truck with ID: " + 2));
    }


}
