package com.pfa.elearning.controller;

import com.pfa.elearning.dto.request.LoginRequest;
import com.pfa.elearning.dto.request.RegisterRequest;
import com.pfa.elearning.dto.response.AuthResponse;
import com.pfa.elearning.model.User;
import com.pfa.elearning.security.JwtTokenProvider;
import com.pfa.elearning.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        User user = userService.getUserByEmail(request.getEmail());

        AuthResponse response = new AuthResponse(
                token, user.getId(), user.getEmail(),
                user.getFullName(), user.getRole()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(request);
        String token = tokenProvider.generateTokenFromEmail(user.getEmail());

        AuthResponse response = new AuthResponse(
                token, user.getId(), user.getEmail(),
                user.getFullName(), user.getRole()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(auth.getName());

        return ResponseEntity.ok(java.util.Map.of(
                "id", user.getId(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "fullName", user.getFullName(),
                "createdAt", user.getCreatedAt().toString()
        ));
    }
}
