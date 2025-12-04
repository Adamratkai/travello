package com.travello.authservice.service;

import com.travello.authservice.dto.JwtDTO;
import com.travello.authservice.dto.LoginTravelerDTO;
import com.travello.authservice.dto.NewTravelerDTO;
import com.travello.authservice.exception.EmailAlreadyExistsException;
import com.travello.authservice.model.Traveler;
import com.travello.authservice.repository.TravelerRepository;
import com.travello.authservice.security.jwt.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TravelerService {


    private final AuthenticationManager authenticationManager;
    private final TravelerRepository travelerRepository;
    private final JwtUtil jwtUtil;
    private final static Logger logger = LoggerFactory.getLogger(TravelerService.class);
    private final PasswordEncoder passwordEncoder;

    public TravelerService(TravelerRepository travelerRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.travelerRepository = travelerRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(NewTravelerDTO traveler) {
        if (travelerRepository.existsByEmail(traveler.getEmail())) {
            throw new EmailAlreadyExistsException("The email already exists");
        }

        Traveler newTraveler = Traveler.builder()
                .username(traveler.getUsername())
                .password(passwordEncoder.encode(traveler.getPassword()))
                .email(traveler.getEmail())
                .build();

        travelerRepository.save(newTraveler);
    }
    public JwtDTO loginUser(LoginTravelerDTO traveler) {
        Authentication authentication = authenticationManager.authenticate(new
                UsernamePasswordAuthenticationToken(traveler.getEmail(),
                traveler.getPassword()));

        logger.info(Objects.requireNonNull(authentication.getPrincipal()).toString());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtUtil.generateJwtToken(authentication);

        Traveler userDetails = (Traveler) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return new JwtDTO(jwtToken, userDetails.getUsername(), roles);


    }
}
