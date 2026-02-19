package com.travello.locationservice.dto;

import java.util.List;
import java.util.UUID;

public record PlaceDTO(String placeId,
                       String name,
                       double rating,
                       int priceLevel,
                       List<String> openingHours,
                       List<UUID> photos,
                       Location location) {
}
