package com.travello.tripsservice.controller;

import com.travello.tripsservice.dto.TripCreateDTO;
import com.travello.tripsservice.dto.TripDTO;
import com.travello.tripsservice.dto.TripDetailsDTO;
import com.travello.tripsservice.dto.TripUpdateDTO;
import com.travello.tripsservice.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Trips", description = "Trip management endpoints")
@RestController
@RequestMapping("trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @Operation(summary = "Create a new trip")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Trip created, returns new trip ID",
                    content = @Content(schema = @Schema(type = "string", format = "uuid"))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/")
    public UUID createTrip(
            @RequestBody TripCreateDTO tripCreateDTO,
            @RequestHeader("Authorization") String authHeader) {
        return tripService.createTrip(tripCreateDTO, authHeader);
    }

    @Operation(summary = "Get all trips")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of all trips",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TripDTO.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping("/")
    public List<TripDTO> getAllTrips() {
        return tripService.getTrips();
    }

    @Operation(summary = "Get trips for the authenticated traveller")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of trips belonging to the current traveller",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TripDTO.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping("/traveller")
    public List<TripDTO> getTripsByTraveller(
            @RequestHeader("Authorization") String authHeader) {
        return tripService.getTripsByTraveller(authHeader);
    }

    @Operation(summary = "Get trip by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Trip details",
                    content = @Content(schema = @Schema(implementation = TripDetailsDTO.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trip not found", content = @Content)
    })
    @GetMapping("/{tripId}")
    public TripDetailsDTO getTripById(
            @Parameter(description = "ID of the trip", required = true)
            @PathVariable UUID tripId,
            @RequestHeader("Authorization") String authHeader) {
        return tripService.getTripById(tripId, authHeader);
    }

    @Operation(summary = "Update a trip by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Trip updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trip not found", content = @Content)
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{tripId}")
    public void updateTrip(
            @Parameter(description = "ID of the trip", required = true)
            @PathVariable UUID tripId,
            @RequestBody TripUpdateDTO tripUpdateDTO,
            @RequestHeader("Authorization") String authHeader) {
        tripService.updateTripById(tripId, tripUpdateDTO, authHeader);
    }

    @Operation(summary = "Delete a trip by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Number of deleted rows"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trip not found", content = @Content)
    })
    @DeleteMapping("/{tripId}")
    public int deleteTrip(
            @Parameter(description = "ID of the trip", required = true)
            @PathVariable UUID tripId,
            @RequestHeader("Authorization") String authHeader) {
        return tripService.deleteTripById(tripId, authHeader);
    }
}