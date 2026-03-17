package com.travello.tripsservice.service;

import com.travello.tripsservice.client.LocationClient;
import com.travello.tripsservice.dto.*;
import com.travello.tripsservice.model.Trip;
import com.travello.tripsservice.model.TripActivity;
import com.travello.tripsservice.repository.TripActivityRepository;
import com.travello.tripsservice.repository.TripRepository;
import com.travello.tripsservice.security.JwtUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final TripActivityRepository tripActivityRepository;
    private final LocationClient locationClient;
    private final JwtUtils jwtUtils;

    public TripService(TripRepository tripRepository,
                       TripActivityRepository tripActivityRepository,
                       LocationClient locationClient,
                       JwtUtils jwtUtils) {
        this.tripRepository = tripRepository;
        this.tripActivityRepository = tripActivityRepository;
        this.locationClient = locationClient;
        this.jwtUtils = jwtUtils;
    }

    private UUID extractUserId(String authHeader) {
        String token = authHeader.substring(7);
        return jwtUtils.extractUserId(token);
    }

    public UUID createTrip(TripCreateDTO tripCreateDTO, String authHeader) {
        UUID userId = extractUserId(authHeader);
        Trip trip = new Trip();
        trip.setTripId(UUID.randomUUID());
        trip.setName(tripCreateDTO.name());
        trip.setStartDate(tripCreateDTO.startDate());
        trip.setEndDate(tripCreateDTO.endDate());
        trip.setTravellerId(userId);
        return tripRepository.save(trip).getTripId();
    }

    @Transactional
    public UUID addTripActivity(UUID tripId, TripActivityCreateDTO dto, String authHeader) {
        UUID userId = extractUserId(authHeader);
        Trip trip = tripRepository.findByTripId(tripId)
                .orElseThrow(() -> new NoSuchElementException("Trip not found"));

        if (!trip.getTravellerId().equals(userId)) {
            throw new NoSuchElementException("Trip not found");
        }

        TripActivity activity = new TripActivity();
        activity.setVisitTime(dto.visitTime());
        activity.setTrip(trip);
        activity.setPlaceId(dto.placeId());
        return tripActivityRepository.save(activity).getTripActivityId();
    }

    public int deleteTripActivityById(UUID tripActivityId) {
        return tripActivityRepository.deleteByTripActivityId(tripActivityId);
    }

    public List<TripDTO> getTrips() {
        return tripRepository.findAll().stream()
                .map(this::toTripDTO)
                .collect(Collectors.toList());
    }

    public List<TripDTO> getTripsByTraveller(String authHeader) {
        UUID userId = extractUserId(authHeader);
        return tripRepository.findByTravellerId(userId).stream()
                .map(this::toTripDTO)
                .collect(Collectors.toList());
    }

    public TripDetailsDTO getTripById(UUID tripId, String authHeader) {
        UUID userId = extractUserId(authHeader);
        Trip trip = tripRepository.findByTripId(tripId)
                .orElseThrow(NoSuchElementException::new);

        if (!trip.getTravellerId().equals(userId)) {
            throw new NoSuchElementException("Trip not found");
        }
        return toTripDetailsDTO(trip);
    }

    public int deleteTripById(UUID tripId, String authHeader) {
        UUID userId = extractUserId(authHeader);
        Trip trip = tripRepository.findByTripId(tripId)
                .orElseThrow(() -> new NoSuchElementException("Trip not found"));

        if (!trip.getTravellerId().equals(userId)) {
            return 0;
        }
        return tripRepository.deleteByTripId(tripId);
    }

    @Transactional
    public void updateTripById(UUID tripId, TripUpdateDTO dto, String authHeader) {
        UUID userId = extractUserId(authHeader);
        Trip trip = tripRepository.findByTripId(tripId)
                .orElseThrow(() -> new NoSuchElementException("Trip not found"));

        if (!trip.getTravellerId().equals(userId)) {
            throw new NoSuchElementException("Trip not found");
        }
        trip.setName(dto.name());
        trip.setStartDate(dto.startDate());
        trip.setEndDate(dto.endDate());
    }

    private TripDTO toTripDTO(Trip trip) {
        return new TripDTO(trip.getTripId(), trip.getName(), trip.getStartDate(), trip.getEndDate());
    }

    private TripDetailsDTO toTripDetailsDTO(Trip trip) {
        List<TripActivityDTO> activityDTOs = trip.getTripActivities().stream()
                .map(this::toTripActivityDTO)
                .collect(Collectors.toList());
        return new TripDetailsDTO(trip.getTripId(), trip.getName(), trip.getStartDate(), trip.getEndDate(), activityDTOs);
    }

    private TripActivityDTO toTripActivityDTO(TripActivity activity) {
        PlaceDTO placeDTO = locationClient.getPlaceById(activity.getPlaceId());
        return new TripActivityDTO(placeDTO, activity.getVisitTime());
    }
}