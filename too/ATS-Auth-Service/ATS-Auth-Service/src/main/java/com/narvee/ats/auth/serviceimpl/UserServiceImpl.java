package com.narvee.ats.auth.serviceimpl;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.narvee.ats.auth.client.EmailClientService;
import com.narvee.ats.auth.dto.EmpHierarchy;
import com.narvee.ats.auth.dto.ExecutiveDTO;
import com.narvee.ats.auth.dto.GetLastLogout;
import com.narvee.ats.auth.dto.GetRecruiter;
import com.narvee.ats.auth.dto.ManagerDTO;
import com.narvee.ats.auth.dto.SortingRequestDTO;
import com.narvee.ats.auth.dto.TeamLeadDTO;
import com.narvee.ats.auth.dto.UpdateProfileRequestDTO;
import com.narvee.ats.auth.dto.UserDTO;
import com.narvee.ats.auth.dto.UserInfoDTO;
import com.narvee.ats.auth.entity.EmployeeDocuments;
import com.narvee.ats.auth.entity.Users;
import com.narvee.ats.auth.repository.EmployeeDocumentRepo;
import com.narvee.ats.auth.repository.ICompanyRepository;
import com.narvee.ats.auth.repository.IUserRepository;
import com.narvee.ats.auth.service.IUserService;
import com.narvee.ats.auth.util.EncryptionUtil;
import com.narvee.ats.auth.util.JwtUtil;

@Service
public class UserServiceImpl implements IUserService {

	public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	@Autowired
	private IUserRepository userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailClientService clientService;

	@Autowired
	private EmployeeDocumentRepo filerepository;

	@Autowired
	private ICompanyRepository companyRepo;

	@Value("${emp.upload-dir}")
	private String filesPath;

	@Override
	public EmployeeDocuments uploadfiles(String files, long id) {
		logger.info("UserServiceImpl.uploadfiles()");

		EmployeeDocuments e = new EmployeeDocuments();
		e.setEducationdoc(files);
		e.setFilename(files);
		e.setUserid(id);
		/// EmployeeDocuments upd = filerepository.save(e);
		return filerepository.save(e);
	}

	@Override
	public List<GetRecruiter> getRecruiter(String token) {
		logger.info("!!! inside class : UserAuthService, !! method : getRecruiter");

		List<Long> cids = JwtUtil.getAssociatedCompanyIds(token);
		return userRepo.getRecruiter(cids);
	}

	@Override
	public List<GetRecruiter> getSalesExecutives(Long userid, Long companyId) {
		logger.info("!!! inside class : UserAuthService, !! method : getSalesExecutives" + userid);
		String roleid = userRepo.getRoleid(userid);
		if (roleid.equalsIgnoreCase("Team Leader Sales")) {
			return userRepo.getBenchSalesTLExecutives(userid, companyId);
		} else if (roleid.equalsIgnoreCase("Sales Manager")) {
			return userRepo.getBenchSalesManagerExecutives(userid, companyId);
		} else if (roleid.equalsIgnoreCase("Super Administrator")) {
			return userRepo.getBenchSuperAdminExecutives(companyId);
		} else {
			return null;
		}
	}

	@Override
	public List<GetRecruiter> getDomExecutives() {
		logger.info("!!! inside class : UserAuthService, !! method : getDomExecutives");
		return userRepo.getDomExecutives();
	}

	@Override
	public Users saveUser(Users users, String token) {
		logger.info("!!! inside class : UserAuthService, !! method : saveUser");

		if (users.getCompanyid() == null) {
			users.setCompanyid(userRepo.findById(users.getAdded()).get().getCompanyid());
		}

		Users user = userRepo.findByEmail(users.getEmail());
		if (user == null) {

			// String pswd=PasswordGenerator.generateRandomPassword(8);
			// users.setPassword(passwordEncoder.encode(pswd));
			// users.setPassword("Narvee123$");
			users.setPassword(passwordEncoder.encode("Narvee123$"));

			if (users.getRole().getRoleid() == 3 || users.getRole().getRoleid() == 4
					|| users.getRole().getRoleid() == 19) {
				users.setIsmanager(true);
			}
			if (users.getRole().getRoleid() == 5 || users.getRole().getRoleid() == 6) {
				users.setIsteamlead(true);
			}
			Users entity = userRepo.save(users);
			String username = users.getFullname();
			if (users.getDepartment().equalsIgnoreCase("Bench Sales")) {
				username = users.getPseudoname();
			}

			try {
				clientService.employeeRegistarionMail(users.getEmail(), username, token);
			} catch (Exception e) {
				return entity;
			}
			return entity;
		} else {
			return null;
		}
	}

	@Override
	public List<Object[]> managerDropDown() {
		logger.info("!!! inside class : UserServiceImpl, !! method : managerDropDown");
		return userRepo.managerDropdown();
	}

	@Override
	public List<UserDTO> AllmanagerDropDown(String companyId) {
		logger.info("!!! inside class : UserServiceImpl, !! method : managerDropDown");

		Long id = null;

		try {
			id = EncryptionUtil.decrypt(companyId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userRepo.AllmanagerDropdown(id);
	}

	@Override
	public ManagerDTO getAllUsersByManagerId(Long managerId) {
		logger.info("!!! inside class : UserServiceImpl, !! method : managerDropDown");

		List<UserDTO> flatList = userRepo.getAllUsersByManagerId(managerId);
		ManagerDTO manager = new ManagerDTO(managerId);

		// Map to track TLs by their ID
		Map<Long, TeamLeadDTO> tlMap = new HashMap<>();

		// First pass: process Team Leaders
		for (UserDTO user : flatList) {
			if (user == null || user.getRole() == null)
				continue;

			String role = user.getRole();

			if ("Team Leader Sales".equalsIgnoreCase(role) || "Team Leader Recruiting".equalsIgnoreCase(role)) {
				TeamLeadDTO tl = new TeamLeadDTO(user.getUserid(), user.getFullname(), user.getPseudoname(), role);
				tlMap.put(user.getUserid(), tl);
				manager.getTeamLeads().add(tl);
			}
		}

		// Second pass: process Executives
		for (UserDTO user : flatList) {
			if (user == null || user.getRole() == null)
				continue;

			String role = user.getRole();

			if ("Sales Executive".equalsIgnoreCase(role) || "Recruiter".equalsIgnoreCase(role)) {
				Long tlId = user.getTeamlead();
				ExecutiveDTO exec = new ExecutiveDTO(user.getUserid(), user.getFullname(), user.getPseudoname(), role);

				if (tlId != null && tlMap.containsKey(tlId)) {
					tlMap.get(tlId).getExecutives().add(exec);
				} else {
					manager.getDirectExecutives().add(exec);
				}
			}
		}

		return manager;
	}

	@Override
	public List<Object[]> TLDropDown(long id) {
		logger.info("!!! inside class : UserServiceImpl, !! method : TLDropDown");
		return userRepo.TlDropdown(id);
	}

	@Override
	public List<Users> getAllUsers(String status, List<Long> companyIds) {
		logger.info("UserServiceImpl.getAllUsers()");

		List<Users> users = userRepo.findUsersByCompany(companyIds);

		if (status.equalsIgnoreCase("all")) {
			return users.stream().filter(u -> !u.getDepartment().equalsIgnoreCase("Consultant"))
					.collect(Collectors.toList());
			
		} else if (status.equalsIgnoreCase("Active") || status.equalsIgnoreCase("Inactive")) {
			// Filter by user status
			return users.stream().filter(
					u -> u.getStatus().equalsIgnoreCase(status) && !u.getDepartment().equalsIgnoreCase("Consultant"))
					.collect(Collectors.toList());
		} else {
			// Assume other values might be department names
			return users.stream().filter(u -> u.getDepartment() != null && u.getDepartment().equalsIgnoreCase(status))
					.collect(Collectors.toList());
		}
	}

	@Override
	public Users finduserById(Long id) {
		logger.info("UserServiceImpl.finduserById()");
		return userRepo.findById(id).get();
	}

	@Override
	public Users updateUser(Users users) {
		if (users.getCompanyid() == null) {
			users.setCompanyid(userRepo.findById(users.getAdded()).get().getCompanyid());
		}

		Users user = userRepo.findByEmailAndUseridNot(users.getEmail(), users.getUserid());
		if (user == null) {
			if (users.getRole().getRoleid() == 3 || users.getRole().getRoleid() == 4
					|| users.getRole().getRoleid() == 18 || users.getRole().getRoleid() == 19
					|| users.getRole().getRoleid() == 24) {
				users.setIsmanager(true);
				users.setIsteamlead(false);
				users.setManager(0);
				users.setTeamlead(0);
			}
			if (users.getRole().getRoleid() == 5 || users.getRole().getRoleid() == 6) {
				users.setIsteamlead(true);
				users.setIsmanager(false);
				users.setTeamlead(0);
			}

			return userRepo.save(users);

		} else {
			return null;
		}
	}

	@Override
	public boolean deleteUsers(Long id) {
		logger.info("UserServiceImpl.deleteUsers()");
		userRepo.deleteById(id);
		return true;
	}

	@Override
	public int changeStatus(String status, Long id, String rem) {
		logger.info("UserServiceImpl.changeStatus()");
		return userRepo.toggleStatus(status, id, rem);
	}

	@Override
	public List<UserInfoDTO> finduserInfoById(Long id) {
		return userRepo.getUserInfo(id);
	}

	@Override
	public String[] allUsitStaff() {
		logger.info("UserServiceImpl.allUsitStaff()");
		return userRepo.allUsitStaff();
	}

	@Override
	public GetLastLogout getLastLogout(Long userid) {
		logger.info("UserServiceImpl.getLastLogout()");
		return userRepo.getLastLogout(userid);
	}

	@Override
	public GetLastLogout getLastLogin(Long userid) {
		logger.info("UserServiceImpl.getLastLogin()");
		return userRepo.getLastLogin(userid);
	}

	@Override
	public List<UserInfoDTO> getTaskAssinedUsersAndCreatedBy(long userid, long[] auserid) {
		logger.info("UserServiceImpl.getTaskAssinedUsersAndCreatedBy()");
		return userRepo.getTaskAssinedUsersAndCreatedBy(userid, auserid);
	}

	@Override
	public Resource downloadfile(long id, String doctype) throws FileNotFoundException {
		logger.info("!!! inside class: UserServiceImpl, method : downloadfile");
		Users model = userRepo.findById(id).orElseThrow(() -> new FileNotFoundException("File does not exist" + id));

		String filename = null;
		if (doctype.equalsIgnoreCase("pan"))
			filename = model.getPan();
		else if (doctype.equalsIgnoreCase("aadhar"))
			filename = model.getAadhar();
		else if (doctype.equalsIgnoreCase("resume")) {
			filename = model.getResume();
		} else {
			filename = model.getBpassbook();
		}

		// String path = filesPath+"/"+model.getConsultantname();
		try {
			Path file = Paths.get(filesPath).resolve(filename);
			logger.info(" Path ", file);
			Resource resource = new UrlResource(file.toUri());
			logger.info(" resource location ", resource);
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read the file!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

	// multiple files
	@Override
	public Resource download(long id) throws FileNotFoundException {
		logger.info("!!! inside class: UserServiceImpl, method : download");
		EmployeeDocuments model = filerepository.findById(id)
				.orElseThrow(() -> new FileNotFoundException("File does not exist" + id));
		String filename = model.getFilename();
		try {
			Path file = Paths.get(filesPath).resolve(filename);
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read the file!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}

	}

	// single file
	@Override
	public int removeFile(long id, String type) {
		logger.info("!!! inside class: UserServiceImpl, method : removeFile");
		Users entity = userRepo.findById(id).get();
		if (type.equalsIgnoreCase("resume")) {
			entity.setResume(null);
			userRepo.save(entity);

		} else if (type.equalsIgnoreCase("aadhar")) {
			entity.setAadhar(null);
			userRepo.save(entity);
		} else if (type.equalsIgnoreCase("pan")) {
			entity.setPan(null);
			userRepo.save(entity);
		} else {
			entity.setBpassbook(null);
			userRepo.save(entity);
		}
		logger.info("!!! inside class: UserServiceImpl, method : removeFile method ending");

		return 1;
	}

//multiple files
	@Override
	public int removeMultipleFile(long id) {
		logger.info("!!! inside class: UserServiceImpl, method : removeMultipleFile");
		Optional<EmployeeDocuments> entity = filerepository.findById(id);

		if (entity.isPresent()) {
			filerepository.deleteById(id);
			return 1;

		} else {
			return 0;
		}

	}

	@Override
	public List<EmpHierarchy> getempHierarchy() {
		List<EmpHierarchy> getempHierarchy = userRepo.getempHierarchy();

		return getempHierarchy;
	}

	@Override
	public int unlockUser(Users users) {
//		try {
//			newemailService.unlockUserMail(users);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (MessagingException e) {
//			e.printStackTrace();
//		}

		Users lockedUser = userRepo.findById(users.getUserid()).get();
		lockedUser.setLocked(false);
		lockedUser.setRemarks(users.getRemarks());
		LocalDateTime logoutTime = LocalDateTime.now();
		lockedUser.setLastLogin(logoutTime);
		// lockedUser.setLastLogout(logoutTime);
		userRepo.save(lockedUser);
		return 0;
	}

	@Override
	public String duplicateCheckWithEmail(String email, Long cid) {
		String user = userRepo.duplicateCheckWithEmail(email, cid);
		if (user == null) {
			return "success";
		}
		return "fail";
	}

	@Override
	public Page<UserInfoDTO> getAllUsersWithSorting(SortingRequestDTO sortingRequestDTO, String token) {

		List<Long> companies = JwtUtil.getAssociatedCompanyIds(token);

		String sortField = sortingRequestDTO.getSortField();
		String sortOrder = sortingRequestDTO.getSortOrder();
		Integer pageNo = sortingRequestDTO.getPageNumber();
		Integer pageSize = sortingRequestDTO.getPageSize();
		String status = sortingRequestDTO.getStatus();

		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

		if (sortField.equalsIgnoreCase("Name"))
			sortField = "fullname";
		else if (sortField.equalsIgnoreCase("Email"))
			sortField = "email";
		else if (sortField.equalsIgnoreCase("Personalnumber"))
			sortField = "personalcontactnumber";
		else if (sortField.equalsIgnoreCase("Designation"))
			sortField = "designation";
		else if (sortField.equalsIgnoreCase("Department"))
			sortField = "department";
		else if (sortField.equalsIgnoreCase("Status"))
			sortField = "status";
		else if (sortField.equalsIgnoreCase("createddate"))
			sortField = "createddate";
		if (sortingRequestDTO.getKeyword().equalsIgnoreCase("empty")) {
			logger.info("Exited from service class getAllUsersWithSorting  Method");

			if (status.equalsIgnoreCase("active")) {
				return userRepo.getAllUsersWithSortingActive(pageable, sortField, sortOrder, status, companies);
			} else {
				return userRepo.getAllUsersWithSortingInactive(pageable, sortField, sortOrder, status, companies);
			}

		} else {
			logger.info("Exited from service class getAllUsersWithSorting Method");
			return userRepo.getAllUsersFilterWithSorting(status, pageable, sortingRequestDTO.getKeyword(), sortField,
					sortOrder);
		}
	}

	@Override
	public boolean updateUserprofile(UpdateProfileRequestDTO updateProfileRequestDTO) {
		updateProfileRequestDTO.getUserid();
		updateProfileRequestDTO.getFullname();
		updateProfileRequestDTO.getEmail();
		updateProfileRequestDTO.getAlternatenumber();
		updateProfileRequestDTO.getCompanycontactnumber();
		updateProfileRequestDTO.getCompanycontactnumber();
		updateProfileRequestDTO.getDesignation();
		userRepo.updateUserProfile(updateProfileRequestDTO.fullname, updateProfileRequestDTO.email,
				updateProfileRequestDTO.personalcontactnumber, updateProfileRequestDTO.companycontactnumber,
				updateProfileRequestDTO.designation, updateProfileRequestDTO.alternatenumber,
				updateProfileRequestDTO.userid);
		return false;
	}

	public Object[] getDepartmentWiseUsers(String department, String companyId) {
		Long id = null;

		try {
			id = EncryptionUtil.decrypt(companyId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Object[] users = userRepo.findDepartmentWiseActiveUsers(department, id);

		return users;
	}

	@Override
	public List<UserDTO> getAllManagers() {
		logger.info("Fetching all active managers., method : getAllManagers");
		List<UserDTO> managers = userRepo.findAllManagers();
		logger.info("Retrieved {} active managers.", managers.size());
		return managers;
	}

	@Override
	public List<UserDTO> getTeamLeadsByManager(Long managerId) {
		logger.info("Fetching team leads under manager with ID: {}", managerId);

		// Fetch the department of the manager
		String department = userRepo.findDepartmentByManagerId(managerId);

		if (department != null) {
			logger.info("Manager ID: {} belongs to department: {}", managerId, department);
		} else {
			logger.warn("Manager ID: {} not found or has no department assigned.", managerId);
		}

		List<UserDTO> teamLeads = userRepo.findTeamLeadsByManager(managerId);
		logger.info("Retrieved {} team leads under manager ID: {}", teamLeads.size(), managerId);
		return teamLeads;
	}

	@Override
	public List<UserDTO> getExecutivesByTeamLead(Long teamLeadId) {
		logger.info("Fetching executives under team lead with ID: {}", teamLeadId);

		List<UserDTO> executives = userRepo.findExecutiveByTeamLead(teamLeadId);

		logger.info("Retrieved {} executives under team lead ID: {}", executives.size(), teamLeadId);
		return executives;
	}

	@Override
	public boolean getValidDateCompanyGiven(String companyid) {

		boolean otherCompany = false;
		try {

			logger.info("!! getValidDateCompanyGiven !! userServiceImpl !! ");
			Long compyid = EncryptionUtil.decrypt(companyid);
			String companyName = companyRepo.findById(compyid).get().getCompanyname();

			if (companyName.equalsIgnoreCase("Narvee Tech") || companyName.equalsIgnoreCase("Singularanalysts")) {
				return otherCompany = true;
			} else {
				return otherCompany = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return otherCompany;
	}

}