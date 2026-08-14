package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.io.logiflow.packageConsumer.domain.dto.request.CreateTruckDTO;
import com.io.logiflow.packageConsumer.domain.dto.request.PackingRequestDTO;
import com.io.logiflow.packageConsumer.domain.dto.request.TruckFilterDto;
import com.io.logiflow.packageConsumer.domain.dto.response.TruckDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.TruckResponseDTO;
import com.io.logiflow.packageConsumer.exception.ApiResponse;
import com.io.logiflow.packageConsumer.infrastructure.service.TruckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trucks")
@RequiredArgsConstructor
public class TruckController {

    private final TruckService truckService;

    @Operation(
            summary = "Pobranie szczegółów paczki po wybranych kryteriach",
            description = "W przypadku braku wyników zwraca pustą stronę ze statusem 200 OK."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Znaleziono paczkę i pomyślnie zwrócono jej dane",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)) // Tutaj OpenAPI pokaże Twój obiekt DTO
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Niepoprawne parametry filtrowania, paginacji lub sortowania (np. page < 0, zły format daty)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
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

    @Operation(summary = "Dodanie paczki do ciężarówki", description = "Do istniejącej ciężarówki  w systemie dodaje paczkę po wcześniejszym sprawdzeniu wymiarów i wagi")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Paczka została pomyślnie dodana do ciężarówki",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Nieprawidłowe dane wejściowe (np. ujemne wymiary, brak wymaganych pól)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Paczka o podanym numerze śledzenia/etykiecie już znajduję się na ciężarówce",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/pack")
    public ResponseEntity<ApiResponse<Void>> packPackages(
            @RequestBody PackingRequestDTO packages) {
        truckService.addPackagesToTruck(packages);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Dodanie nowej ciężarówki", description = "Tworzy nową ciężarówkę  w systemie po wcześniejszej walidacji wymiarów i unikalności.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Ciężarówka została pomyślnie utworzona",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Nieprawidłowe dane wejściowe (np. ujemne wymiary, brak wymaganych pól)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Ciężarówka o podanym numerze śledzenia/etykiecie już istnieje w bazie",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping()
    public ResponseEntity<ApiResponse<TruckResponseDTO>> createTruck(
            @RequestBody CreateTruckDTO newTruck) {
        TruckResponseDTO addTruck = truckService.addTruck(newTruck);
        return ResponseEntity.ok(ApiResponse.success(addTruck));
    }
}