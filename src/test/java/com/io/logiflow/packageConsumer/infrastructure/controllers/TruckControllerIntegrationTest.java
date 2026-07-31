package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.domain.repository.TruckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.thirdBoxPackToTruck;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedSecondTruck;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedTruck;
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

    @Autowired
    private TruckRepository truckRepository;

    @Autowired
    private PackageRepository packageRepository;

    @BeforeEach
    public void setUp() { //Poprawa dane surowe sql, repository
        truckRepository.deleteAll();
        packageRepository.deleteAll();
        truckRepository.save(createLoadedTruck());
        truckRepository.save(createLoadedSecondTruck());
        packageRepository.save(thirdBoxPackToTruck());
    }

    @Test
    void shouldReturnResourcesWithPagination() throws Exception {
        mockMvc.perform(get("/api/trucks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    void shouldUseDefaultPaginationValues() throws Exception {
        mockMvc.perform(get("/api/trucks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(0))          // numer strony
                .andExpect(jsonPath("$.data.pageable.pageSize").value(5))           // rozmiar strony
                .andExpect(jsonPath("$.data.content", hasSize(2)))               // Lista elementów na stronie, tutaj 6
                .andExpect(jsonPath("$.data.first").value(true))    // Czy to pierwsza strona 0 (pierwsza)
                .andExpect(jsonPath("$.data.last").value(true));  //Czy to jest ostatnia strona z paginacji true tak
    }

    @Test
    void shouldReturnTruckByFilterLengthMinAndMax() throws Exception {
        mockMvc.perform(get("/api/trucks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("minLength", "175")
                        .param("maxLength", "600"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].idTruck").value("528e0004-a6f9-4018-bcba-87e20ad08606"))
                .andExpect(jsonPath("$.data.content[1].idTruck").value("c771f079-fb74-4811-b491-e78673c41e0d"))
                .andExpect(jsonPath("$.data.content", hasSize(2)));
    }

    @Test
    void shouldReturn200WhenParamLengthMin() throws Exception {
        mockMvc.perform(get("/api/trucks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("minLength", "178"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].idTruck").value("528e0004-a6f9-4018-bcba-87e20ad08606"))
                .andExpect(jsonPath("$.data.content[1].idTruck").value("c771f079-fb74-4811-b491-e78673c41e0d"))
                .andExpect(jsonPath("$.data.content", hasSize(2)));
    }

    @Test
    void shouldReturn400WhenFieldLengthHasWrongType() throws Exception {
        mockMvc.perform(get("/api/trucks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("minLength", "Ala ma kota"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200WhenMaxLengthIsCorrect() throws Exception {
        mockMvc.perform(get("/api/trucks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("maxLength", "450"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].idTruck").value("c771f079-fb74-4811-b491-e78673c41e0d"));
    }

    @Test
    void shouldReturn200WhenWeProvidedMaxAndMinMaxPayloadTruck() throws Exception {
        mockMvc.perform(get("/api/trucks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("minMaxPayload", "100")
                        .param("minMaxPayload", "220"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].idTruck").value("528e0004-a6f9-4018-bcba-87e20ad08606"))
                .andExpect(jsonPath("$.data.content[1].idTruck").value("c771f079-fb74-4811-b491-e78673c41e0d"))
                .andExpect(jsonPath("$.data.content", hasSize(2)));
    }

    @Test
    void shouldReturn200WhenWeProvidedMinMaxPayloadTruck() throws Exception {
        mockMvc.perform(get("/api/trucks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("minMaxPayload", "199"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].idTruck").value("528e0004-a6f9-4018-bcba-87e20ad08606"));
    }

    @Test
    void shouldReturn200WhenParamUUIDDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(get("/api/trucks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("idTruck", String.valueOf(id)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(2)));
    }

    @Test
    void shouldReturn400WhenFieldWeightHasWrongType() throws Exception {
        mockMvc.perform(get("/api/trucks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("maxWeight", "Ala ma kota"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200WhenAddNewPackageToTruckThatIsNotAssignedToATruck() throws Exception {
        UUID idTruck = UUID.fromString("528e0004-a6f9-4018-bcba-87e20ad08606");
        List<PackageDTO> listPackageToPack = new ArrayList<>();
        listPackageToPack.add(thirdBoxDTOPackToTruck());

        String body = objectMapper.writeValueAsString(listPackageToPack);


        mockMvc.perform(post("/api/trucks/{idTruck}", idTruck)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn409WhenAddPackageToTruckWhichAdd() throws Exception {
        UUID idTruck = UUID.fromString("528e0004-a6f9-4018-bcba-87e20ad08606");

        List<PackageDTO> listPackageToPack = new ArrayList<>();
        listPackageToPack.add(savedToDb());
        listPackageToPack.add(secondPackageToPack());

        String body = objectMapper.writeValueAsString(listPackageToPack);


        mockMvc.perform(post("/api/trucks/{idTruck}", idTruck)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors").value("Unable to load: One or more packages do not exist in the database!"));
    }
}
