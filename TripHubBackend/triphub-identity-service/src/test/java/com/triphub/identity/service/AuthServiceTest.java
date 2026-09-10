package com.triphub.identity.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.triphub.identity.domain.User;
import com.triphub.identity.dto.AuthResponse;
import com.triphub.identity.dto.LoginRequest;
import com.triphub.identity.dto.RegisterRequest;
import com.triphub.identity.repository.UserRepository;
import com.triphub.shared.exception.AppException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtTokenService);
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void testRegisterNewUser() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPassword("password123");
        request.setPhoneNumber("9876543210");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(jwtTokenService.generateToken(any(User.class))).thenReturn("jwt_token_123");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt_token_123", response.getAccessToken());
        assertEquals("john@example.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password123");
        verify(jwtTokenService, times(1)).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when registering existing user")
    void testRegisterExistingUser() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password123");

        User existingUser = new User();
        existingUser.setEmail("existing@example.com");

        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> authService.register(request));
        assertEquals("User already exists", exception.getMessage());
        assertEquals(409, exception.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully login with valid credentials")
    void testLoginWithValidCredentials() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("encoded_password");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
        when(jwtTokenService.generateToken(user)).thenReturn("jwt_token_456");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt_token_456", response.getAccessToken());
        assertEquals("john@example.com", response.getEmail());
        verify(userRepository, times(1)).findByEmail("john@example.com");
        verify(passwordEncoder, times(1)).matches("password123", "encoded_password");
    }

    @Test
    @DisplayName("Should throw exception when login user not found")
    void testLoginUserNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> authService.login(request));
        assertEquals("Invalid credentials", exception.getMessage());
        assertEquals(401, exception.getStatusCode());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when password is incorrect")
    void testLoginWithIncorrectPassword() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("wrongpassword");

        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("encoded_password");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encoded_password")).thenReturn(false);

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> authService.login(request));
        assertEquals("Invalid credentials", exception.getMessage());
        assertEquals(401, exception.getStatusCode());
        verify(jwtTokenService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should handle special characters in registration")
    void testRegisterWithSpecialCharacters() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Jean-Pierre");
        request.setLastName("O'Brien");
        request.setEmail("jean+test@example.com");
        request.setPassword("P@ss#word!123");
        request.setPhoneNumber("+91-9876543210");

        when(userRepository.findByEmail("jean+test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("P@ss#word!123")).thenReturn("encoded_complex_password");
        when(jwtTokenService.generateToken(any(User.class))).thenReturn("jwt_token");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("jean+test@example.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should preserve user data during registration")
    void testRegisterPreservesUserData() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Alice");
        request.setLastName("Smith");
        request.setEmail("alice@example.com");
        request.setPassword("secure_pass");
        request.setPhoneNumber("1234567890");

        User capturedUser = new User();
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secure_pass")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            capturedUser.setFirstName(user.getFirstName());
            capturedUser.setLastName(user.getLastName());
            capturedUser.setEmail(user.getEmail());
            capturedUser.setPhoneNumber(user.getPhoneNumber());
            return user;
        });
        when(jwtTokenService.generateToken(any(User.class))).thenReturn("token");

        // Act
        authService.register(request);

        // Assert
        assertEquals("Alice", capturedUser.getFirstName());
        assertEquals("Smith", capturedUser.getLastName());
        assertEquals("alice@example.com", capturedUser.getEmail());
        assertEquals("1234567890", capturedUser.getPhoneNumber());
    }
}
