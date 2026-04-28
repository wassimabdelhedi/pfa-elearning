package com.pfa.elearning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.pfa.elearning.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource())).csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						// Public endpoints
						.requestMatchers("/api/auth/**").permitAll().requestMatchers(HttpMethod.GET, "/api/courses/**")
						.permitAll().requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/exercises/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/quizzes/**").permitAll().requestMatchers("/uploads/**")
						.permitAll().requestMatchers(HttpMethod.GET, "/api/messages/debug/users").permitAll()

						// Teacher endpoints - courses
						.requestMatchers(HttpMethod.POST, "/api/courses/**").hasRole("TEACHER")
						.requestMatchers(HttpMethod.PUT, "/api/courses/**").hasRole("TEACHER")
						.requestMatchers(HttpMethod.DELETE, "/api/courses/**").hasRole("TEACHER")

						// Student exercise completions
						.requestMatchers(HttpMethod.POST, "/api/exercises/*/complete").hasRole("STUDENT")

						// Teacher endpoints - exercises
						.requestMatchers(HttpMethod.POST, "/api/exercises/**").hasRole("TEACHER")
						.requestMatchers(HttpMethod.PUT, "/api/exercises/**").hasRole("TEACHER")
						.requestMatchers(HttpMethod.DELETE, "/api/exercises/**").hasRole("TEACHER")

						// Student quiz submissions
						.requestMatchers(HttpMethod.POST, "/api/quizzes/*/submit").hasRole("STUDENT")

						// Teacher endpoints - quizzes
						.requestMatchers(HttpMethod.POST, "/api/quizzes/**").hasRole("TEACHER")
						.requestMatchers(HttpMethod.PUT, "/api/quizzes/**").hasRole("TEACHER")
						.requestMatchers(HttpMethod.DELETE, "/api/quizzes/**").hasRole("TEACHER")

						// Student endpoints
						.requestMatchers("/api/search/**").hasRole("STUDENT").requestMatchers("/api/recommendations/**")
						.hasRole("STUDENT").requestMatchers("/api/enrollments/**").hasRole("STUDENT")

						// Chapter endpoints
						.requestMatchers(HttpMethod.GET, "/api/chapters/**").permitAll()
						.requestMatchers(HttpMethod.PUT, "/api/chapters/*/complete").authenticated()
						.requestMatchers(HttpMethod.GET, "/api/chapters/course/*/progress").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/chapters/**").hasRole("TEACHER")
						.requestMatchers(HttpMethod.PUT, "/api/chapters/**").hasRole("TEACHER")
						.requestMatchers(HttpMethod.DELETE, "/api/chapters/**").hasRole("TEACHER")

						// Admin endpoints - shared with teachers for student profiling
						.requestMatchers(HttpMethod.GET, "/api/admin/users/*").hasAnyRole("ADMIN", "TEACHER")
						.requestMatchers(HttpMethod.GET, "/api/admin/categories").hasAnyRole("ADMIN", "TEACHER")
						.requestMatchers("/api/admin/**").hasRole("ADMIN")

						// All other requests require authentication
						.anyRequest().authenticated())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(true);
		configuration.setAllowedOrigins(java.util.List.of("http://localhost:5173", "http://localhost:3000"));
		configuration.setAllowedHeaders(java.util.List.of("*"));
		configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setExposedHeaders(java.util.List.of("Authorization"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
