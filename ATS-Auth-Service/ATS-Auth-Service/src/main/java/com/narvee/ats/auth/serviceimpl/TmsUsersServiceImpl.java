package com.narvee.ats.auth.serviceimpl;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.narvee.ats.auth.client.EmailClientService;
import com.narvee.ats.auth.dto.ResetPassword;
import com.narvee.ats.auth.entity.OTP;
import com.narvee.ats.auth.entity.TmsRoles;
import com.narvee.ats.auth.entity.TmsUsers;
import com.narvee.ats.auth.repository.OtpRepository;
import com.narvee.ats.auth.repository.TmsRolesRepository;
import com.narvee.ats.auth.repository.TmsUsersRepo;
import com.narvee.ats.auth.service.EmailLoginService;
import com.narvee.ats.auth.service.TmsUsersService;
import com.narvee.ats.auth.tms.dto.AllTmsUsers;
import com.narvee.ats.auth.tms.dto.GetAllUserRequestDTO;
import com.narvee.ats.auth.tms.dto.TmsUsersDropDown;
import com.narvee.ats.auth.tms.dto.TmsUsersInfo;
import com.narvee.ats.auth.util.OTPGenerator;
import com.narvee.ats.auth.util.TmsRolesInitializer;

@Service
public class TmsUsersServiceImpl implements TmsUsersService {

	private static final Logger logger = LoggerFactory.getLogger(TmsUsersServiceImpl.class);

	@Autowired
	private TmsUsersRepo tmsUsersRepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	public OtpRepository otpRepo;

	@Autowired
	public TmsRolesRepository rolesRepository;

	@Autowired
	public EmailClientService emailClientService;

	@Autowired
	private EmailLoginService emailLoginService;

	@Override
	public TmsUsersInfo saveUsers(TmsUsersInfo info) {
		logger.info("!!! inside class: TmsUsersServiceImpl, !! method: saveUsers");
		// TmsUsers users= mapper.map(info, TmsUsers.class);
		TmsUsers users = mapper.map(info, TmsUsers.class);
		
		String UserName = toTitleCase(info.getFirstName()).replaceFirst("^\\s+", "");
		users.setFirstName(UserName);
		users.setMiddleName(toTitleCase(info.getMiddleName()));
		users.setLastName(toTitleCase(info.getLastName()));
		
		if (info.getAdminId() == null && info.getAddedBy() == null) {
			logger.info("!!! inside class: TmsUsersServiceImpl, !! method: Admin Creation");
			
			Optional<TmsUsers> existingUser = tmsUsersRepo.findByEmail(info.getEmail());
			if (existingUser.isPresent()) {
				throw new IllegalArgumentException("User with this Email  already exists");
			}

			users.setIsSuperAdmin(true);
		//	users.setPassword(encoder.encode(info.getPassword()));
			users.setPassword(encoder.encode("Tms@123$"));
			 users = tmsUsersRepo.save(users);
			List<TmsRoles> roles = TmsRolesInitializer.createDefaultRoles(users.getUserId(), users.getUserId());
			rolesRepository.saveAll(roles);

			TmsRoles role = rolesRepository.findByRolenameAndAdminId("Super Admin", users.getUserId());
			users.setRole(role);
			users = tmsUsersRepo.save(users);
		} else if (info.getUpdatedBy() == null ) {
			logger.info("!!! inside class: TmsUsersServiceImpl, !! method: Team Member");
			Optional<TmsUsers> existingUser = tmsUsersRepo.findByEmail(info.getEmail());
			if (existingUser.isPresent()) {
				throw new IllegalArgumentException("User with this Email already exists");
			}
			
		TmsUsers adminInfo  =  tmsUsersRepo.findById(info.getAdminId()).get();
         
			TmsRoles role = new TmsRoles();
			role.setRoleid(info.getRoleId());
			users.setRole(role);
			users.setIsSuperAdmin(false);
			users.setCompanyDomain(adminInfo.getCompanyDomain());
			users.setCompanySize(adminInfo.getCompanySize());
			users.setIndustry(adminInfo.getIndustry());
			users.setOrganisationName(adminInfo.getOrganisationName());
			//	String rawPassword = generateRandomPassword(10);
			users.setPassword(encoder.encode("Tms@123$"));
			try {
				emailLoginService.ChangePasswordforTmsLogin(users.getEmail());
			} catch (Exception e) {
				logger.error("Email service failed", e);
				throw new IllegalArgumentException("Email service not available");
			}
			tmsUsersRepo.save(users);
		} else {
			tmsUsersRepo.save(users);
		}

		TmsUsersInfo usersinfo = mapper.map(users, TmsUsersInfo.class);
		return usersinfo;
	}

	public String generateRandomPassword(int length) {
		String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$!";
		SecureRandom random = new SecureRandom();
		StringBuilder password = new StringBuilder();

		for (int i = 0; i < length; i++) {
			int randIndex = random.nextInt(charSet.length());
			password.append(charSet.charAt(randIndex));
		}

		return password.toString();
	}

	@Override
	public TmsUsers findByEmail(String email) {
		Optional<TmsUsers> existingUser = tmsUsersRepo.findByEmail(email);
		return existingUser.orElse(null);
	}

	@Override
	public OTP emailVerification(String email) {
		logger.info("!!! inside class: TmsUsersServiceImpl , !! method: emailVerification");
		Optional<TmsUsers> duplicateCheck = tmsUsersRepo.findByEmail(email);
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
	public boolean isOTPValid(Long id, String enteredOTP) {
		logger.info("!!! inside class : TmsUsersServiceImpl, !! method : isOTPValid");
		Optional<OTP> otpDataOptional = otpRepo.findById(id);

		if (otpDataOptional.isPresent()) {
			OTP otpData = otpDataOptional.get();
			if (System.currentTimeMillis() <= otpData.getExpirationTime() && enteredOTP.equals(otpData.getOtp())) {
				logger.info("!!! inside class : TmsUsersServiceImpl, !! method : isOTPValid =>Valid OTP");
				return true;
			}
//            else {
//            	logger.info("!!! inside class : LoginServiceImpl, !! method : isOTPValid =>Invalid OTP" );	
//            	otpRepository.delete(otpData);
//            }
		}
		return false;
	}

	@Override
	@Modifying
	public void updatePassword(TmsUsers user, String newPsw) {
		logger.info("TmsUsersServiceImpl.updatePassword()");
		user.getEmail();
		tmsUsersRepo.save(user);
	}

//-------------------------------------------------------------------getting tms users data dropdown ---------------------------------
	@Override
	public List<TmsUsersDropDown> getTmsUsersDropDown(Long adminId) {
		logger.info("!!! inside class : TmsUsersServiceImpl, !! method : getTmsUsersDropDown");
		List<Object[]> result = tmsUsersRepo.getUsersDropDown(adminId);

		return result.stream()
	            .map(obj -> {
	                Long userId = obj[0] != null ? Long.parseLong(obj[0].toString()) : null;
	                String rawName = obj[1] != null ? obj[1].toString() : null;
	                String camelCaseName = toTitleCase(rawName);
	                return new TmsUsersDropDown(userId, camelCaseName);
	            })
	            .collect(Collectors.toList());
	}

	@Override
	public Page<AllTmsUsers> getAllUsersByAdmin(GetAllUserRequestDTO allUserRequestDTO) {
		logger.info("!!! inside class : TmsUsersServiceImpl, !! method : getAllUsersByAdmin");

		Long AdminId = allUserRequestDTO.getAdminId();
		Long userId = allUserRequestDTO.getProfileId();
		//System.err.println("AdminId :"+AdminId +"  userId :"+ userId);
		int pageNo = allUserRequestDTO.getPageNumber();
		int pageSize = allUserRequestDTO.getPageSize();
		String keyword = allUserRequestDTO.getKeyword();
		String sortField = allUserRequestDTO.getSortField();
		String sortOrder = allUserRequestDTO.getSortOrder();

		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

		if (sortField.equalsIgnoreCase("userName"))
			sortField = "userFullName";
		else if (sortField.equalsIgnoreCase("adminFirstName"))
			sortField = "adminFirstName";
		else if (sortField.equalsIgnoreCase("addedByFirstName"))
			sortField = "addedByFirstName";
		else if (sortField.equalsIgnoreCase("email"))
			sortField = "email";
		else if (sortField.equalsIgnoreCase("roleName"))
			sortField = "roleName";
		else if (sortField.equalsIgnoreCase("Position"))
			sortField = "position";
		else if (sortField.equalsIgnoreCase("createddate"))
			sortField = "createddate";
		else
			sortField = "updateddate";

		Sort.Direction sortDirection = Sort.Direction.ASC;
		if (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortField);
		pageable = PageRequest.of(pageNo - 1, pageSize, sort);

		String userRole = tmsUsersRepo.getUserRole(userId);
   
		if ( "Admin".equalsIgnoreCase(userRole) ||"Project Manager".equalsIgnoreCase(userRole) || "Super Admin".equalsIgnoreCase(userRole)) {
			 Long adminId = ("Super Admin".equalsIgnoreCase(userRole))
		                ? userId
		                : tmsUsersRepo.AdminId(userId);
			
			if ("empty".equalsIgnoreCase(keyword)) {
				logger.info("!!! inside class : TmsUsersServiceImpl, !! method : getAllUsersByAdmin");
				return tmsUsersRepo.getAllUsersByAdmin(adminId, pageable);
			} else {
				logger.info("!!! inside class : TmsUsersServiceImpl, !! method : getAllUsersByAdminwithSearching");
				return tmsUsersRepo.getAllUsersByAdminwithSearching(adminId,pageable, keyword);
			}
		} else {
			if ("empty".equalsIgnoreCase(keyword)) {
				logger.info("!!! inside class : TmsUsersServiceImpl, !! method : getAllUsersByAddedBy");
				return tmsUsersRepo.getAllUsersByAddedBy(userId, pageable);
			} else {
				logger.info("!!! inside class : TmsUsersServiceImpl, !! method : getAllUsersByAddedBywithSearching");
				return tmsUsersRepo.getAllUsersByAddedBywithSearching(userId, pageable, keyword);
			}
		}
	}

	@Override
	public void deleteTeamMember(Long TeamMemberId) {
		logger.info("!!! inside class : TmsUsersServiceImpl, !! method : deleteTeamMember");
		 Long isAssigned = tmsUsersRepo.isUserAssignedToAnyProject(TeamMemberId);

		    if (isAssigned > 0) {
		        throw new IllegalStateException("Cannot delete team member: Assigned to one or more projects.");
		    }
		tmsUsersRepo.deleteById(TeamMemberId);
	}

	@Override
	public boolean updateTeamMember(TmsUsersInfo tmsUsersInfo) {
		Optional<TmsUsers> user = tmsUsersRepo.findById(tmsUsersInfo.getUserId());
		if (user.isPresent()) {
			TmsUsers tmsUsers = user.get();
			tmsUsers.setFirstName(tmsUsersInfo.getFirstName());
			tmsUsers.setMiddleName(tmsUsersInfo.getMiddleName());
			tmsUsers.setLastName(tmsUsersInfo.getLastName());		
			tmsUsers.setOrganisationEmail(tmsUsersInfo.getOrganisationEmail());
		//	tmsUsers.setEmail(tmsUsersInfo.getEmail());
			tmsUsers.setContactNumber(tmsUsersInfo.getContactNumber());
			tmsUsers.setPosition(tmsUsersInfo.getPosition());
			tmsUsers.setProfilePhoto(tmsUsersInfo.getProfilepic());
			tmsUsers.setUpdatedBy(tmsUsersInfo.getUpdatedBy());
			tmsUsers.setCompanyDomain(tmsUsersInfo.getCompanyDomain());
			tmsUsers.setCompanySize(tmsUsersInfo.getCompanySize());
			tmsUsers.setIndustry(tmsUsersInfo.getIndustry());
			tmsUsers.setAdminId(tmsUsersInfo.getAdminId());
			tmsUsers.setOrganisationName(tmsUsersInfo.getOrganisationName());	
			
			if (!tmsUsers.getEmail().equalsIgnoreCase(tmsUsersInfo.getEmail())) {
			    Optional<TmsUsers> existingUser = tmsUsersRepo.findByEmail(tmsUsersInfo.getEmail());
			    if (existingUser.isPresent()) {
			        throw new IllegalArgumentException("This email already exists");
			    }
			    tmsUsers.setEmail(tmsUsersInfo.getEmail());
			}
			tmsUsers.setEmail(tmsUsersInfo.getEmail());
			if (tmsUsersInfo.getRoleId() != null) {
				Optional<TmsRoles> optionalRole = rolesRepository.findById(tmsUsersInfo.getRoleId());
				optionalRole.ifPresent(tmsUsers::setRole);
			}
			String UserRole = tmsUsersRepo.roleName(tmsUsersInfo.getUserId());
			if (UserRole.equalsIgnoreCase("Super Admin")) {
				tmsUsers.setIsSuperAdmin(true);
				tmsUsers.setAdminId(null);

			}
			// tmsUsers.setUserRole(tmsUsersInfo.getUserRole());
			tmsUsersRepo.save(tmsUsers);
			return true;
		}
		return false;
	}

	@Override
	public TmsUsers findByUserId(Long userid) {
		logger.info("!!! inside class : TmsUsersServiceImpl, !! method : findByUserId");
		return tmsUsersRepo.findById(userid).get();
	}

	@Override
	public void uploadProfilePhoto(Long userId, MultipartFile photo) throws IOException {
		TmsUsers user = tmsUsersRepo.findById(userId)
				.orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

		user.setProfilePhoto(photo.getBytes());
		tmsUsersRepo.save(user);

	}

	@Override
	public void deleteProfilePic(Long id) {
		TmsUsers user = tmsUsersRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("User with ID " + id + " not found"));
		user.setProfilePhoto(null);
		tmsUsersRepo.save(user);
	}

	
	private String toTitleCase(String input) {
	    if (input == null || input.trim().isEmpty()) return input;
	    String[] words = input.trim().toLowerCase().split("\\s+");
	    return Arrays.stream(words)
	                 .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
	                 .collect(Collectors.joining(" "));
	}

}
