package com.narvee.usit.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.narvee.usit.util.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;

@Component

public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

	@Autowired
	private RouteValidator validator;

	@Autowired
	private JwtTokenUtil jwtUtil;

	public AuthenticationFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {
			if (validator.isSecured.test(exchange.getRequest())) {
				if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing authorization header");
				}
				String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
				if (authHeader != null && authHeader.startsWith("Bearer ")) {
					authHeader = authHeader.substring(7);
				}
				try {
					jwtUtil.validateToken(authHeader);
				} catch (ExpiredJwtException ex) {
					//throw new ExpiredJwtException(null, null, "JWT token has expired");
	                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT token has expired", ex); // Updated to return UNAUTHORIZED status
				}

				catch (ResponseStatusException e) {
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Incorrect Username or Password");
				}
			}
			return chain.filter(exchange);
		});
	}

	public static class Config {

	}
}