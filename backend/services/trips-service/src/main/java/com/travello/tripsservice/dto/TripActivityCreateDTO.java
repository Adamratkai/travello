package com.travello.tripsservice.dto;

import java.time.LocalDateTime;

public record TripActivityCreateDTO(String placeId, LocalDateTime visitTime) {
}
