package com.travello.locationservice.controller;

import com.travello.locationservice.dto.PlaceCreateDTO;
import com.travello.locationservice.dto.PlaceDTO;
import com.travello.locationservice.service.PlaceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/places")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping("/")
    public List<PlaceDTO> getPlaces() {
        return placeService.getAllPlaces();
    }

    @GetMapping("/{placeId}")
    public PlaceDTO getPlace(@PathVariable String placeId) {
        return placeService.getPlaceById(placeId);
    }

    @PostMapping("/")
    public PlaceDTO createPlace(@RequestBody PlaceCreateDTO placeCreateDTO) {
        return placeService.createAndGetPlaceDTO(placeCreateDTO);
    }
}
