package tn.enis.pfa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tn.enis.pfa.dto.AuthResponse;
import tn.enis.pfa.dto.LoginDTO;
import tn.enis.pfa.dto.UserDTO;
import tn.enis.pfa.entity.Role;
import tn.enis.pfa.entity.User;
import tn.enis.pfa.security.JwtUtils;
import tn.enis.pfa.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private AuthenticationManager authenticationManager;
	private UserService userService;
	private JwtUtils jwtUtils;

	public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtils jwtUtils) {
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		this.jwtUtils = jwtUtils;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody UserDTO dto) {
		try {
			User user = new User();
			user.setUsername(dto.getUsername());
			user.setPassword(dto.getPassword());
			user.setRole(Role.valueOf(dto.getRole().toUpperCase()));

			userService.save(user);
			return ResponseEntity.ok(java.util.Collections.singletonMap("message", "User registered successfully"));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", "Invalid role"));
		}
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginDTO dto) {

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String token = jwtUtils.generateToken(userDetails.getUsername());

		AuthResponse response = new AuthResponse();
		response.setToken(token);

		return ResponseEntity.ok(response);
	}
}
