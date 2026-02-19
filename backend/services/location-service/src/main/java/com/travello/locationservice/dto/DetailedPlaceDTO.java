package com.travello.locationservice.dto;

import java.util.List;

public record DetailedPlaceDTO(String id,
                               String name,  // Resource name: "places/{placeId}"
                               DisplayName displayName,
                               Double rating,
                               Integer priceLevel,
                               List<PlacePhotoDTO> photos,
                               List<String> types,
                               RegularOpeningHours regularOpeningHours,
                               Location location) {
    
    public record DisplayName(String text, String languageCode) {}
    
    public record RegularOpeningHours(List<String> weekdayText) {}

}
