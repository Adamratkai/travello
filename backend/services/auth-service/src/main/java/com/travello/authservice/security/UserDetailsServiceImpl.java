package com.travello.authservice.security;


import com.travello.authservice.repository.TravelerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    TravelerRepository travelerRepository;

    UserDetailsServiceImpl(TravelerRepository travelerRepository) {
        this.travelerRepository = travelerRepository;
    }


    @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return travelerRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));



    }
}
