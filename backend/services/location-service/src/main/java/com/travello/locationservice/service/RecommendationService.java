package com.travello.locationservice.service;

import com.travello.locationservice.dto.LocationDTO;
import tools.jackson.databind.ObjectMapper;
import com.travello.locationservice.dto.RecommendationDTO;
import com.travello.locationservice.dto.RecommendationResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationService {

    private final RestTemplate restTemplate;
    private final tools.jackson.databind.ObjectMapper objectMapper;

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    public RecommendationService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<RecommendationDTO> getRecommendations(LocationDTO location, String type) {

        String url = "https://places.googleapis.com/v1/places:searchNearby";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Goog-Api-Key", googleMapsApiKey);
        headers.set("X-Goog-FieldMask", "places.id,places.displayName,places.rating,places.priceLevel");
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("includedTypes", List.of(type));
        requestBody.put("maxResultCount", 10);

        Map<String, Object> locationRestriction = new HashMap<>();
        Map<String, Object> circle = new HashMap<>();
        Map<String, Double> center = new HashMap<>();
        center.put("latitude", location.latitude());
        center.put("longitude", location.longitude());
        circle.put("center", center);
        circle.put("radius", 500.0);
        locationRestriction.put("circle", circle);
        requestBody.put("locationRestriction", locationRestriction);
        
        try {
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            
            ResponseEntity<RecommendationResultDTO> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, RecommendationResultDTO.class);
            
            RecommendationResultDTO result = response.getBody();
            return result != null && result.places() != null 
                    ? new ArrayList<>(result.places()) 
                    : List.of();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching recommendations from Google Places API", e);
        }
    }
}

