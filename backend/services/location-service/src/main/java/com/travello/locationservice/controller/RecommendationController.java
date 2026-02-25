package com.travello.locationservice.controller;

import com.travello.locationservice.dto.Location;
import com.travello.locationservice.dto.LocationDTO;
import com.travello.locationservice.dto.RecommendationDTO;
import com.travello.locationservice.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Recommendations", description = "Place recommendation endpoints")
@RestController
@RequestMapping("location/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Operation(summary = "Get place recommendations by location coordinates and type")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of recommendations",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = RecommendationDTO.class))
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Missing or invalid parameters", content = @Content),
            @ApiResponse(responseCode = "404", description = "No recommendations found", content = @Content)
    })
    @GetMapping("/")
    public List<RecommendationDTO> getRecommendations(
            @Parameter(description = "Location coordinates", required = true)
            @ModelAttribute LocationDTO location,
            @Parameter(description = "Type of place (e.g. 'restaurant', 'museum')", required = true, example = "restaurant")
            @RequestParam String type) {
        return recommendationService.getRecommendations(location, type);
    }
}