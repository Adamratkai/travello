package com.travello.authservice.dto;

import java.util.List;

public record JwtDTO(String token, String user, List<String> roles){  }
