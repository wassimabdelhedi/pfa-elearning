package tn.enis.pfa.security;

import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;

@Component
public class JwtUtils {

	// Use a long secret key directly (at least 32 bytes for HS256)
	private final String jwtSecret = "MySuperSecretKeyForJwtSigningMustBeVeryLongToWorkWithHS256";
	private final long jwtExpirationMs = 86400000; // 1 day

	private Key getSignInKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes());
	}

	public String generateToken(String username) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public String getUsernameFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignInKey()).build()
				.parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}
}
