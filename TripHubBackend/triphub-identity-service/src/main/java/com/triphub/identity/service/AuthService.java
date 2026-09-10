package com.triphub.identity.service;

import com.triphub.identity.domain.User;
import com.triphub.identity.dto.AuthResponse;
import com.triphub.identity.dto.LoginRequest;
import com.triphub.identity.dto.RegisterRequest;
import com.triphub.identity.repository.UserRepository;
import com.triphub.shared.exception.AppException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthResponse register(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new AppException("User already exists", 409);
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());

        userRepository.save(user);

        String token = jwtTokenService.generateToken(user);
        return new AuthResponse(token, "refresh-token", user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("Invalid credentials", 401));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException("Invalid credentials", 401);
        }

        String token = jwtTokenService.generateToken(user);
        return new AuthResponse(token, "refresh-token", user.getEmail());
    }
}
