package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.io.logiflow.packageConsumer.domain.dto.request.PackageCreateRequest;
import com.io.logiflow.packageConsumer.domain.dto.request.PackageFilterDTO;
import com.io.logiflow.packageConsumer.domain.dto.request.UpdatePackageDimensionsRequestDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.PackageDTO;
import com.io.logiflow.packageConsumer.exception.ApiResponse;
import com.io.logiflow.packageConsumer.infrastructure.service.PackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Pobierz listę dostępnych paczek oraz wyfiltruj po wybranych  kryteriach")
    @GetMapping
    ResponseEntity<ApiResponse<Page<PackageDTO>>> getAllPackages(
            @ModelAttribute PackageFilterDTO filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<PackageDTO> packPage = packingService.getPackages(filter, page, size);
        return ResponseEntity.ok(ApiResponse.success(packPage));
    }

    @Operation(summary = "Dodanie nowej paczki", description = "Tworzy nową paczkę w systemie po wcześniejszej walidacji wymiarów i unikalności.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Paczka została pomyślnie utworzona",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Nieprawidłowe dane wejściowe (np. ujemne wymiary, brak wymaganych pól)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Paczka o podanym numerze śledzenia/etykiecie już istnieje w bazie",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<PackageDTO>> registerPackage(@RequestBody PackageCreateRequest createRequest) {
        PackageDTO savedPackage = packingService.registerNewPackage(createRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(savedPackage));
    }

    @Operation(summary = "Aktualizacja danych paczki")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "Zasób został pomyślnie zaktualizowany (brak ciała odpowiedzi)",
                    content = @Content // Informuje Swaggera, że odpowiedź NIE MA ciała
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Nie znaleziono paczki o podanym ID",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Niepoprawne dane wejściowe (błąd walidacji wymiarów)",
                    content = @Content
            )
    })
    @PatchMapping("/{packId}")
    public ResponseEntity<ApiResponse<PackageDTO>> updateDimensions(
            @PathVariable UUID packId,
            @RequestBody UpdatePackageDimensionsRequestDTO fields) {
        PackageDTO updated = packingService.updatePackageFields(packId, fields);
        return ResponseEntity
                .ok(ApiResponse.success(updated));
    }
}