package com.travello.locationservice.controller;

import com.travello.locationservice.dto.PlaceCreateDTO;
import com.travello.locationservice.dto.PlaceDTO;
import com.travello.locationservice.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Location", description = "Place management endpoints")
@RestController
@RequestMapping("location")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @Operation(summary = "Get all places")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of all places",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = PlaceDTO.class))
                    )
            )
    })
    @GetMapping("/")
    public List<PlaceDTO> getPlaces() {
        return placeService.getAllPlaces();
    }

    @Operation(summary = "Get a place by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Place found",
                    content = @Content(schema = @Schema(implementation = PlaceDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Place not found", content = @Content)
    })
    @GetMapping("/{placeId}")
    public PlaceDTO getPlace(
            @Parameter(description = "ID of the place", required = true)
            @PathVariable String placeId) {
        return placeService.getPlaceById(placeId);
    }

    @Operation(summary = "Create a new place")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Place created successfully",
                    content = @Content(schema = @Schema(implementation = PlaceDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    @PostMapping("/")
    public PlaceDTO createPlace(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Place data to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PlaceCreateDTO.class))
            )
            @RequestBody PlaceCreateDTO placeCreateDTO) {
        return placeService.createAndGetPlaceDTO(placeCreateDTO);
    }
}