package com.travello.locationservice.dto;

public record RecommendationDTO(String id, DisplayName displayName, Double rating, Integer priceLevel) {
    
    public record DisplayName(String text, String languageCode) {}
}

