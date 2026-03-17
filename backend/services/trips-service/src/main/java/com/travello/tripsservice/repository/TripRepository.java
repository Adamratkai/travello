package com.travello.tripsservice.repository;

import com.travello.tripsservice.model.Trip;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {
    @EntityGraph(attributePaths = {"tripActivities"})
    Optional<Trip> findByTripId(UUID tripId);

    int deleteByTripId(UUID tripId);

    List<Trip> findByTravellerId(UUID travellerId);
}
