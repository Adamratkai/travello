package com.travello.tripsservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trip_activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private UUID tripActivityId;

    @Column(nullable = false)
    private String placeId;

    private LocalDateTime visitTime;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @PrePersist
    private void onCreate() {
        if (this.tripActivityId == null) {
            this.tripActivityId = UUID.randomUUID();
        }
    }
}
