package com.narvee.ats.auth.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.narvee.ats.auth.client.EmailClientService;
import com.narvee.ats.auth.commons.RestAPIResponse;
import com.narvee.ats.auth.dto.EmailVerificationDTO;
import com.narvee.ats.auth.dto.ResetPassword;
import com.narvee.ats.auth.entity.OTP;
import com.narvee.ats.auth.entity.TmsPrivilege;
import com.narvee.ats.auth.entity.TmsRoles;
import com.narvee.ats.auth.entity.TmsUsers;
import com.narvee.ats.auth.repository.OtpRepository;
import com.narvee.ats.auth.service.EmailLoginService;
import com.narvee.ats.auth.service.TmsUsersService;
import com.narvee.ats.auth.tms.dto.AuthRequest;
import com.narvee.ats.auth.tms.dto.AuthResponse;
import com.narvee.ats.auth.tms.dto.TmsUsersInfo;
import com.narvee.ats.auth.util.JwtUtil;
import com.narvee.ats.auth.util.OTPGenerator;


@RestController
@RequestMapping("/tms")
public class TmsUsersController {

	private static final Logger logger = LoggerFactory.getLogger(TmsUsersController.class);

	@Autowired
	private TmsUsersService tmsUsersService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private OtpRepository otpRepo;
	
 
	@Autowired
	private EmailClientService emailClientService;
	
	@Autowired
	private EmailLoginService emailLoginService;

	@PostMapping(  "/userRegistration")
	public ResponseEntity<RestAPIResponse> saveUsers(@RequestBody TmsUsersInfo tmsUsersInfo) {
		
		logger.info("!!! inside class: TmsUsersController, !! method: saveUsers ");
		
		try {
			TmsUsersInfo info = tmsUsersService.saveUsers(tmsUsersInfo);
			logger.info("User registered successfully: {}", info.getEmail());
			return ResponseEntity.ok(new RestAPIResponse("success", "User registered successfully", info));
		} catch (IllegalArgumentException e) {
			logger.error("Error registering user: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.OK)
					.body(new RestAPIResponse("fail", "Error registering user", e.getMessage()));
		
	 }catch (Exception e) {
		logger.error("Error registering user: {}", e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.OK)
				.body(new RestAPIResponse("fail", "Error registering user", "Error registering user"));
	}
	}
	
	
	@PostMapping( value = "/uploadPic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<RestAPIResponse> uploadProfile (  @RequestParam Long id,
			@RequestPart("photo") MultipartFile photo) throws IOException {
		logger.info("!!! inside class: TmsUsersController, !! method: uploadPic ");
		tmsUsersService.uploadProfilePhoto(id, photo);
		return ResponseEntity.status(HttpStatus.OK)
				.body(new RestAPIResponse("success", "User profile photo uploded successfully"));
		
	}

	@PostMapping("/login")
	public ResponseEntity<RestAPIResponse> login(@RequestBody AuthRequest authRequest) {
		logger.info("!!! inside class: TmsUsersController, !! method: login ");
		// Authentication authentication = null;
		TmsUsers tmsUserDetails = tmsUsersService.findByEmail(authRequest.getEmail());

		String username = authRequest.getEmail() + "|" + authRequest.getLoginType();
		if (!tmsUsersService.isOTPValid(authRequest.getOtpId(), authRequest.getOtp())) {
			return new ResponseEntity<>(new RestAPIResponse("fail", "The OTP you entered is incorrect."), HttpStatus.OK);
		}
//		try {
//			authentication = authenticationManager
//					.authenticate(new UsernamePasswordAuthenticationToken(username, authRequest.getPassword()));
//			logger.info("!!! inside class : LoginController, !! method : authenticateAndGetToken inside try block");
//
//		} catch (BadCredentialsException e) {
//			logger.info("!!! inside class: LoginController, !! method: generateJwtToken => Invalid Credentials");
//			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("failed", "Invalid Credentials"),
//					HttpStatus.OK);
//		} catch (UsernameNotFoundException e) {
//			logger.error("Username not found: {}", authRequest.getEmail());
//			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("failed", "User Not Found  With Email  "),
//					HttpStatus.OK);
//		} catch (Exception e) {
//			logger.error("An error occurred: {}", e.getMessage());
//			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("failed", "Failed to login ! please contact our support team."),
//					HttpStatus.OK);
//
//		}

		AuthResponse response = new AuthResponse();
		String token = jwtUtil.generateTokenTms(username);

		TmsRoles userRole = tmsUserDetails.getRole();
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		List<String> rolePrivileges = new ArrayList<>();

		if (null != userRole) {

			for (TmsPrivilege privilege : userRole.getPrivileges()) {
				authorities.add(new SimpleGrantedAuthority(privilege.getName()));
				rolePrivileges.add(privilege.getName());
				rolePrivileges.add(privilege.getName().replaceAll("\\d", ""));
			}
		}

		response.setRolePrivileges(rolePrivileges);
		response.setToken(token);
		response.setEmail(authRequest.getEmail());
		response.setUserId(tmsUserDetails.getUserId());
		response.setFirstName(tmsUserDetails.getFirstName());
		response.setMiddleName(tmsUserDetails.getMiddleName());
		response.setLastName(tmsUserDetails.getLastName());
		response.setProfilePic(tmsUserDetails.getProfilePhoto());
		response.setUserRole(userRole.getRolename());
		response.setAdminId(tmsUserDetails.getAdminId());
		response.setOrganizationName(tmsUserDetails.getOrganisationName());
		response.setCompanyDomain(tmsUserDetails.getCompanyDomain());
		response.setCompanySize(tmsUserDetails.getCompanySize());
		response.setIndustry(tmsUserDetails.getIndustry());
		response.setContactNumber(tmsUserDetails.getContactNumber());
		response.setPosition(tmsUserDetails.getPosition());

		return new ResponseEntity<>(new RestAPIResponse("success", "User logged in successfully", response),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/forgotPassword", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> forgotPassword(@RequestBody ResetPassword user) {
		logger.info("!!! inside class: TmsUsersController, !! method: forgotPassword ");
		TmsUsers userDetails = tmsUsersService.findByEmail(user.getEmail());

		if (userDetails != null) {
			long expirationTime = System.currentTimeMillis() + OTP.OTPEXP;
			OTP otp = new OTP();
			String genOtp = OTPGenerator.generateRandomPassword(4);
			otp.setExpirationTime(expirationTime);
			otp.setTmsUserId(userDetails.getUserId());
			otp.setOtp(genOtp);
			user.setUsername(userDetails.getFirstName());
			user.setOtp(genOtp);
			OTP otpRes = otpRepo.save(otp);
		//	emailClientService.resetlinkmail(user);
			emailLoginService.sendTmsForgotPassword(user);
			return new ResponseEntity<>(new RestAPIResponse("success", "OTP sent successfully", otpRes), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(
					new RestAPIResponse("failed", "User does not exist. Please check the username and try again."),
					HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/emailVerification/{email}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> emailVerification(@PathVariable String email) {
		logger.info("!!! inside class: TmsUsersController , !! method: emailVerification");
		OTP status = tmsUsersService.emailVerification(email);
		if (status == null) {
			return new ResponseEntity<>(
					new RestAPIResponse("fail",
							"Email service currently not available at this moment..Please try after some time..."),
					HttpStatus.OK);
		} else if (status.getId() != null) {
			return new ResponseEntity<>(new RestAPIResponse("success", "OTP sent successfully", status), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new RestAPIResponse("fail", "User already exist."), HttpStatus.OK);
		}
	}

	@GetMapping("/validate/{id}/{enteredOTP}")
	public ResponseEntity<RestAPIResponse> validateOTP(@PathVariable Long id, @PathVariable String enteredOTP) {
		logger.info("!!! inside class: TmsUsersController, !! method: validateOTP ");
		if (tmsUsersService.isOTPValid(id, enteredOTP)) {
			return new ResponseEntity<>(new RestAPIResponse("success", "Please Reset your password"), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new RestAPIResponse("failed", "The OTP you entered is incorrect."), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/change_password", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> changePasswordExternalLink(@RequestBody ResetPassword restpswEntity) {
		logger.info("!!! inside class: LoginController, !! method: changePasswordExternalLink ");
		TmsUsers validateuser = tmsUsersService.findByEmail(restpswEntity.getEmail());
		if (validateuser == null) {
			return new ResponseEntity<>(new RestAPIResponse("mailerror", "Email not registered with Task Management  Portal"),
					HttpStatus.OK);
		} else {
			TmsUsers validuser = validateuser;
			validuser.setPassword(passwordEncoder.encode(restpswEntity.getNewPassword()));
			tmsUsersService.updatePassword(validuser, restpswEntity.getNewPassword());
			ResetPassword resetPassword = new ResetPassword();
			resetPassword.setUsername(validateuser.getFirstName());
			resetPassword.setPassword(restpswEntity.getNewPassword());
			resetPassword.setEmail(restpswEntity.getEmail());
		//	emailClientService.changeUserPassword(resetPassword);
			emailLoginService.sendTmsChangePassword(resetPassword);
			return new ResponseEntity<>(new RestAPIResponse("success", "Your Password has been successfully changed"),
					HttpStatus.OK);
		}

	}

	@PostMapping("/verify-old-password")
	public ResponseEntity<RestAPIResponse> verifyOldPassword(@RequestBody ResetPassword restpswEntity) {
		logger.info("!!! inside class: LoginController, !! method: verifyOldPassword ");
		TmsUsers user = tmsUsersService.findByEmail(restpswEntity.getEmail());

		if (user == null) {
			return new ResponseEntity<>(new RestAPIResponse("failed ", "User not found"), HttpStatus.OK);
		}
		boolean isMatch = passwordEncoder.matches(restpswEntity.getPassword(), user.getPassword());

		if (isMatch) {
			return new ResponseEntity<>(new RestAPIResponse("Success ", "password verified"), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new RestAPIResponse("failed ", "Old password does not match"), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/authantication-tms", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> authantication(@RequestBody AuthRequest request) {
		logger.info("!!! inside class: LoginController, !! method: signin ");

		String username = request.getEmail() + "|" + request.getLoginType();
		TmsUsers tmsUserDetails = tmsUsersService.findByEmail(request.getEmail());
		
		 
		    if (tmsUserDetails == null) {
		        logger.error("User not found with email: {}", request.getEmail());
		        return new ResponseEntity<>(
		            new RestAPIResponse("fail", "User not found with email"),
		            HttpStatus.OK
		        );
		    }
		

//		try {
//			authenticationManager
//					.authenticate(new UsernamePasswordAuthenticationToken(username, request.getPassword()));
//			logger.info("!!! inside class : LoginController, !! method : authenticateAndGetToken inside try block");
//
//		} catch (BadCredentialsException e) {
//			logger.info("!!! inside class: LoginController, !! method: generateJwtToken => Invalid Credentials");
//			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("Failed", "Invalid Credentials"),HttpStatus.OK);
//		}
//		 catch (Exception e) {
//			logger.error("An error occurred: {}", e.getMessage());
//			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("Failed", "Failed to login! please contact our support team."),
//					HttpStatus.OK);
//
//		}

		Long otpid = null;
		if (tmsUserDetails != null) {
			long expirationTime = System.currentTimeMillis() + OTP.OTPEXP;
			OTP otp = new OTP();
			String genOtp = OTPGenerator.generateRandomPassword(6);
			otp.setExpirationTime(expirationTime);
			otp.setTmsUserId((tmsUserDetails.getUserId()));
			otp.setOtp(genOtp);
			OTP otpRes = otpRepo.save(otp);
			EmailVerificationDTO emailVerificationDTO = new EmailVerificationDTO();
			emailVerificationDTO.setOtp(genOtp);
			emailVerificationDTO.setEmail(tmsUserDetails.getEmail());
			emailVerificationDTO.setUserName(tmsUserDetails.getFirstName());
			otpid = otp.getId();
			try {
				//emailClientService.login(emailVerificationDTO);
				emailLoginService.sendTmsLoginOtp(emailVerificationDTO);
			} catch (Exception e) {
				return new ResponseEntity<>(
						new RestAPIResponse("Fail", "Email service is not available. Please try again later.", otpid),
						HttpStatus.OK);
			}

		}
		logger.info("!!! inside class: LoginController, !! method: end of signin return response => ");
		return new ResponseEntity<>(new RestAPIResponse("success", "OTP sent successfully", otpid), HttpStatus.OK);

	}

}
