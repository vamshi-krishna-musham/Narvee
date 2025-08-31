package com.narvee.ats.auth.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.narvee.ats.auth.exception.JwtTokenMalformedException;
import com.narvee.ats.auth.exception.JwtTokenMissingException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String jwtSecret;
	
	@Value("${jwt.token.validity}")
	private long tokenValidity;

	public String getUserName(final String token) {
		try {
			Claims body = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
			return body.getSubject();
		} catch (Exception e) {
		}
		return null;
	}
	
	

	public String generateToken(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		Claims claims = Jwts.claims().setSubject(user.getUsername());
		final long nowMillis = System.currentTimeMillis();
		final long expMillis = nowMillis + tokenValidity;
		Date exp = new Date(expMillis);
		return Jwts.builder().setClaims(claims).setIssuedAt(new Date(nowMillis)).setExpiration(exp)
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}

	public String generateTokenTms(String email) {
		//User user = (User) authentication.getPrincipal();
		Claims claims = Jwts.claims().setSubject(email);
		final long nowMillis = System.currentTimeMillis();
		final long expMillis = nowMillis + tokenValidity;
		Date exp = new Date(expMillis);
		return Jwts.builder().setClaims(claims).setIssuedAt(new Date(nowMillis)).setExpiration(exp)
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}

	
//	public String generateTokenWithCompanies(Authentication authentication, List<Long> associatedCids) {
//	    if (authentication == null || authentication.getPrincipal() == null) {
//	        throw new IllegalArgumentException("Authentication object or principal is null");
//	    }
//
//	    User user = (User) authentication.getPrincipal();
//
//	    // Create JWT claims
//	    Claims claims = Jwts.claims().setSubject(user.getUsername());
//	    claims.put("aCid", associatedCids); // Add company IDs
//
//	    long nowMillis = System.currentTimeMillis();
//	    Date now = new Date(nowMillis);
//	    Date expiry = new Date(nowMillis + tokenValidity);
//
//	    return Jwts.builder()
//	            .setClaims(claims)
//	            .setIssuedAt(now)
//	            .setExpiration(expiry)
//	            .signWith(SignatureAlgorithm.HS512, jwtSecret)
//	            .compact();
//	}
	
	
	public String generateTokenWithCompanies(String email, List<Long> associatedCids) {
		if (email == null || email.isEmpty()) {
			throw new IllegalArgumentException("Email cannot be null or empty");
		}

		// Create JWT claims
		Claims claims = Jwts.claims().setSubject(email);
		claims.put("aCid", associatedCids); // Add company IDs

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		Date expiry = new Date(nowMillis + tokenValidity);

		return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(expiry)
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}

	
	public void validateToken(final String token) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
		} catch (SignatureException ex) {
			throw new JwtTokenMalformedException("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			throw new JwtTokenMalformedException("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			throw new JwtTokenMalformedException("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			throw new JwtTokenMalformedException("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			throw new JwtTokenMissingException("JWT claims string is empty.");
		}
	}
	
	
	public static List<Long> getAssociatedCompanyIds(String token) {
	    if (token == null || token.isEmpty()) {
	        return Collections.emptyList();
	    }

	    try {
	        Claims claims = Jwts.parser()
	            .setSigningKey("secretkey")
	            .parseClaimsJws(token.replace("Bearer ", "").trim())
	            .getBody();

	        Object aCidObject = claims.get("aCid");

	        if (aCidObject instanceof List<?>) {
	            List<?> rawList = (List<?>) aCidObject;
	            return rawList.stream()
	                .map(item -> Long.valueOf(item.toString()))
	                .collect(Collectors.toList());
	        }
	    } catch (Exception e) {
	        // log error or rethrow as needed
	        e.printStackTrace();
	    }

	    return Collections.emptyList();
	}

	
	
	
	

}
