package com.narvee.usit.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenUtil {

	@Value("${jwt.secret}")
	private String jwtSecret;

	public void validateToken(final String token) {
		try {
			Jws<Claims> parseClaimsJws = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
			 Date expiration = parseClaimsJws.getBody().getExpiration();
		        if (expiration != null && expiration.before(new Date())) {
		            throw new ExpiredJwtException(null, null, "JWT token has expired");
		        }
		} catch (SignatureException ex) {
			throw new SignatureException("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			throw new MalformedJwtException("Invalid JWT token");
		} catch (UnsupportedJwtException ex) {
			throw new UnsupportedJwtException("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("JWT claims string is empty.");
		}
	}
	
	

}