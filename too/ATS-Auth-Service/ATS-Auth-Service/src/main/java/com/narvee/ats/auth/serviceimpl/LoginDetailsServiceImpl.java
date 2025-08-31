package com.narvee.ats.auth.serviceimpl;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.narvee.ats.auth.client.ConsultantClientService;
import com.narvee.ats.auth.client.EmailClientService;
import com.narvee.ats.auth.controller.ConsultantLoginController;
import com.narvee.ats.auth.dto.RegistrationRequest;
import com.narvee.ats.auth.dto.ResetPassword;
import com.narvee.ats.auth.entity.LoginDetails;
import com.narvee.ats.auth.entity.OTP;
import com.narvee.ats.auth.entity.Roles;
import com.narvee.ats.auth.entity.Users;
import com.narvee.ats.auth.repository.IUserRepository;
import com.narvee.ats.auth.repository.LoginDetailsRepository;
import com.narvee.ats.auth.repository.OtpRepository;
import com.narvee.ats.auth.service.LoginDetailsService;
import com.narvee.ats.auth.util.OTPGenerator;

@Service
public class LoginDetailsServiceImpl implements LoginDetailsService {
	public static final Logger logger = LoggerFactory.getLogger(ConsultantLoginController.class);

	@Autowired
	public OtpRepository otpRepo;

	@Autowired
	public EmailClientService emailClientService;

	@Autowired
	public IUserRepository iUserRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private LoginDetailsRepository loginDetailsRepository;

	@Autowired
	private ConsultantClientService consultantClientService;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Override
	public OTP emailVerification(String email) {
		logger.info("!!! inside class: ConsultantLoginServiceImpl , !! method: emailVerification");
		Users duplicateCheck = iUserRepository.findByEmail(email);
		OTP otp = new OTP();
		if (duplicateCheck == null) {
			long expirationTime = System.currentTimeMillis() + OTP.OTPEXP;
			String genOtp = OTPGenerator.generateRandomPassword(6);
			otp.setExpirationTime(expirationTime);
			otp.setOtp(genOtp);
			ResetPassword dto = new ResetPassword();
			dto.setOtp(genOtp);
			dto.setEmail(email);
			try {
				emailClientService.emailVerification(dto);
			} catch (Exception e) {
				return null;
			}

			otpRepo.save(otp);
		}
		return otp;
	}

	@Override
	public boolean validateOtp(String otp, Long id) {
		logger.info("!!! inside class: ConsultantLoginServiceImpl , !! method: validateOtp");
		Optional<OTP> otpDataOptional = otpRepo.findById(id);
		if (otpDataOptional.isPresent()) {
			OTP otpData = otpDataOptional.get();
			if (System.currentTimeMillis() <= otpData.getExpirationTime() && otp.equals(otpData.getOtp())) {
				otpRepo.deleteById(id);
				logger.info("!!! inside class : LoginServiceImpl, !! method : isOTPValid =>Valid OTP");
				return true;
			}
		}
		return false;
	}

	@Override
	public Long registerConsultant(RegistrationRequest request) {
		logger.info("!!! Inside class: ConsultantLoginServiceImpl , !! method: registerConsultant");
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
		Users duplicateCheck = iUserRepository.findByEmail(request.getEmail());

		if (duplicateCheck != null) {
			logger.info("User with email {} already exists. Registration skipped.", request.getEmail());
			return null;
		}

		// If email is not in Users, proceed with registration
		Users user = mapper.map(request, Users.class);
		user.setDepartment("Consultant");
		user.setDesignation("Consultant");

		Roles role = new Roles();
		role.setRoleid(20l);
		user.setRole(role);

		user.setPseudoname(request.getFirstname());
		user.setFullname(request.getFirstname() + " " + request.getLastname());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setCompanyid(1l);

		// Save the new user to the Users table
		iUserRepository.save(user);

		// Prepare the RegistrationRequest for consultant info
		request.setConsultantemail(request.getEmail());
		request.setConRegistrationId(user.getUserid());
		request.setAddedby(user.getUserid());

		// Call Feign client to handle consultant registration/update in consultant_info
		// table
		RegistrationRequest response;
		try {
			response = consultantClientService.saveConsultant(request);
			transactionManager.commit(status);
		} catch (Exception e) {
			transactionManager.rollback(status);
			logger.error("Failed to save consultant info via Feign client", e);
			throw new RuntimeException("Consultant registration failed in external service.");
		}
		// response = consultantClientService.saveConsultant(request);

		// Get consultant ID from response
		Long consultantId = response.getConsultantid();
		if (consultantId == null) {
			logger.error("Consultant registration failed: Consultant ID is null.");
			throw new RuntimeException("Consultant registration failed.");
		}
		// Send registration email
		emailClientService.consultantRegistarion(user);

		// Save login details
		LoginDetails loginDetails = new LoginDetails();
		loginDetails.setConsultantName(request.getFirstname() + " " + request.getLastname());
		loginDetails.setEmail(request.getEmail());
		loginDetails.setPassword(request.getPassword());
		loginDetailsRepository.save(loginDetails);

		return consultantId;

	}
}