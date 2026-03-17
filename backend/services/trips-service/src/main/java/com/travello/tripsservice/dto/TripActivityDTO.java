package com.travello.tripsservice.dto;

import java.time.LocalDateTime;

public record TripActivityDTO(PlaceDTO placeDTO, LocalDateTime visitTime) {
}
