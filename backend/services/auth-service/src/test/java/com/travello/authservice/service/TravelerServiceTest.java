package com.travello.authservice.service;

import com.travello.authservice.dto.JwtDTO;
import com.travello.authservice.dto.LoginTravelerDTO;
import com.travello.authservice.dto.NewTravelerDTO;
import com.travello.authservice.exception.EmailAlreadyExistsException;
import com.travello.authservice.model.Role;
import com.travello.authservice.model.Traveler;
import com.travello.authservice.repository.TravelerRepository;
import com.travello.authservice.security.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TravelerServiceTest {

    @Mock private TravelerRepository travelerRepository;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TravelerService travelerService;

    @BeforeEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("registerUser()")
    class RegisterUser {

        private NewTravelerDTO dto;

        @BeforeEach
        void setUp() {
            dto = new NewTravelerDTO();
            dto.setUsername("john_doe");
            dto.setEmail("john@example.com");
            dto.setPassword("secret123");
        }

        @Test
        @DisplayName("saves traveler with correct fields when email is not taken")
        void shouldSaveTraveler_whenEmailNotExists() {
            when(travelerRepository.existsByEmail("john@example.com")).thenReturn(false);
            when(passwordEncoder.encode("secret123")).thenReturn("encodedSecret");

            travelerService.registerUser(dto);

            ArgumentCaptor<Traveler> captor = ArgumentCaptor.forClass(Traveler.class);
            verify(travelerRepository).save(captor.capture());

            Traveler saved = captor.getValue();
            assertThat(saved.getUsername()).isEqualTo("john_doe");
            assertThat(saved.getEmail()).isEqualTo("john@example.com");
            assertThat(saved.getPassword()).isEqualTo("encodedSecret");
        }

        @Test
        @DisplayName("saves traveler with default ROLE_USER authority")
        void shouldSaveTraveler_withDefaultUserRole() {
            when(travelerRepository.existsByEmail(any())).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("encoded");

            travelerService.registerUser(dto);

            ArgumentCaptor<Traveler> captor = ArgumentCaptor.forClass(Traveler.class);
            verify(travelerRepository).save(captor.capture());

            assertThat(captor.getValue().getAuthorities())
                    .extracting("authority")
                    .containsExactly("ROLE_USER");
        }

        @Test
        @DisplayName("encodes the raw password before persisting")
        void shouldEncodePassword_beforeSave() {
            when(travelerRepository.existsByEmail(any())).thenReturn(false);
            when(passwordEncoder.encode("secret123")).thenReturn("$2a$10$hashed");

            travelerService.registerUser(dto);

            verify(passwordEncoder).encode("secret123");
            ArgumentCaptor<Traveler> captor = ArgumentCaptor.forClass(Traveler.class);
            verify(travelerRepository).save(captor.capture());
            assertThat(captor.getValue().getPassword()).isEqualTo("$2a$10$hashed");
            assertThat(captor.getValue().getPassword()).isNotEqualTo("secret123");
        }

        @Test
        @DisplayName("throws EmailAlreadyExistsException when email is already registered")
        void shouldThrow_whenEmailAlreadyExists() {
            when(travelerRepository.existsByEmail("john@example.com")).thenReturn(true);

            assertThatThrownBy(() -> travelerService.registerUser(dto))
                    .isInstanceOf(EmailAlreadyExistsException.class)
                    .hasMessageContaining("already exists");
        }

        @Test
        @DisplayName("never calls save or encode when email already exists")
        void shouldNotPersist_whenEmailAlreadyExists() {
            when(travelerRepository.existsByEmail(any())).thenReturn(true);

            assertThatThrownBy(() -> travelerService.registerUser(dto))
                    .isInstanceOf(EmailAlreadyExistsException.class);

            verify(passwordEncoder, never()).encode(any());
            verify(travelerRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("loginUser()")
    class LoginUser {

        private LoginTravelerDTO dto;
        private Traveler traveler;
        private Authentication authentication;

        @BeforeEach
        void setUp() {
            dto = new LoginTravelerDTO();
            dto.setEmail("john@example.com");
            dto.setPassword("secret123");

            traveler = Traveler.builder()
                    .username("john_doe")
                    .email("john@example.com")
                    .password("encodedSecret")
                    .build();

            authentication = mock(Authentication.class);
        }

        @Test
        @DisplayName("returns JwtDTO with token in the 'token' record field")
        void shouldReturnJwtDTO_withCorrectToken() {
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(traveler);
            when(jwtUtil.generateJwtToken(authentication)).thenReturn("mocked.jwt.token");

            JwtDTO result = travelerService.loginUser(dto);

            assertThat(result.token()).isEqualTo("mocked.jwt.token");
        }

        @Test
        @DisplayName("returns JwtDTO with username in the 'user' record field")
        void shouldReturnJwtDTO_withCorrectUser() {
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(traveler);
            when(jwtUtil.generateJwtToken(authentication)).thenReturn("token");

            JwtDTO result = travelerService.loginUser(dto);

            assertThat(result.user()).isEqualTo("john_doe");
        }

        @Test
        @DisplayName("returns ROLE_USER in roles list for a default traveler")
        void shouldReturnJwtDTO_withDefaultRole() {
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(traveler);
            when(jwtUtil.generateJwtToken(authentication)).thenReturn("token");

            JwtDTO result = travelerService.loginUser(dto);

            assertThat(result.roles()).containsExactly("ROLE_USER");
        }

        @Test
        @DisplayName("returns all roles when traveler has multiple roles")
        void shouldReturnAllRoles_whenTravelerHasMultipleRoles() {
            Traveler multiRoleTraveler = Traveler.builder()
                    .username("admin_user")
                    .email("admin@example.com")
                    .password("encoded")
                    .roles(Set.of(Role.USER, Role.ADMIN))
                    .build();

            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(multiRoleTraveler);
            when(jwtUtil.generateJwtToken(authentication)).thenReturn("token");

            JwtDTO result = travelerService.loginUser(dto);

            assertThat(result.roles()).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
        }

        @Test
        @DisplayName("authenticates using email and password from the DTO")
        void shouldAuthenticateWithEmailAndPassword() {
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(traveler);
            when(jwtUtil.generateJwtToken(authentication)).thenReturn("token");

            travelerService.loginUser(dto);

            ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                    ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
            verify(authenticationManager).authenticate(captor.capture());

            assertThat(captor.getValue().getPrincipal()).isEqualTo("john@example.com");
            assertThat(captor.getValue().getCredentials()).isEqualTo("secret123");
        }

        @Test
        @DisplayName("sets the returned Authentication into SecurityContextHolder")
        void shouldSetSecurityContext() {
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(traveler);
            when(jwtUtil.generateJwtToken(authentication)).thenReturn("token");

            travelerService.loginUser(dto);

            assertThat(SecurityContextHolder.getContext().getAuthentication())
                    .isSameAs(authentication);
        }

        @Test
        @DisplayName("calls generateJwtToken exactly once with the Authentication object")
        void shouldCallGenerateJwtToken_exactlyOnce() {
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(traveler);
            when(jwtUtil.generateJwtToken(authentication)).thenReturn("token");

            travelerService.loginUser(dto);

            verify(jwtUtil, times(1)).generateJwtToken(authentication);
        }

        @Test
        @DisplayName("propagates BadCredentialsException and never generates a token")
        void shouldPropagateException_whenAuthenticationFails() {
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> travelerService.loginUser(dto))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessageContaining("Bad credentials");

            verify(jwtUtil, never()).generateJwtToken(any());
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }
    }
}