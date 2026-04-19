package com.pfa.elearning.controller;

import com.pfa.elearning.dto.request.LoginRequest;
import com.pfa.elearning.dto.request.RegisterRequest;
import com.pfa.elearning.dto.response.AuthResponse;
import com.pfa.elearning.model.User;
import com.pfa.elearning.model.PasswordResetToken;
import com.pfa.elearning.repository.PasswordResetTokenRepository;
import com.pfa.elearning.security.JwtTokenProvider;
import com.pfa.elearning.service.EmailService;
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
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        userService.updateLastLoginDate(request.getEmail());
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

    @PostMapping("/forgot-password")
    public ResponseEntity<java.util.Map<String, String>> forgotPassword(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", "Email is required"));
        }

        try {
            User user = userService.getUserByEmail(email);
            
            // Generate token
            String token = java.util.UUID.randomUUID().toString();
            
            // Clean up old tokens for this user
            tokenRepository.deleteByUser(user);
            
            // Save new token
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expiryDate(java.time.LocalDateTime.now().plusHours(2))
                    .build();
            tokenRepository.save(resetToken);
            
            // Send email
            String resetUrl = "http://localhost:5173/reset-password?token=" + token;
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), resetUrl);
            
            return ResponseEntity.ok(java.util.Map.of("message", "A password reset link has been sent to your email."));
        } catch (com.pfa.elearning.exception.ResourceNotFoundException e) {
            // Return OK even if user not found to prevent email enumeration
            return ResponseEntity.ok(java.util.Map.of("message", "If an account exists with that email, a password reset link has been sent."));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<java.util.Map<String, String>> resetPassword(@RequestBody java.util.Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        
        if (token == null || newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", "Valid token and a new password (min 6 chars) are required"));
        }
        
        java.util.Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Map.of("message", "Invalid or expired password reset token"));
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Map.of("message", "Password reset token has expired"));
        }
        
        // Reset password
        userService.resetPassword(resetToken.getUser().getEmail(), newPassword);
        
        // Delete token
        tokenRepository.delete(resetToken);
        
        return ResponseEntity.ok(java.util.Map.of("message", "Password resetting successful"));
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
