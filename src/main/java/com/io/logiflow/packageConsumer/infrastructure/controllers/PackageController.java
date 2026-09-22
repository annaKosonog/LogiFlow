package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.io.logiflow.packageConsumer.domain.dto.request.PackageCreateRequest;
import com.io.logiflow.packageConsumer.domain.dto.request.PackageFilterDTO;
import com.io.logiflow.packageConsumer.domain.dto.request.UpdatePackageDimensionsRequestDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.PackageDTO;
import com.io.logiflow.packageConsumer.exception.web.api.common.ApiResponse;
import com.io.logiflow.packageConsumer.infrastructure.service.PackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packingService;

    @Operation(summary = "Download the list of available packages and filter them by your chosen criteria")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "A pack was found, and its information was successfully returned",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Invalid filtering, pagination, or sorting parameters (e.g., page < 0, incorrect date format)",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class))
            )
    })
    @GetMapping
    ResponseEntity<ApiResponse<Page<PackageDTO>>> getAllPackages(
            @ModelAttribute PackageFilterDTO filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<PackageDTO> packPage = packingService.getPackages(filter, page, size);
        return ResponseEntity.ok(ApiResponse.success(packPage));
    }

    @Operation(summary = "Add new package", description = "It creates a new package in the system after first validating its exteriorDimensions and uniqueness.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "The package was successfully created",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data (dimension validation error)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "A package with the provided tracking number already exists in the database",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<PackageDTO>> registerPackage(@Valid @RequestBody PackageCreateRequest createRequest) {
        PackageDTO savedPackage = packingService.registerNewPackage(createRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(savedPackage));
    }

    @Operation(summary = "Updating package data")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "The resource has been successfully updated ",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "No package with the provided ID was found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data (dimension validation error)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PatchMapping("/{packId}")
    public ResponseEntity<ApiResponse<PackageDTO>> updateDimensions(
            @PathVariable UUID packId,
            @Valid @RequestBody UpdatePackageDimensionsRequestDTO fields) {
        PackageDTO updated = packingService.updatePackageFields(packId, fields);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updated));
    }
}

//todo przetestować wielowątkowość optimistic lock Exception