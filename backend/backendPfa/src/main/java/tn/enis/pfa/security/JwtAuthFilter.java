package tn.enis.pfa.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tn.enis.pfa.entity.User;
import tn.enis.pfa.repository.UserRepository;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);
        if (!jwtService.isValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
            request.setAttribute("userId", user.getId());
        }
        filterChain.doFilter(request, response);
    }
}
