package com.io.logiflow.packageConsumer.infrastructure.controllers.integrations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.io.logiflow.packageConsumer.domain.dto.request.PackageCreateRequest;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.firstBoxPackToTruck;
import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.secondBoxPackToTruck;
import static com.io.logiflow.packageConsumer.domain.dto.request.PackageCreateRequestTest.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PackageControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PackageRepository packageRepository;


    @BeforeEach
    public void setUp() { //Poprawa dane surowe sql, repository
        packageRepository.deleteAll();
        packageRepository.save(firstBoxPackToTruck());
        packageRepository.save(secondBoxPackToTruck());
    }

    @Test
    void shouldFiltersPackagesByUuid() throws Exception {
        UUID uuid = UUID.fromString("3a2b1c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d");

        mockMvc.perform(get("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("uuid", String.valueOf(UUID.fromString("3a2b1c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d")))
                        .param("page", "1")
                        .param("size", "3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(uuid.toString()));
    }

    @Test
    void shouldReturnAllPackagesWhenWeDoNotProvidedNoneParameter() throws Exception {
        mockMvc.perform(get("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(2)));
    }

    @Test
    void shouldReturnPackageFiltersByTrackingNumberCaseInsensitive() throws Exception {
        String trackingNumber = "TRK2026000000002";

        mockMvc.perform(get("/api/packages")
                        .param("trackingNumber", trackingNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value(trackingNumber));
    }

    @Test
    void shouldReturn200WhenPackageFiltersByTrackingNumberDoesNotExists() throws Exception {
        String trackingNumber = "TRK2026000000222";

        mockMvc.perform(get("/api/packages")
                        .param("trackingNumber", trackingNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    @Test
    void shouldReturnDepthBetweenPackage() throws Exception {
        mockMvc.perform(get("/api/packages")
                        .param("minDepth", "1")
                        .param("maxDepth", "7")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    void shouldReturnDepthGreaterOrEqualsTo() throws Exception {
        mockMvc.perform(get("/api/packages")
                        .param("minDepth", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000002"));
    }

    @Test
    void shouldReturnDepthLessThanOrEqualsTo() throws Exception {
        mockMvc.perform(get("/api/packages")
                        .param("maxDepth", String.valueOf(5))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000001"))
                .andExpect(jsonPath("$.data.content[1].trackingNumber").value("TRK2026000000002"))
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    void shouldReturnAllPackagesWhenBothMinAndMaxLengthAreNull() throws Exception {
        mockMvc.perform(get("/api/packages")
                        .param("minDepth", "")
                        .param("maxDepth", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000001"))
                .andExpect(jsonPath("$.data.content[1].trackingNumber").value("TRK2026000000002"));
    }

    @Test
    void shouldReturnWeightBetweenPackage() throws Exception {
        mockMvc.perform(get("/api/packages")
                        .param("minWeight", "10")
                        .param("maxWeight", "30")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000001"))
                .andExpect(jsonPath("$.data.content[1].trackingNumber").value("TRK2026000000002"));
    }

    @Test
    void shouldReturnWeightGreaterOrEqualsTo() throws Exception {
        mockMvc.perform(get("/api/packages")
                        .param("minWeight", "15")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000002"));
    }

    @Test
    void shouldReturnWeightLessThanOrEqualsTo() throws Exception {
        mockMvc.perform(get("/api/packages")
                        .param("maxWeight", "25")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000001"));
    }

    @Test
    void shouldReturnAllPackagesWhenBothMinAndMaxAreNull() throws Exception {
        mockMvc.perform(get("/api/packages")
                        .param("minWeight", "")
                        .param("maxWeight", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].trackingNumber").value("TRK2026000000001"))
                .andExpect(jsonPath("$.data.content[1].trackingNumber").value("TRK2026000000002"));
    }

    @Test
    void shouldRegisterANewPackageToSystem() throws Exception {
        PackageCreateRequest createNewPackage = exactThirdRequest();
        String jsonRequest = objectMapper.writeValueAsString(createNewPackage);

        mockMvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.trackingNumber").isString())
                .andExpect(jsonPath("$.data.dimensions.width").value(67))
                .andExpect(jsonPath("$.data.weight").value(new BigDecimal("123.0")))
                .andExpect(jsonPath("$.data.coordinates.x").value(0))
                .andExpect(jsonPath("$.data.coordinates.y").value(0))
                .andExpect(jsonPath("$.data.coordinates.z").value(0));
    }

    @Test
    void shouldGenerateUniqueTrackingNumberForEachPackage() throws Exception {
        String request1 = objectMapper.writeValueAsString(requestFirst());
        String request2 = objectMapper.writeValueAsString(requestSecond());

        MvcResult result1 = mockMvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request1))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult result2 = mockMvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request2))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode node1 = objectMapper.readTree(result1.getResponse().getContentAsString());
        JsonNode node2 = objectMapper.readTree(result2.getResponse().getContentAsString());

        String tracking1 = node1.path("data").path("trackingNumber").asText();
        String tracking2 = node2.path("data").path("trackingNumber").asText();

        Assertions.assertNotEquals(tracking1, tracking2);
    }

    @Test
    void shouldReturn400WhenBodyPackageCreateRequestIsEmpty() throws Exception {
        mockMvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenDimensionsAreMissing() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(firstBoxCreateRequest());

        mockMvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenWeightIsNegative() throws Exception {
        PackageCreateRequest request = smallRequest();
        request.setWeight(new BigDecimal("-5.0"));
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenWeightHasWrongType() throws Exception {
        String jsonRequest = "{\"dimensions\":{\"width\":10,\"height\":10,\"depth_length\":10},\"weight\":\"not-a-number\"}";

        mockMvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn415WhenContentTypeIsMissing() throws Exception {
        PackageCreateRequest request = exactThirdRequest();

        mockMvc.perform(post("/api/packages")
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldUpdateAllowedFieldsSuccessfully() throws Exception {
        UUID uuid = UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");
        Map<String, Object> path = Map.of(
                "width", 12,
                "weight", new BigDecimal(120),
                "height", 146
        );

        String jsonRequest = objectMapper.writeValueAsString(path);

        mockMvc.perform(patch("/api/packages/{packId}", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(uuid.toString()))
                .andExpect(jsonPath("$.data.dimensions.width").value(12))
                .andExpect(jsonPath("$.data.dimensions.height").value(146))
                .andExpect(jsonPath("$.data.trackingNumber").value("TRK2026000000002"));
    }

    @Test
    void shouldUpdateOnlySingleFieldWhenOnlyOneProvided() throws Exception {
        UUID uuid = UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");
        Map<String, Object> patch = Map.of("weight", new BigDecimal("99.5"));
        String jsonRequest = objectMapper.writeValueAsString(patch);

        mockMvc.perform(patch("/api/packages/{packId}", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.weight").value(new BigDecimal("99.5")));
    }

    @Test
    void shouldIgnoreBothUuidAndTrackingNumberWhenBothArePresentInPatch() throws Exception {
        UUID uuid = UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");

        Map<String, Object> patch = Map.of(
                "uuid", UUID.randomUUID().toString(),
                "trackingNumber", "TRK2026000000888",
                "height", 200
        );

        String jsonRequest = objectMapper.writeValueAsString(patch);

        mockMvc.perform(patch("/api/packages/{packId}", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(uuid.toString()))
                .andExpect(jsonPath("$.data.trackingNumber").value("TRK2026000000002"))
                .andExpect(jsonPath("$.data.dimensions.height").value(200));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentPackage() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();

        Map<String, Object> patch = Map.of("weight", new BigDecimal("50"));
        String jsonRequest = objectMapper.writeValueAsString(patch);

        mockMvc.perform(patch("/api/packages/{packId}", nonExistentUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenUuidFormatIsInvalid() throws Exception {
        Map<String, Object> patch = Map.of("weight", new BigDecimal("50"));
        String jsonRequest = objectMapper.writeValueAsString(patch);

        mockMvc.perform(patch("/api/packages/{packId}", "not-a-valid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenBodyIsEmpty() throws Exception {
        UUID uuid = UUID.fromString("7c9e0d12-5b1d-407e-8c43-1bc2d3f4a5b6");

        mockMvc.perform(patch("/api/packages/{packId}", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenFieldHasWrongType() throws Exception {
        UUID uuid = UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");

        Map<String, Object> patch = Map.of("weight", "not-a-number");
        String jsonRequest = objectMapper.writeValueAsString(patch);

        mockMvc.perform(patch("/api/packages/{packId}", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenDimensionIsNegative() throws Exception {
        UUID uuid = UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");

        Map<String, Object> patch = Map.of("width", -10);
        String jsonRequest = objectMapper.writeValueAsString(patch);

        mockMvc.perform(patch("/api/packages/{packId}", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
