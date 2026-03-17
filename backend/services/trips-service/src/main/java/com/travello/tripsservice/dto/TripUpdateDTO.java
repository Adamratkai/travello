package com.travello.tripsservice.dto;

import java.time.LocalDate;

public record TripUpdateDTO(String name, LocalDate startDate, LocalDate endDate) {
}
