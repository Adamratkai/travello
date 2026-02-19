package com.travello.locationservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID photoId;

    @ManyToOne
    @JoinColumn(nullable = false, name = "place_id")
    private Place place;

    @Column(nullable = false)
    private byte[] photo;

    @PrePersist
    public void generateUUID() {
        if (this.photoId == null) {
            this.photoId = UUID.randomUUID();
        }
    }
}

