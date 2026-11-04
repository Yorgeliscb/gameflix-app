package com.example.gameflixproject.service;

import com.example.gameflixproject.dto.LoginRequest;
import com.example.gameflixproject.dto.LoginResponse;
import com.example.gameflixproject.dto.RegisterRequest;
import com.example.gameflixproject.dto.UserResponse;
import com.example.gameflixproject.entity.User;
import com.example.gameflixproject.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password exceeds bcrypt input limit");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        try {
            return UserResponse.from(users.saveAndFlush(user));
        } catch (DataIntegrityViolationException error) {
            // Unique email is ultimately enforced by the MySQL database.
            if (users.existsByEmail(email)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
            }
            throw error;
        }
    }

    public LoginResponse login(LoginRequest request) {
        // Use the same generic error for missing email, unknown account, or wrong password.
        if (request == null || request.email() == null || request.password() == null
                || request.email().isBlank() || request.password().isBlank()) {
            throw new BadCredentialsException("Invalid email or password");
        }
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        User user = users.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        return new LoginResponse(jwtService.createToken(user), "Bearer", jwtService.expiresInSeconds());
    }
}
