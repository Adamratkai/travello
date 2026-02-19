package com.travello.authservice.controller;

import com.travello.authservice.dto.JwtDTO;
import com.travello.authservice.dto.LoginTravelerDTO;
import com.travello.authservice.dto.NewTravelerDTO;
import com.travello.authservice.service.TravelerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final TravelerService travelerService;
    public AuthController(TravelerService travelerService) {
        this.travelerService = travelerService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> createUser(@Valid @RequestBody NewTravelerDTO signUpRequest){
        travelerService.registerUser(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping("/login")
    public JwtDTO login(@Valid @RequestBody LoginTravelerDTO loginRequest){
       return travelerService.loginUser(loginRequest);
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader){

        if(!authHeader.startsWith("Bearer ")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return travelerService.validateToken(authHeader.substring(7))
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
