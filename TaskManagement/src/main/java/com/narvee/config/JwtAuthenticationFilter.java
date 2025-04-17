package com.narvee.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

@Service
public class JwtAuthenticationFilter extends OncePerRequestFilter{
		
		
		
		@Autowired
	    private JwtUtil jwtService;

	    @Autowired
	    private TmsUserDetailsService userDetailsService;

	    @Override
	    protected void doFilterInternal(HttpServletRequest request,
	                                    HttpServletResponse response,
	                                    FilterChain filterChain)
	                                    throws ServletException, IOException {
	        final String authHeader = request.getHeader("Authorization");
	        String email = null;
	        String jwtToken = null;

	        if (authHeader != null && authHeader.startsWith("Bearer ")) {
	            jwtToken = authHeader.substring(7);
	            email = jwtService.extractUsername(jwtToken);
	        }

	        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

	            if (jwtService.validateToken(jwtToken)) {
	                UsernamePasswordAuthenticationToken authToken =
	                        new UsernamePasswordAuthenticationToken(
	                                userDetails, null, userDetails.getAuthorities());

	                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                SecurityContextHolder.getContext().setAuthentication(authToken);
	            }
	        }

	        filterChain.doFilter(request, response);
	    }
		
	}


