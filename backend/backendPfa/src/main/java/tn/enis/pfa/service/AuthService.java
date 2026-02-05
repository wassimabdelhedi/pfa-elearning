package tn.enis.pfa.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.enis.pfa.dto.AuthResponse;
import tn.enis.pfa.dto.LoginRequest;
import tn.enis.pfa.dto.RegisterRequest;
import tn.enis.pfa.entity.User;
import tn.enis.pfa.repository.UserRepository;
import tn.enis.pfa.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("Email déjà utilisé");
		}
		User.Role role = "TEACHER".equalsIgnoreCase(request.getRole()) ? User.Role.TEACHER : User.Role.LEARNER;
		User user = User.builder().email(request.getEmail()).passwordHash(passwordEncoder.encode(request.getPassword()))
				.fullName(request.getFullName()).role(role).build();
		user = userRepository.save(user);
		String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
		return AuthResponse.builder().token(token).userId(user.getId()).email(user.getEmail())
				.fullName(user.getFullName()).role(user.getRole().name()).build();
	}

	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect"));
		if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
			throw new IllegalArgumentException("Email ou mot de passe incorrect");
		}
		String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
		return AuthResponse.builder().token(token).userId(user.getId()).email(user.getEmail())
				.fullName(user.getFullName()).role(user.getRole().name()).build();
	}
}
