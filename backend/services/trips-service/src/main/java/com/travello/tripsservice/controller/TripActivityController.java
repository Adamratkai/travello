package com.travello.tripsservice.controller;

import com.travello.tripsservice.dto.TripActivityCreateDTO;
import com.travello.tripsservice.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Trip Activities", description = "Trip activity management endpoints")
@RestController
@RequestMapping("trips/trip-activities")
public class TripActivityController {

    private final TripService tripService;

    public TripActivityController(TripService tripService) {
        this.tripService = tripService;
    }

    @Operation(summary = "Add an activity to a trip")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Activity added, returns new activity ID",
                    content = @Content(schema = @Schema(type = "string", format = "uuid"))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trip not found", content = @Content)
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{tripId}")
    public UUID addTripActivity(
            @Parameter(description = "ID of the trip", required = true)
            @PathVariable UUID tripId,
            @RequestBody TripActivityCreateDTO tripActivityCreateDTO,
            @RequestHeader("Authorization") String authHeader) {
        return tripService.addTripActivity(tripId, tripActivityCreateDTO, authHeader);
    }

    @Operation(summary = "Delete a trip activity by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Number of deleted rows"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Activity not found", content = @Content)
    })
    @DeleteMapping("/{tripActivityId}")
    public int deleteTripActivity(
            @Parameter(description = "ID of the trip activity", required = true)
            @PathVariable UUID tripActivityId) {
        return tripService.deleteTripActivityById(tripActivityId);
    }
}