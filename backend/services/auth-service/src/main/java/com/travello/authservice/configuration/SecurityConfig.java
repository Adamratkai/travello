package com.travello.authservice.configuration;


import com.travello.authservice.security.jwt.AuthEntryPointJwt;
import com.travello.authservice.security.jwt.AuthTokenFilter;
import com.travello.authservice.security.jwt.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final JwtUtil jwtUtil;
    public SecurityConfig(UserDetailsService userDetailsService, AuthEntryPointJwt unauthorizedHandler, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtUtil = jwtUtil;
    }

    public AuthTokenFilter authenticationJwtTokenFilter() {return new AuthTokenFilter(userDetailsService, jwtUtil);}
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider =new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf->csrf.disable()).cors(cors->cors.disable())
                .exceptionHandling(exception-> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/api/traveler/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().permitAll());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);



        return http.build();



    }

}
