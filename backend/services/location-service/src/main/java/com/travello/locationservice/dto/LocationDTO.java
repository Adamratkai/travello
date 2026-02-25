package com.travello.locationservice.dto;

public record LocationDTO(
        Double latitude,
        Double longitude
) {
    public LocationDTO {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude and longitude are required");
        }
    }
}