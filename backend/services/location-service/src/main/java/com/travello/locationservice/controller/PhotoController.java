package com.travello.locationservice.controller;

import com.travello.locationservice.service.PlaceService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/photos")
public class PhotoController {

    private final PlaceService placeService;

    public PhotoController(PlaceService placeService) {
        this.placeService = placeService;
    }

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

    @GetMapping("/all/{placeId}")
    public List<UUID> getIdsFromPhotos(@PathVariable String placeId) {
        return placeService.getPhotoIdsByPlaceId(placeId);
    }
}

