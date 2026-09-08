package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.io.logiflow.packageConsumer.domain.dto.request.CreateTruckDTO;
import com.io.logiflow.packageConsumer.domain.dto.request.PackingRequestDTO;
import com.io.logiflow.packageConsumer.domain.dto.request.TruckFilterDto;
import com.io.logiflow.packageConsumer.domain.dto.response.TruckDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.TruckResponseDTO;
import com.io.logiflow.packageConsumer.exception.web.api.common.ApiResponse;
import com.io.logiflow.packageConsumer.infrastructure.service.TruckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trucks")
@RequiredArgsConstructor
public class TruckController {

    private final TruckService truckService;

    @Operation(
            summary = "Download truck details based on selected criteria",
            description = "If no results are found, returns an empty page with a 200 OK status."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "A truck was found, and its information was successfully returned",
                    content = @Content(schema = @Schema(implementation = TruckDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Invalid filtering, pagination, or sorting parameters (e.g., page < 0, incorrect date format)",
                    content = @Content(schema = @Schema(implementation = TruckDTO.class))
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TruckDTO>>> getAllTrucks(
            @ModelAttribute TruckFilterDto filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<TruckDTO> trucks = truckService.getTrucks(filter, page, size);
        return ResponseEntity.ok(ApiResponse.success(trucks));
    }

    @Operation(summary = "Adding a package to a truck ", description = "It adds a package to an existing truck in the system after first checking its exteriorDimensions and weight")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "The package has been successfully loaded onto the truck",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data (dimension validation error)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "The package with the tracking number provided is already on the truck",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/pack")
    public ResponseEntity<ApiResponse<Void>> packPackages(
            @RequestBody PackingRequestDTO packages) {
        truckService.addPackagesToTruck(packages);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(null));
    }

    @Operation(summary = "Add a new truck", description = "It creates a new truck in the system after first validating its exteriorDimensions and uniqueness.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "The truck was successfully created",
                    content = @Content(schema = @Schema(implementation = TruckResponseDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data (dimension validation error)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "The truck with the provided tracking number already exists in the database.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping()
    public ResponseEntity<ApiResponse<TruckResponseDTO>> createTruck(
            @RequestBody CreateTruckDTO newTruck) {
        TruckResponseDTO addTruck = truckService.createTruck(newTruck);
        return ResponseEntity.ok(ApiResponse.success(addTruck));
    }
}