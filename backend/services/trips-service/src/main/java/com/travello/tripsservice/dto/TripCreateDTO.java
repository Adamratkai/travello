package com.travello.tripsservice.dto;

import java.time.LocalDate;

public record TripCreateDTO(String name, LocalDate startDate, LocalDate endDate) {
}
