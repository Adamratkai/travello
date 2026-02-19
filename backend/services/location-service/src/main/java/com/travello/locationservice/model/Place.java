package com.travello.locationservice.model;


import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "places")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String placeId;

    @Column(nullable = false)
    private String name;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "place_place_type",
            joinColumns = @JoinColumn(name = "place_id"),
            inverseJoinColumns = @JoinColumn(name = "place_type_id")
    )
    private Set<PlaceType> placeTypes;

    private double rating;
    private int priceLevel;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<String> openingHours;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    private List<Photo> photos;

    private double latitude;
    private double longitude;
}
