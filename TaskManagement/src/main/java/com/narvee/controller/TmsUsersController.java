package com.narvee.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.narvee.commons.RestAPIResponse;
import com.narvee.config.JwtUtil;
import com.narvee.dto.AuthRequest;
import com.narvee.dto.AuthResponse;
import com.narvee.dto.TmsUsersInfo;
import com.narvee.entity.TmsUsers;

import com.narvee.service.service.TmsUsersService;


@RestController
@RequestMapping("/api")
public class TmsUsersController {
	
	private static final Logger logger = LoggerFactory.getLogger(TmsUsersController.class);
	
	@Autowired
	private TmsUsersService tmsUsersService;
	
	
	 @Autowired
	    private AuthenticationManager authenticationManager;

	    @Autowired
	    private UserDetailsService tmsUsersDetailsService;

	    @Autowired
	    private JwtUtil jwtUtil;
	
	
	@PostMapping("/userRegistration")
	public ResponseEntity<RestAPIResponse> saveUsers(@RequestBody   TmsUsersInfo tmsUsersInfo){
		try {
			TmsUsersInfo info =	tmsUsersService.saveUsers(tmsUsersInfo);
			logger.info("User registered successfully: {}", info.getEmail());
			return ResponseEntity.ok(new RestAPIResponse("success", "User registered successfully", info));
		} catch (Exception e) {
			logger.error("Error registering user: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new RestAPIResponse("fail", "Error registering user" ,e.getMessage()));
		}
	}
	
	
	 @PostMapping("/login")
	    public ResponseEntity<RestAPIResponse> login(@RequestBody AuthRequest authRequest) {
		
				 try {	
			            Authentication authentication = authenticationManager.authenticate(
			                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
			            );
				logger.info("!!! inside class : LoginController, !! method : authenticateAndGetToken inside try block");

			} catch (BadCredentialsException e) {
				logger.info("!!! inside class: LoginController, !! method: generateJwtToken => Invalid Credentials");
				 return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("failed","Invalid Credentials"),HttpStatus.UNAUTHORIZED);
			} catch (UsernameNotFoundException e) {
				logger.error("Username not found: {}", authRequest.getEmail());
				 return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "User with email " , e), HttpStatus.UNAUTHORIZED);
			} catch (Exception e) {
				logger.error("An error occurred: {}", e.getMessage());
				 return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "An error occurred: ", e), HttpStatus.UNAUTHORIZED);

			}
				 
	    AuthResponse response= new AuthResponse();
					String token = jwtUtil.generateToken(authRequest.getEmail());
					response.setToken(token);
					response.setEmail(authRequest.getEmail());
					
				 return new ResponseEntity<>(new RestAPIResponse("success", "User logged in successful", response), HttpStatus.OK);
	    }

}
