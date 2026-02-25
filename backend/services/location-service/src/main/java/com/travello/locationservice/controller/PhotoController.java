package com.travello.locationservice.controller;

import com.travello.locationservice.dto.PlacePhotoDTO;
import com.travello.locationservice.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name="Photos", description = "Location photos endpoints")
@RestController
@RequestMapping("location/photos")
public class PhotoController {

    private final PlaceService placeService;

    public PhotoController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @Operation(summary = "Get photo by place ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo received succesfully",
            content = @Content(array = @ArraySchema(schema = @Schema(type = "string", format = "byte")))),
            @ApiResponse(responseCode = "404", description = "Photo not found",content = @Content)
    })
    @GetMapping("/{photoId}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable UUID photoId) {
        byte[] imageBytes = placeService.getPhotoById(photoId);
        if (imageBytes == null || imageBytes.length == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/png"))
                .body(imageBytes);
    }

    @Operation(summary = "Get all the photo ID by place ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Place photo ids received sucessfully",
            content = @Content(array = @ArraySchema(schema = @Schema(type = "string", format = "uuid")))),
            @ApiResponse(responseCode = "404", description = "Photo ids not found", content = @Content)
    })
    @GetMapping("/all/{placeId}")
    public List<UUID> getIdsFromPhotos(@PathVariable String placeId) {
        return placeService.getPhotoIdsByPlaceId(placeId);
    }
}

