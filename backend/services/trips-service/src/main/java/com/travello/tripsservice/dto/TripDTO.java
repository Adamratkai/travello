package com.travello.tripsservice.dto;

import java.time.LocalDate;
import java.util.UUID;

public record TripDTO(UUID tripId, String name, LocalDate startDate, LocalDate endDate) {
}
