package com.travello.locationservice.service;

import com.travello.locationservice.dto.*;
import com.travello.locationservice.model.Photo;
import com.travello.locationservice.model.Place;
import com.travello.locationservice.model.PlaceType;
import com.travello.locationservice.repository.PhotoRepository;
import com.travello.locationservice.repository.PlaceRepository;
import com.travello.locationservice.repository.PlaceTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceTypeRepository placeTypeRepository;
    private final PhotoRepository photoRepository;
    private final RestTemplate restTemplate;

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    private static final Logger log = LoggerFactory.getLogger(PlaceService.class);

    public PlaceService(PlaceRepository placeRepository,
                        PlaceTypeRepository placeTypeRepository,
                        PhotoRepository photoRepository,
                        RestTemplate restTemplate) {
        this.placeRepository = placeRepository;
        this.placeTypeRepository = placeTypeRepository;
        this.photoRepository = photoRepository;
        this.restTemplate = restTemplate;
    }

    public List<PlaceDTO> getAllPlaces() {
        return placeRepository.findAll().stream()
                .map(this::toPlaceDTO)
                .collect(Collectors.toList());
    }

    public PlaceDTO getPlaceById(String placeId) {
        Place place = findOrCreatePlace(placeId);
        return toPlaceDTO(place);
    }

    public PlaceDTO createAndGetPlaceDTO(PlaceCreateDTO placeCreateDTO) {
        Place newPlace = createPlace(placeCreateDTO);
        return toPlaceDTO(newPlace);
    }

    private Place createPlace(PlaceCreateDTO dto) {
        Place place = new Place();
        Set<PlaceType> placeTypes = dto.placeTypes().stream()
                .map(this::findOrCreatePlaceType)
                .collect(Collectors.toSet());
        place.setPlaceId(dto.placeId());
        place.setName(dto.name());
        place.setPlaceTypes(placeTypes);
        place.setRating(dto.rating());
        place.setPriceLevel(dto.priceLevel());
        place.setOpeningHours(dto.openingHours());
        place.setLatitude(dto.latitude());
        place.setLongitude(dto.longitude());
        Place savedPlace = placeRepository.save(place);
        List<Photo> savedPhotos = createPhotos(dto.photoReferences(), savedPlace);
        savedPlace.setPhotos(savedPhotos);
        return savedPlace;
    }

    public PlaceType findOrCreatePlaceType(String placeType) {
        return placeTypeRepository.findByPlaceType(placeType).orElseGet(() -> {
            PlaceType newPlaceType = new PlaceType();
            newPlaceType.setPlaceType(placeType);
            return placeTypeRepository.save(newPlaceType);
        });
    }

    public Place findOrCreatePlace(String placeId) {
        return placeRepository.findByPlaceId(placeId).orElseGet(() -> {
            String url = String.format("https://places.googleapis.com/v1/places/%s", placeId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Goog-Api-Key", googleMapsApiKey);
            headers.set("X-Goog-FieldMask", "id,displayName,rating,priceLevel,currentOpeningHours.weekdayDescriptions,types,location,photos.name");
            headers.set("Content-Type", "application/json");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            try {
                ResponseEntity<DetailedPlaceDTO> response = restTemplate.exchange(
                        url, HttpMethod.GET, entity, DetailedPlaceDTO.class);
                
                DetailedPlaceDTO detailedPlace = response.getBody();
                if (detailedPlace == null) {
                    throw new NoSuchElementException("Place not found: " + placeId);
                }
                
                List<String> references = detailedPlace.photos() != null
                        ? detailedPlace.photos().stream()
                                .map(photo -> {
                                    String name = photo.name();
                                    if (name != null && name.contains("/photos/")) {
                                        return name.substring(name.lastIndexOf("/photos/") + 8);
                                    }
                                    return null;
                                })
                                .filter(ref -> ref != null)
                                .toList()
                        : List.of();
                
                PlaceCreateDTO placeCreateDTO = new PlaceCreateDTO(
                        detailedPlace.id(),
                        detailedPlace.displayName() != null ? detailedPlace.displayName().text() : "",
                        detailedPlace.types() != null ? detailedPlace.types() : List.of(),
                        detailedPlace.rating() != null ? detailedPlace.rating() : 0.0,
                        convertPriceLevel(detailedPlace.priceLevel()),
                        detailedPlace.currentOpeningHours() != null
                                ? detailedPlace.currentOpeningHours().weekdayDescriptions()  // ← átnevezve
                                : List.of(),
                        references,
                        detailedPlace.location() != null ? detailedPlace.location().latitude() : 0.0,
                        detailedPlace.location() != null ? detailedPlace.location().longitude() : 0.0
                );
                return createPlace(placeCreateDTO);
            } catch (Exception e) {
                log.error("Error fetching place from Google Places API: {}", e.getMessage());
                throw new NoSuchElementException("Place not found: " + placeId);
            }
        });
    }

    private List<UUID> getIdsFromPhotos(List<Photo> photos) {
        if (photos == null) return List.of();
        return photos.stream().map(Photo::getPhotoId).collect(Collectors.toList());
    }

    private PlaceDTO toPlaceDTO(Place place) {
        Location location = new Location(place.getLatitude(), place.getLongitude());
        return new PlaceDTO(
                place.getPlaceId(),
                place.getName(),
                place.getRating(),
                place.getPriceLevel(),
                place.getOpeningHours(),
                getIdsFromPhotos(place.getPhotos()),
                location
        );
    }

    public List<Photo> createPhotos(List<String> references, Place place) {
        List<Photo> photos = new ArrayList<>();
        int maxPhotos = 2;
        int counter = 0;
        for (String reference : references) {
            if (counter >= maxPhotos) break;
            Photo photo = new Photo();
            photo.setPhoto(getPhotoFromGoogle(place.getPlaceId(), reference));
            photo.setPlace(place);
            photoRepository.save(photo);
            photos.add(photo);
            counter++;
        }
        return photos;
    }

    private byte[] getPhotoFromGoogle(String placeId, String photoReference) {
        String url = String.format(
                "https://places.googleapis.com/v1/places/%s/photos/%s/media?maxWidthPx=400",
                placeId, photoReference);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Goog-Api-Key", googleMapsApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<byte[]> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, byte[].class);
        return response.getBody();
    }

    public byte[] getPhotoById(UUID photoId) {
        Photo photo = photoRepository.findByPhotoId(photoId)
                .orElseThrow(() -> new NoSuchElementException("No photo with id " + photoId));
        return photo.getPhoto();
    }

    public List<UUID> getPhotoIdsByPlaceId(String placeId) {
        Place place = placeRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new NoSuchElementException("No place with id " + placeId));
        return getIdsFromPhotos(place.getPhotos());
    }

    private int convertPriceLevel(String priceLevel) {
        if (priceLevel == null) return 0;
        return switch (priceLevel) {
            case "PRICE_LEVEL_FREE" -> 0;
            case "PRICE_LEVEL_INEXPENSIVE" -> 1;
            case "PRICE_LEVEL_MODERATE" -> 2;
            case "PRICE_LEVEL_EXPENSIVE" -> 3;
            case "PRICE_LEVEL_VERY_EXPENSIVE" -> 4;
            default -> 0;
        };
    }
}
