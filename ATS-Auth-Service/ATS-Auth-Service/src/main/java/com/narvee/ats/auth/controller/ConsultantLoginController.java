package com.narvee.ats.auth.controller;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.narvee.ats.auth.commons.RestAPIResponse;
import com.narvee.ats.auth.dto.RegistrationRequest;
import com.narvee.ats.auth.entity.OTP;
import com.narvee.ats.auth.entity.Privilege;
import com.narvee.ats.auth.entity.Roles;
import com.narvee.ats.auth.entity.UserTracker;
import com.narvee.ats.auth.entity.Users;
import com.narvee.ats.auth.exception.DisabledUserException;
import com.narvee.ats.auth.exception.InvalidUserCredentialsException;
import com.narvee.ats.auth.request.LoginVo;
import com.narvee.ats.auth.response.Response;
import com.narvee.ats.auth.service.ConsultantActivtyService;
import com.narvee.ats.auth.service.ILoginService;
import com.narvee.ats.auth.service.IUserService;
import com.narvee.ats.auth.service.LoginDetailsService;
import com.narvee.ats.auth.util.JwtUtil;

@RestController
@RequestMapping("/conLogin")
public class ConsultantLoginController {
	public static final Logger logger = LoggerFactory.getLogger(ConsultantLoginController.class);

	@Autowired
	private LoginDetailsService consultantLoginService;

	@Autowired
	public ILoginService loginService;

	@Autowired
	private IUserService userService; 

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired // to genarate token
	private JwtUtil jwtUtil;
	
	@Autowired
    private ConsultantActivtyService activtyService;

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

	@RequestMapping(value = "/validateOtp/{id}/{otp}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> validateOtp(@PathVariable String otp, @PathVariable Long id) {
		logger.info("!!! inside class: ConsultantLoginController , !! method: validateOtp");
		if (consultantLoginService.validateOtp(otp, id)) {
			return new ResponseEntity<>(new RestAPIResponse("success", "OTP is valid"), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new RestAPIResponse("failed", "OTP is invalid or has expired."), HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/consultantRegistration", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> ConsultantRegistration(@RequestBody RegistrationRequest user) {
		logger.info("!!! inside class: ConsultantLoginController , !! method: ConsultantRegistration");
		
		try {
			Long conid = consultantLoginService.registerConsultant(user);
		if (conid != null) {
			return new ResponseEntity<>(new RestAPIResponse("success", "Emplolyee Registred Successfully", conid),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new RestAPIResponse("fail", "Email already Exists"), HttpStatus.OK);
		}
		}catch(Exception e ) {
			return new ResponseEntity<>(new RestAPIResponse("fail", "Employee registration failed. Please try again later"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/signin", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> generateJwtToken(@RequestBody LoginVo request) {
		logger.info("!!! inside class: ConsultantLoginController, !! method: signin ");
		String email = request.getEmail();
		// getting user data
		Users userDetails = loginService.findByEmail(email);

		if (userDetails != null) {
			if (!userDetails.getDesignation().equalsIgnoreCase("consultant")) {
				return new ResponseEntity<>(new RestAPIResponse("fail", "User not belongs to Consultant"),
						HttpStatus.OK);
			}
		}

		Authentication authentication = null;
		try {
			authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		} catch (DisabledException e) {
			logger.info("!!! inside class: ConsultantLoginController, !! method: generateJwtToken => User Inactive");
			throw new DisabledUserException("User Inactive");
		} catch (BadCredentialsException e) {
			logger.info(
					"!!! inside class: ConsultantLoginController, !! method: generateJwtToken => Invalid Credentials");
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
		userDetails.setLastLogin(currenetTime);
		userService.saveUser(userDetails, "");
		if (userDetails.getStatus().equalsIgnoreCase("InActive")) {
			return new ResponseEntity<>(new RestAPIResponse("inactive", "Account In Active"), HttpStatus.FORBIDDEN);
		} else {
			Roles userRole = userDetails.getRole();
			Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
			if (null != userRole) {
				for (Privilege privilege : userRole.getPrivileges()) {
					authorities.add(new SimpleGrantedAuthority(privilege.getName()));
					rolePrivileges.add(privilege.getName());
				}
			}

			String name = userDetails.getFirstname();
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
			logger.info("!!! inside class: ConsultantLoginController, !! method: end of signin return response => ");
			return new ResponseEntity<>(new RestAPIResponse("success", "User logged in successful", response),
					HttpStatus.OK);
		}

	}
	
	@RequestMapping(value = "/working-hours", method = RequestMethod.GET, produces = "application/json")                                                                                                            
    public ResponseEntity<RestAPIResponse> getWorkingHours(@RequestParam Long userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Duration workingHours = activtyService.findUserworkingHours(userId, date);
        long hours = workingHours.toHours();
        long minutes = workingHours.toMinutes() % 60;

        //return ResponseEntity.ok(String.format("User %d worked %d hours and %d minutes on %s", userId, hours, minutes, date));
        return new ResponseEntity<>(new RestAPIResponse("success",String.format("User %d worked %d hours and %d minutes on %s", userId, hours, minutes, date)),
				HttpStatus.OK);
    }
	
	@RequestMapping(value = "/working-hours-between", method = RequestMethod.GET, produces = "application/json")  
    public ResponseEntity<RestAPIResponse> getWorkingHoursBetweenDates(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<LocalDate, Duration> workingHoursMap = activtyService.calculateWorkingHoursBetweenDates(userId, startDate, endDate);

        List<Map<String, Object>> response = new ArrayList<>();
        for (Map.Entry<LocalDate, Duration> entry : workingHoursMap.entrySet()) {
            LocalDate date = entry.getKey();
            Duration duration = entry.getValue();

            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;

            Map<String, Object> dailyRecord = new HashMap<>();
            dailyRecord.put("date", date);
            dailyRecord.put("hours", hours);
            dailyRecord.put("minutes", minutes);

            response.add(dailyRecord);
        }
         return new ResponseEntity<>(new RestAPIResponse("success",response),HttpStatus.OK);
    }
	
	@RequestMapping(value = "/working-hours-all-users", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getWorkingHoursForAllUsers(
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

	    Map<Long, Map.Entry<String, Map<LocalDate, Duration>>> userWorkingHoursMap = 
	            activtyService.calculateWorkingHoursForAllUsers(startDate, endDate);

	    List<Map<String, Object>> response = new ArrayList<>();

	    // Build the response structure for all users
	    for (Map.Entry<Long, Map.Entry<String, Map<LocalDate, Duration>>> userEntry : userWorkingHoursMap.entrySet()) {
	        Long userId = userEntry.getKey();
	        String userName = userEntry.getValue().getKey();
	        Map<LocalDate, Duration> workingHoursForUser = userEntry.getValue().getValue();

	        List<Map<String, Object>> userRecords = new ArrayList<>();
	        for (Map.Entry<LocalDate, Duration> entry : workingHoursForUser.entrySet()) {
	            LocalDate date = entry.getKey();
	            Duration duration = entry.getValue();

	            long hours = duration.toHours();
	            long minutes = duration.toMinutes() % 60;

	            Map<String, Object> dailyRecord = new HashMap<>();
	            dailyRecord.put("date", date);
	            dailyRecord.put("hours", hours);
	            dailyRecord.put("minutes", minutes);

	            userRecords.add(dailyRecord);
	        }
	        UserTracker tracker = new UserTracker();

	        Map<String, Object> userRecord = new HashMap<>();
	        userRecord.put("userId", userId);
	        userRecord.put("userName", userName);
	     //   userRecord.put("IP Address",tracker.getIpAddress());
	        userRecord.put("workingHours", userRecords);

	        response.add(userRecord);
	    }

	    return new ResponseEntity<>(new RestAPIResponse("success", response), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/history/{userId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getLoginHistory(@PathVariable Long userId) {
        List<UserTracker> loginHistory = activtyService.getLoginHistoryByUserId(userId);
        if (loginHistory.isEmpty()) {
            return new ResponseEntity<>("No login history found for the given user ID.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(loginHistory, HttpStatus.OK);
    }
	
	

}
