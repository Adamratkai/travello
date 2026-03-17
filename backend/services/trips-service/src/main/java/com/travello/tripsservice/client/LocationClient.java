package com.travello.tripsservice.client;

import com.travello.tripsservice.dto.PlaceDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LocationClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public LocationClient(RestTemplate restTemplate,
                          @Value("${places.service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }

    public PlaceDTO getPlaceById(String placeId) {
        String url = baseUrl + "location/" + placeId;
        return restTemplate.getForObject(url, PlaceDTO.class);
    }
}
