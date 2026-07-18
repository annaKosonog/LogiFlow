package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.io.logiflow.packageConsumer.domain.dto.PackageCreateRequest;
import com.io.logiflow.packageConsumer.infrastructure.service.PackageService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dto.PackageCreateRequestTest.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PackageControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PackageService packageService;


    @BeforeEach
    public void setUp() {
        packageService.registerNewPackage(exactRequest());
        packageService.registerNewPackage(exactSecondRequest());
    }

    @AfterAll
    static void cleanUpOnce(@Autowired PackageService packageService) {
        packageService.deleteAllPackage();
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
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.trackingNumber").isString())
                .andExpect(jsonPath("$.dimensions.width").value(67))
                .andExpect(jsonPath("$.weight").value(new BigDecimal("123.0")))
                .andExpect(jsonPath("$.coordinates.x").value(0))
                .andExpect(jsonPath("$.coordinates.y").value(0))
                .andExpect(jsonPath("$.coordinates.z").value(0));
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

        String tracking1 = objectMapper.readTree(result1.getResponse().getContentAsString())
                .get("trackingNumber").asText();
        String tracking2 = objectMapper.readTree(result2.getResponse().getContentAsString())
                .get("trackingNumber").asText();

        assertNotEquals(tracking1, tracking2);
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
    void shouldReturnResourcesWithPagination() throws Exception {
        mockMvc.perform(get("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3));
    }

    @Test
    void shouldUseDefaultPaginationValues() throws Exception {
        mockMvc.perform(get("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0))          // numer strony
                .andExpect(jsonPath("$.size").value(10))           // rozmiar strony
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    void shouldReturnPackageWhenUuidExists() throws Exception {
        UUID uuid = UUID.fromString("7c9e0d12-5b1d-407e-8c43-1bc2d3f4a5b6");
        mockMvc.perform(get("/api/packages/{uuid}", uuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.trackingNumber").value("TRK2026000000004"))
                .andExpect(jsonPath("$.truckID").value(2));
    }

    @Test
    void shouldReturn404WhenUuidIsNull() throws Exception {
        mockMvc.perform(get("/api/packages/{uuid}", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenUuidDoesNotExist() throws Exception {
        String nonExistentUuid = UUID.randomUUID().toString();
        MvcResult result = mockMvc.perform(get("/api/packages/{uuid}", nonExistentUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();

        assertTrue(responseBody.contains("Package with UUID number " + nonExistentUuid +
                " not found"));
    }

    @Test
    void shouldReturnPackageWhenTrackingNumberExists() throws Exception {
        String trackingNumber = "TRK2026000000002";
        mockMvc.perform(get("/api/packages/tracking/{trackingNumber}", trackingNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.trackingNumber").value(trackingNumber))
                .andExpect(jsonPath("$.truckID").value(1));
    }

    @Test
    void shouldReturn404WhenTrackingNumberDoesNotExist() throws Exception {
        String nonExistentTrackingNumber = "TRK2026000000111";
        MvcResult result = mockMvc.perform(get("/api/packages/tracking/{trackingNumber}", nonExistentTrackingNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();

        assertTrue(responseBody.contains("Package with tracking number " + nonExistentTrackingNumber +
                " not found"));
    }

    @Test
    void shouldUpdateAllowedFieldsSuccessfully() throws Exception {
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
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
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.dimensions.width").value(12))
                .andExpect(jsonPath("$.dimensions.height").value(146))
                .andExpect(jsonPath("$.trackingNumber").value("TRK2026000000001"));
    }

    @Test
    void shouldUpdateOnlySingleFieldWhenOnlyOneProvided() throws Exception {
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        Map<String, Object> patch = Map.of("weight", new BigDecimal("99.5"));
        String jsonRequest = objectMapper.writeValueAsString(patch);

        mockMvc.perform(patch("/api/packages/{packId}", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weight").value(new BigDecimal("99.5")));
    }

    @Test
    void shouldIgnoreBothUuidAndTrackingNumberWhenBothArePresentInPatch() throws Exception {
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

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
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.trackingNumber").value("TRK2026000000001"))
                .andExpect(jsonPath("$.dimensions.height").value(200));
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
        UUID uuid = UUID.fromString("7c9e0d12-5b1d-407e-8c43-1bc2d3f4a5b6");

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
        UUID uuid = UUID.fromString("7c9e0d12-5b1d-407e-8c43-1bc2d3f4a5b6");

        Map<String, Object> patch = Map.of("width", -10);
        String jsonRequest = objectMapper.writeValueAsString(patch);

        mockMvc.perform(patch("/api/packages/{packId}", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
