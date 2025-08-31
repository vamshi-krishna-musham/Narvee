package com.narvee.ats.auth.serviceimpl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.narvee.ats.auth.client.EmailClientService;
import com.narvee.ats.auth.entity.LoginDetails;
import com.narvee.ats.auth.entity.OTP;
import com.narvee.ats.auth.entity.Roles;
import com.narvee.ats.auth.entity.Users;
import com.narvee.ats.auth.repository.IUserRepository;
import com.narvee.ats.auth.repository.LoginDetailsRepository;
import com.narvee.ats.auth.repository.OtpRepository;
import com.narvee.ats.auth.service.ILoginService;

@Service
public class LoginServiceImpl implements ILoginService {

	public static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);
	@Autowired
	private IUserRepository userRepo;
	@Autowired
	private LoginDetailsRepository loginRepository;
	@Autowired
	private OtpRepository otpRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	public EmailClientService emailClientService;

	@Override
	public Users findByEmail(String email) {
		logger.info("LoginServiceImpl.findByEmail()");
		return userRepo.findByEmail(email);
	}

	@Override
	public Users findbyuserid(long userid) {
		logger.info("LoginServiceImpl.findbyuserid()");
		return userRepo.findByUserid(userid);
	}

	@Override
	@Modifying
	public void updatePassword(Users user, String newPsw) {
		logger.info("LoginServiceImpl.updatePassword()");
		user.getEmail();
		userRepo.save(user);
	}

	@Override
	public void updateResetPasswordToken(String token, String email) {
		logger.info("LoginServiceImpl.updateResetPasswordToken()");
		Users customer = userRepo.findByEmail(email);
		if (customer != null) {
			userRepo.save(customer);
		}
	}

//	@Override
//	public boolean isOTPValid(Long id, String enteredOTP) {
//		logger.info("!!! inside class : LoginServiceImpl, !! method : isOTPValid");
//		Optional<OTP> otpDataOptional = otpRepository.findById(id);
//System.err.println("otpDataOptional: "+otpDataOptional);
//		if (otpDataOptional.isPresent()) {
//			OTP otpData = otpDataOptional.get();
//			System.err.println("otpData: "+otpData);
//			if (System.currentTimeMillis() <= otpData.getExpirationTime() && enteredOTP.equals(otpData.getOtp())) {
//				logger.info("!!! inside class : LoginServiceImpl, !! method : isOTPValid =>Valid OTP");
//				return true;
//			}
//
//		}
//		return false;
//	}
	@Override
	public boolean isOTPValid(Long id, String enteredOTP) {
		logger.info("!!! inside class : LoginServiceImpl, !! method : isOTPValid");

		Optional<OTP> otpDataOptional = otpRepository.findById(id);
		if (otpDataOptional.isPresent()) {
			OTP otpData = otpDataOptional.get();
			System.err.println("otpData: " + otpData);

			if (System.currentTimeMillis() > otpData.getExpirationTime()) {
				otpRepository.deleteById(id);
				logger.info("Expired OTP deleted for ID: {}", id);
				return false;
			}

			if (enteredOTP.equals(otpData.getOtp())) {
				otpRepository.deleteById(id);
				logger.info("Validated OTP deleted for ID: {}", id);
				return true;
			}
		}

		return false;
	}

	@Override
	public Users registerNewUser(Users user) {
		logger.info("!!! Inside class: LoginServiceImpl , !! method: registerNewUser");
		Users duplicateCheck = userRepo.findByEmail(user.getEmail());
		if (duplicateCheck == null) {
			user.setDepartment("EMP");
			user.setDesignation("EMP");
			user.setPseudoname(user.getFirstname());
			String fullname = user.getFirstname() + " " + user.getLastname();
			user.setFullname(fullname);
			Roles role = new Roles();
			role.setRoleid(19l);
			user.setRole(role);
			LoginDetails loginDetails = new LoginDetails();
			String username = user.getFirstname() + " " + user.getLastname();
			loginDetails.setConsultantName(username);
			loginDetails.setEmail(user.getEmail());
			loginDetails.setPassword(user.getPassword());
//		String pswd = PasswordGenerator.generateRandomPassword(8);
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			emailClientService.consultantRegistarion(user);
			userRepo.save(user);
			loginRepository.save(loginDetails);
//		public void consultantRegistrationMail(String password, String email, String username)
		}
		return user;
	}

	
}
