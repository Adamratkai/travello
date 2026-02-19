package com.travello.locationservice.dto;

import java.util.List;

public record DetailedPlaceDTO(String id,
                               String name,  // Resource name: "places/{placeId}"
                               DisplayName displayName,
                               Double rating,
                               String priceLevel,
                               List<PlacePhotoDTO> photos,
                               List<String> types,
                               CurrentOpeningHours currentOpeningHours,
                               Location location) {
    
    public record DisplayName(String text, String languageCode) {}
    
    public record CurrentOpeningHours(List<String> weekdayDescriptions) {}

}
