package com.narvee.ats.auth.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.narvee.ats.auth.client.EmailClientService;
import com.narvee.ats.auth.commons.RestAPIResponse;
import com.narvee.ats.auth.dto.EmailVerificationDTO;
import com.narvee.ats.auth.dto.ResetPassword;
import com.narvee.ats.auth.entity.OTP;
import com.narvee.ats.auth.entity.Privilege;
import com.narvee.ats.auth.entity.Roles;
import com.narvee.ats.auth.entity.UserTracker;
import com.narvee.ats.auth.entity.Users;
import com.narvee.ats.auth.exception.DisabledUserException;
import com.narvee.ats.auth.exception.InvalidUserCredentialsException;
import com.narvee.ats.auth.repository.ConsultantActivityHistoryRepo;
import com.narvee.ats.auth.repository.IPrivilegeRepository;
import com.narvee.ats.auth.repository.IRoleRepository;
import com.narvee.ats.auth.repository.OtpRepository;
import com.narvee.ats.auth.request.LoginVo;
import com.narvee.ats.auth.response.Response;
import com.narvee.ats.auth.service.ConsultantActivtyService;
import com.narvee.ats.auth.service.EmailLoginService;
import com.narvee.ats.auth.service.IAssociatedCompanyService;
import com.narvee.ats.auth.service.ILoginService;
import com.narvee.ats.auth.service.IUserService;
import com.narvee.ats.auth.service.LoginDetailsService;
import com.narvee.ats.auth.serviceimpl.LoginTrackerServiceImpl;
import com.narvee.ats.auth.util.EncryptionUtil;
import com.narvee.ats.auth.util.IPUtils;
import com.narvee.ats.auth.util.JwtUtil;
import com.narvee.ats.auth.util.OTPGenerator;

@RestController
@RequestMapping("/login")
@CrossOrigin
public class Logincontroller {
	public static final Logger logger = LoggerFactory.getLogger(Logincontroller.class);

	@Autowired
	public ILoginService loginService;

	@Autowired
	private IUserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private EmailClientService emailClientService;

	@Autowired
	private OtpRepository otpRepo;

	@Autowired
	private LoginDetailsService consultantLoginService;
	
	@Autowired
	private ConsultantActivityHistoryRepo activityHistoryRepo;

	@Autowired // to genarate token
	private JwtUtil jwtUtil;
	
	@Autowired
	private ConsultantActivtyService activtyService;
	
	@Autowired
	private EmailLoginService emailLoginService;
	
	@Autowired
	private IAssociatedCompanyService associatedCompanys;
	
	@Autowired
	private LoginTrackerServiceImpl loginTrackerService;

	@Value("${unlock.sadmin}")
	private String sadmin;

	@Value("${unlock.admin}")
	private String admin;

	@RequestMapping(value = "/authantication", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> authantication(HttpServletRequest servletRequest,@RequestBody LoginVo request) {
	    logger.info("!!! inside class: LoginController, !! method: signin ");
	    
	    String email = request.getEmail();

	    // Step 1: Check if user exists
	    Users userDetails = loginService.findByEmail(email);
	    if (userDetails == null) {
	        return new ResponseEntity<>(new RestAPIResponse("fail", "Invalid credentials"), HttpStatus.UNAUTHORIZED);
	    }


		/* ----------IP tracking block here ---------- */
		String systemIp = IPUtils.getClientIP(servletRequest);  // LAN IP
        String networkIp = IPUtils.getNetworkIP();              // Public IP
        logger.info("Client system IP: {}, network IP: {}", systemIp, networkIp);
        
	    // Step 2: Check if user is a consultant (not allowed)
	    if (userDetails.getDesignation().equalsIgnoreCase("consultant")) {
	    	loginTrackerService.save(userDetails, systemIp, networkIp, "Failed", "User not an employee");
	        return new ResponseEntity<>(new RestAPIResponse("Fail", "User not belongs to Employee"), HttpStatus.FORBIDDEN);
	    }

	    // Step 3: Generate OTP and send
	    Long otpid = null;
	    long expirationTime = System.currentTimeMillis() + OTP.OTPEXP;

	    OTP otp = new OTP();
	    String genOtp = OTPGenerator.generateRandomPassword(6);
	    otp.setExpirationTime(expirationTime);
	    otp.setUserid(userDetails.getUserid());
	    otp.setOtp(genOtp);

	    OTP otpRes = otpRepo.save(otp);
	    otpid = otp.getId();

	    EmailVerificationDTO emailVerificationDTO = new EmailVerificationDTO();
	    emailVerificationDTO.setOtp(genOtp);
	    emailVerificationDTO.setEmail(email);
	    emailVerificationDTO.setUserName(userDetails.getFullname());

	    logger.info("!!! inside class: LoginController, !! OTP sent successfully{}", otpRes);
	    
	    logger.info("Login validation success for userId={}, email={}", userDetails.getUserid(), email);
	    loginTrackerService.save(userDetails, systemIp, networkIp, "Success", "OTP sent successfully");
	    // Send OTP via email
	    emailLoginService.sendAtsLoginOtp(emailVerificationDTO);

	    logger.info("!!! inside class: LoginController, !! method: end of signin return response => ");
	    return new ResponseEntity<>(new RestAPIResponse("success", "otp send successful", otpid), HttpStatus.OK);
	}
	
/*	@RequestMapping(value = "/signin", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> generateJwtToken(@RequestBody LoginVo request) {
		logger.info("!!! inside class: LoginController, !! method: signin");

		String email = request.getEmail();
		String enteredOtp = request.getOtp();
		Long otpId = request.getOtpId();

		// Step 1: Retrieve user details
		Users userDetails = loginService.findByEmail(email);
		if (userDetails == null) {
			logger.warn("User not found for email: {}", email);
			return new ResponseEntity<>(new RestAPIResponse("fail", "User not found"), HttpStatus.NOT_FOUND);
		}

		// Step 2: Check if user belongs to employees
		if (userDetails.getDesignation().equalsIgnoreCase("consultant")) {
			return new ResponseEntity<>(new RestAPIResponse("fail", "User not belongs to Employee"), HttpStatus.OK);
		}

		// Step 3: Validate OTP
		if (!loginService.isOTPValid(otpId, enteredOtp)) {
			logger.warn("Invalid or expired OTP entered for user: {}", enteredOtp);
			return new ResponseEntity<>(new RestAPIResponse("failed", "Invalid or expired OTP"),
					HttpStatus.UNAUTHORIZED);
		}

		// Step 4: Lock user if inactive for 3 days
		LocalDateTime lastLogin = userDetails.getLastLogin() != null ? userDetails.getLastLogin() : LocalDateTime.now();
		LocalDateTime currentTime = LocalDateTime.now();
		long daysSinceLastLogin = ChronoUnit.DAYS.between(lastLogin, currentTime);

		if (!userDetails.getRole().getRolename().equalsIgnoreCase("admin")
				&& !userDetails.getRole().getRolename().equalsIgnoreCase("sadmin")) {
			if (daysSinceLastLogin > 3) {
				userDetails.setLocked(true);
				userService.saveUser(userDetails, "");
				logger.warn("User account locked due to inactivity: {}", email);
				return new ResponseEntity<>(
						new RestAPIResponse("locked", "User inactive for 3 days", "User Account Locked"),
						HttpStatus.FORBIDDEN);
			}
		}

		// Step 5: Handle inactive account status
		if (userDetails.getStatus().equalsIgnoreCase("InActive")) {
			return new ResponseEntity<>(new RestAPIResponse("inactive", "Account Inactive"), HttpStatus.FORBIDDEN);
		}

		// Step 6: Generate JWT token
		List<Long> associatedCompanyIds = associatedCompanys.findByAssociatedCompanyId(userDetails.getCompanyid());
		associatedCompanyIds.add(userDetails.getCompanyid());
		String token = jwtUtil.generateTokenWithCompanies(email, associatedCompanyIds);

		// Step 7: Prepare response object
		Response response = new Response();
		response.setToken(token);
		response.setFullname(userDetails.getFullname());
		response.setDesignation(userDetails.getDesignation());
		response.setUserid(userDetails.getUserid());
		response.setDepartment(userDetails.getDepartment());
		response.setLastlogin(lastLogin);
		response.setLastlogout(userDetails.getLastLogout());

		try {
			response.setCompanyid(EncryptionUtil.encrypt(userDetails.getCompanyid()));
		} catch (Exception e) {
			logger.error("Error encrypting company ID for user: {}", email, e);
		}

		Roles userRole = userDetails.getRole();
		Collection<e
		> authorities = new ArrayList<>();
		List<String> rolePrivileges = new ArrayList<>();

		if (userRole != null) {
			for (Privilege privilege : userRole.getPrivileges()) {
				authorities.add(new SimpleGrantedAuthority(privilege.getName()));
				rolePrivileges.add(privilege.getName().replaceAll("\\d", ""));
			}
		}
		response.setRolePrivileges(rolePrivileges);

		// Step 9: Adjust name for Bench Sales department
		if ("Bench Sales".equalsIgnoreCase(userDetails.getDepartment())) {
			response.setFullname(userDetails.getPseudoname());
		}

		// Step 10: Update last login time
		userDetails.setLastLogin(currentTime);
		userService.saveUser(userDetails, "");

		// Step 11: Return success response
		logger.info("User logged in successfully: {}", email);
		return new ResponseEntity<>(new RestAPIResponse("success", "User logged in successfully", response),
				HttpStatus.OK);
	}	*/

	@RequestMapping(value = "/signin", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> generateJwtToken(@RequestBody LoginVo request) {
		logger.info("!!! inside class: LoginController, !! method: signin ");
		String email = request.getEmail();
		// getting user data
		Users userDetails = loginService.findByEmail(email);
		if (userDetails != null) {
			if (!userDetails.getDesignation().equalsIgnoreCase("consultant")) {
				return new ResponseEntity<>(new RestAPIResponse("fail", "User not belongs to Employee"), HttpStatus.OK);
			}
		}

		Authentication authentication = null;
		try {
			authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		} catch (DisabledException e) {
			logger.info("!!! inside class: LoginController, !! method: generateJwtToken => User Inactive");
			throw new DisabledUserException("User Inactive");
		} catch (BadCredentialsException e) {
			logger.info("!!! inside class: LoginController, !! method: generateJwtToken => Invalid Credentials");
			throw new InvalidUserCredentialsException("Invalid Credentials");
		}
		// to get user
		User user = (User) authentication.getPrincipal();
		// to get user roles
		Set<String> roles = user.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toSet());
		// to genearte jwt token
		String token = jwtUtil.generateToken(authentication);
		Response response = new Response();// send response back to client
		response.setToken(token);
		roles.forEach(rl -> {
			response.setRoles(rl);
		});
		List<String> rolePrivileges = new ArrayList<>();
		response.setFullname(user.getUsername());

//		System.out.println(userDetails);
		// lock the user if not login for 3 days
		LocalDateTime logintTime = userDetails.getLastLogin();
		if (logintTime == null) {
			logintTime = LocalDateTime.now();
		}
		LocalDateTime currenetTime = LocalDateTime.now();
		long minutesSinceLastLogin = ChronoUnit.DAYS.between(logintTime, currenetTime);

		if (userDetails.getRole().getRolename().equalsIgnoreCase(admin)
				|| userDetails.getRole().getRolename().equalsIgnoreCase(sadmin)) {
		} else {
			if (minutesSinceLastLogin > 3) {

				// commented by kiran need implement this
				userDetails.setLocked(true);
				userService.saveUser(userDetails, "");
				return new ResponseEntity<>(
						new RestAPIResponse("locked", "User in-active for 3 days", "User Account Locked"),
						HttpStatus.OK);
			}

		}
		userDetails.setLastLogin(currenetTime);
		userService.saveUser(userDetails, "");
		if (userDetails.getStatus().equalsIgnoreCase("InActive")) {
			return new ResponseEntity<>(new RestAPIResponse("inactive", "Account In Active"), HttpStatus.FORBIDDEN);
		} else {
			Roles userRole = userDetails.getRole();
			Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
			if (null != userRole) {
				
//	List<Long>	selectedPrivs= roleRepo.findPrivilegesAssignedByGivenRoleAndCompany(userRole.getRoleid(),userDetails.getCompanyid());
//				
//				Set<Privilege> privilegesTableData= new HashSet<>();
//				
//				for (Long selectedId : selectedPrivs) {
//					privilegesTableData.add(privRepo.findById(selectedId).get());
//				}
				
				for (Privilege privilege : userRole.getPrivileges()) {
					authorities.add(new SimpleGrantedAuthority(privilege.getName()));
//					rolePrivileges.add(privilege.getName());
					rolePrivileges.add(privilege.getName().replaceAll("\\d", ""));
				}
			}

			String name = userDetails.getFullname();
			String dept = userDetails.getDepartment();
			if (dept.equalsIgnoreCase("Bench Sales")) {
				name = userDetails.getPseudoname();
			}

			response.setRolePrivileges(rolePrivileges);
			response.setFullname(name);
			response.setDesignation(userDetails.getDesignation());
			response.setUserid(userDetails.getUserid());
			try {
				response.setCompanyid(EncryptionUtil.encrypt(userDetails.getCompanyid()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setDepartment(dept);
			response.setLastlogin(userDetails.getLastLogin());
			response.setLastlogout(userDetails.getLastLogout());
			
//		List<String> cardPrivleges = loginService.cardPrivileges(userDetails.getRole().getRoleid());
//		response.setCardPrivileges(cardPrivleges);
			
			logger.info("!!! inside class: LoginController, !! method: end of signin return response => ");
			return new ResponseEntity<>(new RestAPIResponse("success", "User logged in successful", response),
					HttpStatus.OK);
		}

	
	}
//========================================================
	
//	@RequestMapping(value = "/logout/{id}", method = RequestMethod.GET, produces = "application/json")
//	public ResponseEntity<?> signOut(@PathVariable("id") Long id) {
//		logger.info("!!! inside class: LoginController, !! method: signOut => ");
//		Users optionalUser = userService.finduserById(id);
//		if (optionalUser != null) {
//			LocalDateTime local = LocalDateTime.now();
//			optionalUser.setLastLogout(local);
//			List<UserTracker> tracker = new ArrayList<>();
////			UserTracker track = new UserTracker();
////			track.setLastlogout(LocalDateTime.now());
////			track.setUsername(optionalUser.getFullname());
////			tracker.add(track);
//			// optionalUser.setUsertracker(tracker);
//			userService.saveUser(optionalUser, "");
//			return ResponseEntity.ok().build();
//		} else {
//			return ResponseEntity.notFound().build();
//		}
//	}
//	
	
	@RequestMapping(value = "/logout/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> signOut(@PathVariable("id") Long id, HttpServletRequest request) {
	    logger.info("!!! inside class: LoginController, !! method: signOut => ");
	    Users user = userService.finduserById(id);

	    if (user != null) {
	        LocalDateTime logoutTime = LocalDateTime.now();

	        // Set user's last logout time
	        user.setLastLogout(logoutTime);
	        userService.saveUser(user, "");

	        // Save the logout details in the UserTracker table
	        String ipAddress = request.getRemoteAddr(); // Fetch client IP address
	        Optional<UserTracker> existingTrackerOpt = activityHistoryRepo.findTopByUserIdOrderByLastloginDesc(id);

	        if (existingTrackerOpt.isPresent()) {
	            // Update the existing tracker with logout time
	            UserTracker existingTracker = existingTrackerOpt.get();
	            existingTracker.setLastlogout(logoutTime);
	            activityHistoryRepo.save(existingTracker);
	        } else {
	            // Handle the edge case where no previous login record exists
	            UserTracker tracker = new UserTracker();
	            tracker.setUserId(id);
	            tracker.setLastlogout(logoutTime);
	            tracker.setIpAddress(ipAddress);
	            activityHistoryRepo.save(tracker);
	        }

	        return ResponseEntity.ok(new RestAPIResponse("success", "User logged out successfully."));
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(new RestAPIResponse("fail", "User not found."));
	    }
	}


//	 reset password from my user rofile
	@RequestMapping(value = "/reset_password", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> loginCheck(@RequestBody ResetPassword restpswEntity) {
		System.out.println(restpswEntity);
		String reOldpsw = restpswEntity.getPassword();
		String newPassword = restpswEntity.getNewPassword();

		Users users = loginService.findbyuserid(restpswEntity.getUserid());
		String dboldpassword = users.getPassword();
		if (!passwordEncoder.matches(reOldpsw, dboldpassword)) {
			return new ResponseEntity<>(new RestAPIResponse("fail", "password is incorrect"), HttpStatus.OK);
		} else {
			if (newPassword.equals(reOldpsw)) {
				return new ResponseEntity<>(
						new RestAPIResponse("samepassword", "New Password And OldPassword are Same"), HttpStatus.OK);
			} else {
				users.setPassword(passwordEncoder.encode(newPassword));
				loginService.updatePassword(users, restpswEntity.getNewPassword());
				return new ResponseEntity<>(new RestAPIResponse("success", "Your Password has been changed"),
						HttpStatus.OK);
			}
		}

	}

	@RequestMapping(value = "/change_password", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> changePasswordExternalLink(@RequestBody ResetPassword restpswEntity) {
		logger.info("!!! inside class: LoginController, !! method: changePasswordExternalLink ");
		Users validateuser = loginService.findByEmail(restpswEntity.getEmail());
		if (validateuser == null) {
			return new ResponseEntity<>(new RestAPIResponse("mailerror", "Email not registered with Narvee Portal"),
					HttpStatus.OK);
		} else {
			Users validuser = validateuser;
			validuser.setPassword(passwordEncoder.encode(restpswEntity.getNewPassword()));
			loginService.updatePassword(validuser, restpswEntity.getNewPassword());
			ResetPassword resetPassword = new ResetPassword();
			resetPassword.setUsername(validateuser.getFullname());
			resetPassword.setPassword(restpswEntity.getNewPassword());
			resetPassword.setEmail(restpswEntity.getEmail());
			//emailClientService.changeUserPassword(resetPassword);
			emailLoginService.sendAtsChangePasswordEmail(resetPassword);
			return new ResponseEntity<>(new RestAPIResponse("success", "Your Password has been changed"),
					HttpStatus.OK);
		}

	}

	@RequestMapping(value = "kpt/signin", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> generateJwtTokenForKPT(@RequestBody LoginVo request) {
		logger.info("!!! inside class: LoginController, !! method: signin ");
		Authentication authentication = null;
		try {
			authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		} catch (DisabledException e) {
			logger.info("!!! inside class: LoginController, !! method: generateJwtToken => User Inactive");
			throw new DisabledUserException("User Inactive");
		} catch (BadCredentialsException e) {
			logger.info("!!! inside class: LoginController, !! method: generateJwtToken => Invalid Credentials");
			throw new InvalidUserCredentialsException("Invalid Credentials");
		}
		// to get user
		User user = (User) authentication.getPrincipal();
		// to get user roles
		Set<String> roles = user.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toSet());
		// to genearte jwt token
		String token = jwtUtil.generateToken(authentication);
		Response response = new Response();// send response back to client
		response.setToken(token);
		roles.forEach(rl -> {
			response.setRoles(rl);
		});
		List<String> rolePrivileges = new ArrayList<>();
		response.setFullname(user.getUsername());
		String email = request.getEmail();
		// getting user data
		Users userDetails = loginService.findByEmail(email);
		if (userDetails.getStatus().equalsIgnoreCase("InActive")) {
			return new ResponseEntity<>("inactive Account In Active", HttpStatus.FORBIDDEN);

		} else {
			Roles userRole = userDetails.getRole();
			Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
			if (null != userRole) {
				for (Privilege privilege : userRole.getPrivileges()) {
					authorities.add(new SimpleGrantedAuthority(privilege.getName()));
					rolePrivileges.add(privilege.getName());
				}
			}
			// lock the user if not login for 3 days
			LocalDateTime logintTime = userDetails.getLastLogin();
			if (logintTime == null) {
				logintTime = LocalDateTime.now();
			}
			LocalDateTime currenetTime = LocalDateTime.now();
			long minutesSinceLastLogin = ChronoUnit.DAYS.between(logintTime, currenetTime);

			if (userDetails.getRole().getRolename().equalsIgnoreCase(admin)
					|| userDetails.getRole().getRolename().equalsIgnoreCase(sadmin)) {
			} else {
				if (minutesSinceLastLogin > 3) {
					userDetails.setLocked(true);
					userService.saveUser(userDetails, "");
					return new ResponseEntity<>(
							new RestAPIResponse("locked", "User in-active for 3 days", "User Account Locked"),
							HttpStatus.OK);
				}
			}
			String name = userDetails.getFullname();
			String dept = userDetails.getDepartment();
			if (dept.equalsIgnoreCase("Bench Sales")) {
				name = userDetails.getPseudoname();
			}

			response.setRolePrivileges(rolePrivileges);
			response.setFullname(name);
			response.setDesignation(userDetails.getDesignation());
			response.setUserid(userDetails.getUserid());
			response.setDepartment(dept);
			response.setLastlogin(userDetails.getLastLogin());
			response.setLastlogout(userDetails.getLastLogout());
			logger.info("!!! inside class: LoginController, !! method: end of signin return response => ");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/forgotPassword", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> forgotPassword(@RequestBody ResetPassword user) {
		logger.info("!!! inside class: LoginController, !! method: processForgotPassword ");
		Users userDetails = loginService.findByEmail(user.getEmail());
		if (userDetails != null) {
			long expirationTime = System.currentTimeMillis() + OTP.OTPEXP;
			OTP otp = new OTP();
			String genOtp = OTPGenerator.generateRandomPassword(4);
			otp.setExpirationTime(expirationTime);
			otp.setUserid(userDetails.getUserid());
			otp.setOtp(genOtp);
			user.setUsername(userDetails.getFullname());
			user.setOtp(genOtp);
			OTP otpRes = otpRepo.save(otp);
			//emailClientService.resetlinkmail(user);
			emailLoginService.sendAtsForgotPassword(user);
			return new ResponseEntity<>(new RestAPIResponse("success", "OTP sent Successfully", otpRes), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(
					new RestAPIResponse("failed", "User does not exist. Please check the username and try again."),
					HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/emailVerification/{email}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> emailVerification(@PathVariable String email) {
		logger.info("!!! inside class: ConsultantLoginController , !! method: emailVerification");
		OTP status = consultantLoginService.emailVerification(email);
		if (status == null) {
			return new ResponseEntity<>(
					new RestAPIResponse("fail",
							"Email service currently not available at this moment..Please try after some time..."),
					HttpStatus.OK);
		} else if (status.getId() != null) {
			return new ResponseEntity<>(new RestAPIResponse("success", "OTP sent Successfully", status), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new RestAPIResponse("fail", "Consultant already exist."), HttpStatus.OK);
		}
	}

	@GetMapping("/validate/{id}/{enteredOTP}")
	public ResponseEntity<RestAPIResponse> validateOTP(@PathVariable Long id, @PathVariable String enteredOTP) {
		logger.info("!!! inside class: LoginController, !! method: validateOTP ");
		if (loginService.isOTPValid(id, enteredOTP)) {
			return new ResponseEntity<>(new RestAPIResponse("success", "OTP is valid"), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new RestAPIResponse("failed", "OTP is invalid or has expired."), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/userRegistration", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> ConsultantRegistration(@RequestBody Users user) {
		System.err.println(user);
		logger.info("!!! inside class: LoginController , !! method: ConsultantRegistration");
		Users users = loginService.registerNewUser(user);
		if (users.getUserid() != null) {

			return new ResponseEntity<>(new RestAPIResponse("success", "user Registred Successfully"), HttpStatus.OK);
		} else {

			return new ResponseEntity<>(new RestAPIResponse("fail", "user already Exists"), HttpStatus.OK);
		}
	}
	
	@Autowired
	private IPrivilegeRepository privRepo;

	@Autowired
	private IRoleRepository roleRepo;

	@RequestMapping(value = "/authSignIn", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> authSignIn(@RequestBody LoginVo request) {
		logger.info("Inside LoginController::authSignIn");

		String email = request.getEmail();
		String enteredOtp = request.getOtp();
		Long otpId = request.getOtpId();

		Users userDetails = loginService.findByEmail(email);
		if (userDetails == null) {
			logger.warn("User not found for email: {}", email);
			return new ResponseEntity<>(new RestAPIResponse("fail", "User not found"), HttpStatus.NOT_FOUND);
		}

		if (userDetails.getDesignation().equalsIgnoreCase("consultant")) {
			return new ResponseEntity<>(new RestAPIResponse("fail", "User not belongs to Employeer"), HttpStatus.OK);
		}

		if (!loginService.isOTPValid(otpId, enteredOtp)) {
			logger.warn("Invalid OTP for email: {}", email);
			return new ResponseEntity<>(new RestAPIResponse("fail", "Invalid or expired OTP"), HttpStatus.UNAUTHORIZED);
		}

		// Check inactivity and lock user if needed
		LocalDateTime lastLogin = userDetails.getLastLogin() != null ? userDetails.getLastLogin() : LocalDateTime.now();
		long daysInactive = ChronoUnit.DAYS.between(lastLogin, LocalDateTime.now());
		if (!userDetails.getRole().getRolename().equalsIgnoreCase(admin)
				&& !userDetails.getRole().getRolename().equalsIgnoreCase(sadmin) && daysInactive > 3) {
			userDetails.setLocked(true);
			userService.saveUser(userDetails, "");
			logger.warn("User account locked due to inactivity: {}", email);
			return new ResponseEntity<>(
					new RestAPIResponse("locked", "User inactive for 3 days", "User Account Locked"),
					HttpStatus.FORBIDDEN);
		}

		if (userDetails.getStatus().equalsIgnoreCase("InActive")) {
			return new ResponseEntity<>(new RestAPIResponse("inactive", "Account is inactive"), HttpStatus.FORBIDDEN);
		}

		// Generate JWT token
		List<Long> associatedCompanyIds = associatedCompanys.findByAssociatedCompanyId(userDetails.getCompanyid());
		associatedCompanyIds.add(userDetails.getCompanyid());
		String token = jwtUtil.generateTokenWithCompanies(email, associatedCompanyIds);

		// Prepare response
		Response response = new Response();
		response.setRoles(userDetails.getRole().getRolename());
		response.setToken(token);
		response.setFullname("Bench Sales".equalsIgnoreCase(userDetails.getDepartment()) ? userDetails.getPseudoname()
				: userDetails.getFullname());
		response.setDesignation(userDetails.getDesignation());
		response.setUserid(userDetails.getUserid());
		response.setDepartment(userDetails.getDepartment());
		response.setLastlogin(lastLogin);
		response.setLastlogout(userDetails.getLastLogout());
		try {
			response.setCompanyid(EncryptionUtil.encrypt(userDetails.getCompanyid()));
		} catch (Exception e) {
			logger.error("Error encrypting company ID for user: {}", email, e);
		}

		// Set role privileges
		List<String> rolePrivileges = userDetails.getRole().getPrivileges().stream()
				.map(privilege -> privilege.getName().replaceAll("\\d", "")).collect(Collectors.toList());
		response.setRolePrivileges(rolePrivileges);

		userDetails.setLastLogin(LocalDateTime.now());
		userService.saveUser(userDetails, "");

		logger.info("User logged in successfully: {}", email);
		return new ResponseEntity<>(new RestAPIResponse("success", "User logged in successfully", response),
				HttpStatus.OK);
	}

//	@RequestMapping(value = "/authSignIn", method = RequestMethod.POST, produces = "application/json")
//	public ResponseEntity<RestAPIResponse> authSignIn(@RequestBody LoginVo request) {
//		logger.info("!!! inside class: LoginController, !! method: signin ");
//		
//		if (!loginService.isOTPValid(request.getOtpId(), request.getOtp())) {
//			return new ResponseEntity<>(new RestAPIResponse("fail", "Invalid OTP  "), HttpStatus.OK);
//		}
//		String email = request.getEmail();
//		// getting user data
//		Users userDetails = loginService.findByEmail(email);
//		if (userDetails != null) {
//			if (userDetails.getDesignation().equalsIgnoreCase("consultant")) {
//				return new ResponseEntity<>(new RestAPIResponse("fail", "User not belongs to Employee"),
//						HttpStatus.OK);
//			}
//		}
//
//		Authentication authentication = null;
//		try {
//			authentication = authenticationManager
//					.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
//
//		} catch (DisabledException e) {
//			logger.info("!!! inside class: LoginController, !! method: generateJwtToken => User Inactive");
//			throw new DisabledUserException("User Inactive");
//		} catch (BadCredentialsException e) {
//			logger.info("!!! inside class: LoginController, !! method: generateJwtToken => Invalid Credentials");
//			throw new InvalidUserCredentialsException("Invalid Credentials");
//		}
//		
//		// to get user
//		User user = (User) authentication.getPrincipal();
//		// to get user roles
//		Set<String> roles = user.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toSet());
//		// to genearte jwt token
//		String token = jwtUtil.generateToken(authentication);
//		Response response = new Response();// send response back to client
//		response.setToken(token);
//		roles.forEach(rl -> {
//			response.setRoles(rl);
//		});
//		List<String> rolePrivileges = new ArrayList<>();
//		response.setFullname(user.getUsername());
//
////		System.out.println(userDetails);
//		// lock the user if not login for 3 days
//		LocalDateTime logintTime = userDetails.getLastLogin();
//		if (logintTime == null) {
//			logintTime = LocalDateTime.now();
//		}
//		LocalDateTime currenetTime = LocalDateTime.now();
//		long minutesSinceLastLogin = ChronoUnit.DAYS.between(logintTime, currenetTime);
//
//		if (userDetails.getRole().getRolename().equalsIgnoreCase(admin)
//				|| userDetails.getRole().getRolename().equalsIgnoreCase(sadmin)) {
//		} else {
//			if (minutesSinceLastLogin > 3) {
//
//				// commented by kiran need implement this
//				userDetails.setLocked(true);
//				userService.saveUser(userDetails, "");
//				return new ResponseEntity<>(
//						new RestAPIResponse("locked", "User in-active for 3 days", "User Account Locked"),
//						HttpStatus.OK);
//			}
//
//		}
//		userDetails.setLastLogin(currenetTime);
//		userService.saveUser(userDetails, "");
//		if (userDetails.getStatus().equalsIgnoreCase("InActive")) {
//			return new ResponseEntity<>(new RestAPIResponse("inactive", "Account In Active"), HttpStatus.FORBIDDEN);
//		} else {
//			Roles userRole = userDetails.getRole();
//			Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
//			if (null != userRole) {
//				
////				List<Long>	selectedPrivs= roleRepo.findPrivilegesAssignedByGivenRoleAndCompany(userRole.getRoleid(),userDetails.getCompanyid());
////				
////				Set<Privilege> privilegesTableData= new HashSet<>();
////				
////				for (Long selectedId : selectedPrivs) {
////					privilegesTableData.add(privRepo.findById(selectedId).get());
////				}
//				
//				for (Privilege privilege : userRole.getPrivileges()) {
//					authorities.add(new SimpleGrantedAuthority(privilege.getName()));
////					rolePrivileges.add(privilege.getName());
//					rolePrivileges.add(privilege.getName().replaceAll("\\d", ""));
//				}
//			}
//
//			String name = userDetails.getFullname();
//			String dept = userDetails.getDepartment();
//			if (dept.equalsIgnoreCase("Bench Sales")) {
//				name = userDetails.getPseudoname();
//			}
//
//			response.setRolePrivileges(rolePrivileges);
//			response.setFullname(name);
//			response.setDesignation(userDetails.getDesignation());
//			response.setUserid(userDetails.getUserid());
//			
//				try {
//					response.setCompanyid(EncryptionUtil.encrypt(userDetails.getCompanyid()));
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			
//			response.setDepartment(dept);
//			response.setLastlogin(userDetails.getLastLogin());
//			response.setLastlogout(userDetails.getLastLogout());
//			
////		List<String> cardPrivleges = loginService.cardPrivileges(userDetails.getRole().getRoleid());
////		response.setCardPrivileges(cardPrivleges);
//			
//			logger.info("!!! inside class: LoginController, !! method: end of signin return response => ");
//			return new ResponseEntity<>(new RestAPIResponse("success", "User logged in successful", response),
//					HttpStatus.OK);
//		}
//
//	}

}
