package com.travello.authservice.controller;

import com.travello.authservice.dto.JwtDTO;
import com.travello.authservice.dto.LoginTravelerDTO;
import com.travello.authservice.dto.NewTravelerDTO;
import com.travello.authservice.service.TravelerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="Auth", description = "Authentication endpoints")
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final TravelerService travelerService;
    public AuthController(TravelerService travelerService) {
        this.travelerService = travelerService;
    }

    @Operation(summary = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered succefully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),

    })
    @PostMapping("/register")
    public ResponseEntity<Void> createUser(@Valid @RequestBody NewTravelerDTO signUpRequest){
        travelerService.registerUser(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary ="Login and receive JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login succesful",
                content = @Content(schema = @Schema(implementation = JwtDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Bad credentials", content = @Content)
    })
    @PostMapping("/login")
    public JwtDTO login(@Valid @RequestBody LoginTravelerDTO loginRequest){
       return travelerService.loginUser(loginRequest);
    }

    @Operation(summary = "Validate a JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Valid token"),
            @ApiResponse(responseCode = "401", description = "Invalid token, or missing Bearer prefix")
    })
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@Valid @RequestHeader("Authorization") String authHeader){

        if(!authHeader.startsWith("Bearer ")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return travelerService.validateToken(authHeader.substring(7))
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
