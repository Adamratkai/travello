package com.travello.locationservice.dto;

public record RecommendationDTO(String id, DisplayName displayName, Double rating, String priceLevel) {
    
    public record DisplayName(String text, String languageCode) {}
}

