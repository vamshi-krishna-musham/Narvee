package com.narvee.ats.auth.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.narvee.ats.auth.commons.RestAPIResponse;
import com.narvee.ats.auth.dto.EmpHierarchy;
import com.narvee.ats.auth.dto.GetRecruiter;
import com.narvee.ats.auth.dto.ManagerDTO;
import com.narvee.ats.auth.dto.SortingRequestDTO;
import com.narvee.ats.auth.dto.UpdateProfileRequestDTO;
import com.narvee.ats.auth.dto.UserDTO;
import com.narvee.ats.auth.dto.UserInfoDTO;
import com.narvee.ats.auth.entity.Users;
import com.narvee.ats.auth.service.IUserService;
import com.narvee.ats.auth.service.IfileStorageService;
import com.narvee.ats.auth.util.EncryptionUtil;
import com.narvee.ats.auth.util.JwtUtil;

@RestController
@CrossOrigin
@RequestMapping(value = "/users")
public class UserController {
	public static final Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private IUserService iUserService;

	@Autowired
	private IfileStorageService fileStorageService;

	// manager tl drop down
	@RequestMapping(value = "/manageDropDown", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> managerDropdown() {
		logger.info("UsersController.managerDropdown()");
		List<Object[]> getrole = iUserService.managerDropDown();
		return new ResponseEntity<>(new RestAPIResponse("success", " Fetched Managers Dropdown", getrole), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/AllmanageDropDown/{companyId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> AllmanagerDropdown(@PathVariable String  companyId) {
		logger.info("UsersController.AllmanagerDropdown()");
		List<UserDTO> getrole = iUserService.AllmanagerDropDown(companyId);
		return new ResponseEntity<>(new RestAPIResponse("success", " Fetched  All Managers Dropdown", getrole), HttpStatus.OK);
	}

	
	@RequestMapping(value = "/getAllUserByManagerId/{ManagerId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getAlluserByManagerId(@PathVariable Long ManagerId) {
		logger.info("UsersController.getAllUserByManagerId()");
		ManagerDTO getrole = iUserService.getAllUsersByManagerId(ManagerId);
		return new ResponseEntity<>(new RestAPIResponse("success", " Fetched  All Users By Manager Id ", getrole), HttpStatus.OK);
	}

	// manager tl drop down
	@RequestMapping(value = "/TlDropDown/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> TLDropdown(@PathVariable long id) {
		logger.info("UsersController.getAllRoles()");
		List<Object[]> getrole = iUserService.TLDropDown(id);
		return new ResponseEntity<>(new RestAPIResponse("success", "All Roles Fetched", getrole), HttpStatus.OK);
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> addUsers(@RequestBody Users users,
			@RequestHeader("Authorization") String token) throws Exception {
		logger.info("UsersController.addUsers()");
		users.setCompanyid(EncryptionUtil.checkCidType(users.getCid()));
		Users saveUser = iUserService.saveUser(users, token);
		if (saveUser != null) {
			logger.info("UsersController.addUsers() saving user");
			return new ResponseEntity<>(new RestAPIResponse("success", "User Saved", saveUser), HttpStatus.CREATED);
		} else {
			logger.info("UsersController.addUsers() => user not saved Already exists");
			return new ResponseEntity<>(new RestAPIResponse("Fail", "User Already Exist", saveUser), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/all/{status}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getUsers(@PathVariable String status, @RequestHeader("AUTHORIZATION") String token) {
		logger.info("UsersController.getUsers()");
		
		List<Long> acid=JwtUtil.getAssociatedCompanyIds(token);
		
		
		List<Users> listall = iUserService.getAllUsers(status, acid);
//		Comparator<Users> comparator = (c1, c2) -> {
//			return (c1.getFullname()).compareTo(c2.getFullname());
//		};
//		Collections.sort(listall, comparator);
		return new ResponseEntity<>(new RestAPIResponse("success", "User fetched", listall), HttpStatus.OK);
	}

	// to check authorization
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<RestAPIResponse> deleteUser(@PathVariable Long id) {
		logger.info("UsersController.deleteUser()");
		return new ResponseEntity<>(new RestAPIResponse("success", "User Deleted", iUserService.deleteUsers(id)),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/status", method = RequestMethod.PATCH, produces = "application/json")
	public ResponseEntity<RestAPIResponse> changeStatus(@RequestBody Users users) {
		logger.info("UsersController.changeStatus()");
		Long id = users.getUserid();
		String status = users.getStatus();
		String remarks = users.getRemarks();

		int changestat = 0;
		String result;
		if (status.equals("Active"))
			result = "InActive";
		else
			result = "Active";
		changestat = iUserService.changeStatus(result, id, remarks);
		if (changestat != 0) {
			logger.info("UsersController.changeStatus() status changed Successfully");
		} else {
			logger.info("UsersController.changeStatus() status not changed ");
		}
		return new ResponseEntity<>(new RestAPIResponse("success", "Status Change Successfully"), HttpStatus.OK);
	}

	@RequestMapping(value = "/userbyid/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getUserByid(@PathVariable Long id) throws Exception {
		logger.info("UsersController.getUserByid()");
		Users user=iUserService.finduserById(id);
		user.setCid(EncryptionUtil.encrypt(user.getCompanyid()));
		
		return new ResponseEntity<>(new RestAPIResponse("success", "User Details", iUserService.finduserById(id)),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces = "application/json")
	public ResponseEntity<RestAPIResponse> updateUsers(@RequestBody Users users) throws Exception {
		logger.info("UsersController.updateUsers()");
		users.setCompanyid(EncryptionUtil.checkCidType(users.getCid()));
		Users flg = iUserService.updateUser(users);
		if (flg != null) {
			logger.info("UsersController.updateUsers() User updated");
			return new ResponseEntity<>(new RestAPIResponse("success", "User Updated", flg), HttpStatus.CREATED);
		} else {
			logger.info("UsersController.updateUsers() User not updated");
			return new ResponseEntity<>(new RestAPIResponse("Fail", "User Already Exist", "User Email Already Exist"),
					HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/userinfo/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getUserInfoByid(@PathVariable Long id) {
		logger.info("UsersController.getUserByid()");
		return new ResponseEntity<>(new RestAPIResponse("success", "User Details", iUserService.finduserInfoById(id)),
				HttpStatus.OK);
	}

	// get uservy Id for MS Intracommunication
	@RequestMapping(value = "/userDetailsById/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Users> getUserDetailsByByid(@PathVariable Long id) {
		logger.info("UsersController.getUserByid()");
		return new ResponseEntity<>(iUserService.finduserById(id), HttpStatus.OK);
	}

	@RequestMapping(value = "/getStaffData", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<String[]> getallStaffData() {
		logger.info("UsersController.getUserByid()");
		return new ResponseEntity<>(iUserService.allUsitStaff(), HttpStatus.OK);
	}

	@RequestMapping(value = "/recruiterlist", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<GetRecruiter>> listrecruiter(@RequestHeader("AUTHORIZATION") String token) {
		logger.info("!!! inside class : UsersController, !! method : listrecruiter");
		
		
		return new ResponseEntity<>(iUserService.getRecruiter(token), HttpStatus.OK);
	}

	@RequestMapping(value = "/salesExecutivesList/{userid}/{companyId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<GetRecruiter>> listSalesExecutives(@PathVariable Long userid,
			@PathVariable Long companyId) {
		logger.info("!!! inside class : UsersController, !! method : listSalesExecutives");
		return new ResponseEntity<>(iUserService.getSalesExecutives(userid, companyId), HttpStatus.OK);
	}

	@RequestMapping(value = "/domExecutivesList", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<GetRecruiter>> listDomExecutives() {
		logger.info("!!! inside class : UsersController, !! method : listDomExecutives");
		return new ResponseEntity<>(iUserService.getDomExecutives(), HttpStatus.OK);
	}

	@RequestMapping(value = "/recruiterlist2", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getAllRecruiterlist(@RequestHeader("AUTHORIZATION") String token) {
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", "fetch All Requirments", iUserService.getRecruiter(token)), HttpStatus.OK);
	}

	@RequestMapping(value = "/findAssinUsersCreatedBy/{userid}/{auserid}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<UserInfoDTO>> getTaskAssinedUsersAndCreatedBy(@PathVariable Long userid,
			@PathVariable long auserid[]) {
		logger.info("!!! inside class : UsersController, !! method : getTaskAssinedUsersAndCreatedBy");
		return new ResponseEntity<List<UserInfoDTO>>(iUserService.getTaskAssinedUsersAndCreatedBy(userid, auserid),
				HttpStatus.OK);

	}

	@RequestMapping(value = "/uploadMultiple/{id}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<RestAPIResponse> uploadMultipleFile2(
			@RequestParam(required = false, value = "pan") MultipartFile pan,
			@RequestParam(required = false, value = "resume") MultipartFile resume,
			@RequestParam(required = false, value = "aadhar") MultipartFile aadhar,
			@RequestParam(required = false, value = "passbook") MultipartFile passbook, @PathVariable Long id,
			@RequestParam(required = false, value = "files") MultipartFile[] education,
			@RequestHeader("Authorization") String token) throws IOException {

		logger.info("UsersController.uploadMultipleFile2() Kiramn  " + aadhar);

		Users entity = iUserService.finduserById(id);

		String fullname = entity.getFullname();

		try {
			String nresume = fileStorageService.storeEmployeeFile(resume, fullname, "Resume");
			entity.setResume(nresume);
		} catch (NullPointerException e) {
		}

		try {
			String npan = fileStorageService.storeEmployeeFile(pan, fullname, "Pan");
			entity.setPan(npan);
		} catch (NullPointerException e) {
		}

		try {
			String naadhar = fileStorageService.storeEmployeeFile(aadhar, fullname, "Aadhar");
			entity.setAadhar(naadhar);
		} catch (NullPointerException e) {
		}

		try {
			String npassbook = fileStorageService.storeEmployeeFile(passbook, fullname, "Passbook");
			entity.setBpassbook(npassbook);// bpassbook

		} catch (NullPointerException e) {
		}

		try {
			List<String> fileNames = new ArrayList<>();
			if (education.length != 0) {
				Arrays.asList(education).stream().forEach(file -> {
					String filename = fileStorageService.storeEmpmultiplefiles(file, fullname);
					iUserService.uploadfiles(filename, id);
					fileNames.add(filename);
				});
			}
		} catch (Exception e) {
		}

		iUserService.saveUser(entity, token);
		logger.info("UsersController.uploadMultipleFile2() Ending");
		return new ResponseEntity<>(new RestAPIResponse("success", "Uploaded files"), HttpStatus.OK);
	}

	@RequestMapping(value = "/download/{id}/{flg}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Resource> downloadDoc(@PathVariable long id, @PathVariable String flg) throws IOException {
		logger.info("UsersController.downloadDoc()");
		Resource file = iUserService.downloadfile(id, flg);
		Path path = file.getFile().toPath();
		logger.info("!!! inside class: ConsultantController, method : downloadDoc");
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@RequestMapping(value = "/downloadfiles/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Resource> downloadFile(@PathVariable long id) throws IOException {
		logger.info("!!! inside class: ConsultantController, method : downloadFile");
		Resource file = iUserService.download(id);
		Path path = file.getFile().toPath();
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@RequestMapping(value = "/removefile/{id}/{flg}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> removeFile(@PathVariable long id, @PathVariable String flg)
			throws IOException {
		logger.info("!!! inside class: UsersController, method : removeFile");
		iUserService.removeFile(id, flg);
		return new ResponseEntity<>(new RestAPIResponse("success", "File Removed Successfully "), HttpStatus.OK);
	}

	@RequestMapping(value = "/removefiles/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> removemultipleFile(@PathVariable long id) throws IOException {
		logger.info("!!! inside class: UsersController, method : removemultipleFile");
		int count = iUserService.removeMultipleFile(id);
		if (count == 0) {
			return new ResponseEntity<>(new RestAPIResponse("fail", "File Not Removed"), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new RestAPIResponse("success", "File Removed Successfully"), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/emphierarchy", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getempHierarchy() {
		List<EmpHierarchy> getempHierarchy = iUserService.getempHierarchy();
//		Map<String, List<EmpHierarchy>> peopleByAge = getempHierarchy.stream()
//	            .collect(Collectors.groupingBy(e->e.getIsmanager()));
		Map<String, Map<String, List<EmpHierarchy>>> peopleByAge = getempHierarchy.stream()
				.collect(Collectors.groupingBy(e -> e.getManagername() != null ? e.getManagername() : "Manager", // First
																													// level
																													// grouping
																													// by
																													// isManager
						Collectors.groupingBy(e -> e.getTeamleadname() != null ? e.getTeamleadname() : "TeamLead" // Second
																													// level
																													// grouping
																													// by
																													// teamLead
																													// within
																													// each
																													// isManager
																													// group
						)));

//		Map<String, Map<String, Map<String, List<EmpHierarchy>>>> peopleByAge1 = getempHierarchy.stream()
//			    .collect(Collectors.groupingBy(
//			    		e->e.getIsmanager(), // First level grouping by isManager
//			        Collectors.groupingBy(
//			        		e->e.getIsteamlead(), // Second level grouping by teamLead within each isManager group
//			            Collectors.groupingBy(
//			            		e->e.getTeamleadname() // Third level grouping by another criterion within each teamLead group
//			            )
//			        )
//			    ));

		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "fetch All Requirments", peopleByAge),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/unlock", method = RequestMethod.PATCH, produces = "application/json")
	public ResponseEntity<RestAPIResponse> unlockUser(@RequestBody Users users) {
		logger.info("UsersController.changeStatus()");
		String remarks = users.getRemarks();

		int changestat = 0;
		String result;
		changestat = iUserService.unlockUser(users);
		return new ResponseEntity<>(new RestAPIResponse("success", "Status Change Successfully", "Done"),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/user/{department}/{companyId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> departwiseActiveUsers(@PathVariable String department,
			@PathVariable String companyId) {
		logger.info("UsersController.changeStatus()");
		Object[] users = iUserService.getDepartmentWiseUsers(department, companyId);

		return new ResponseEntity<>(new RestAPIResponse("success", "Status Change Successfully", users), HttpStatus.OK);
	}

	@RequestMapping(value = "/validate/{email}/{companyId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> duplicateCheckWithEmail(@PathVariable String email,
			@PathVariable String companyId) throws Exception {
		logger.info("UsersController.duplicateCheckWithEmail()");
		Long cId = EncryptionUtil.decrypt(companyId);
		String status = iUserService.duplicateCheckWithEmail(email, cId);
		if (status.equals("success")) {
			return new ResponseEntity<>(new RestAPIResponse(status, "Email Validated Successfully"), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new RestAPIResponse(status, "Email already exists.."), HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/all", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getUsersWithsorting(@RequestBody SortingRequestDTO sortingRequestDTO, @RequestHeader("AUTHORIZATION") String token) {
		logger.info("UsersController.getUsers()");
		Page<UserInfoDTO> listall = iUserService.getAllUsersWithSorting(sortingRequestDTO, token);
		return new ResponseEntity<>(new RestAPIResponse("success", "User fetched", listall), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateProfile", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> updatedUserProfile(@RequestBody UpdateProfileRequestDTO requestDTO) {
		System.out.println(requestDTO);
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "successfully updated user details",
				iUserService.updateUserprofile(requestDTO)), HttpStatus.OK);
	}

	// Fetch all active managers
	@RequestMapping(value = "/managers", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getAllManagers() {
		logger.info("Received request to fetch all active managers.");
		List<UserDTO> managers = iUserService.getAllManagers();

		if (managers.isEmpty()) {
			logger.warn("No active managers found.");
			return ResponseEntity.ok(new RestAPIResponse("No data found", "No active managers available", null));
		}

		logger.info("Returning {} active managers.", managers.size());
		return ResponseEntity.ok(new RestAPIResponse("success", "Successfully fetched manager data", managers));
	}

	// Fetch all active team leads under a manager
	@RequestMapping(value = "/teamleads/{managerId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getTeamLeadsByManager(@PathVariable Long managerId) {
		logger.info("Received request to fetch team leads under manager ID: {}", managerId);

		List<UserDTO> teamLeads = iUserService.getTeamLeadsByManager(managerId);

		if (teamLeads.isEmpty()) {
			logger.warn("No team leads found under manager ID: {}", managerId);
			return ResponseEntity
					.ok(new RestAPIResponse("No data found", "No team leads available for this manager", null));
		}

		logger.info("Returning {} team leads under manager ID: {}", teamLeads.size(), managerId);
		return ResponseEntity.ok(new RestAPIResponse("success", "Successfully fetched team lead data", teamLeads));
	}

	// Fetch all active executives under a team lead
	@RequestMapping(value = "/executives/{teamLeadId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getExecutivesByTeamLead(@PathVariable Long teamLeadId) {
		logger.info("Received request to fetch executives under team lead ID: {}", teamLeadId);

		List<UserDTO> executives = iUserService.getExecutivesByTeamLead(teamLeadId);

		if (executives.isEmpty()) {
			logger.warn("No executives found under team lead ID: {}", teamLeadId);
			return ResponseEntity
					.ok(new RestAPIResponse("No data found", "No executives available for this team lead", null));
		}

		logger.info("Returning {} executives under team lead ID: {}", executives.size(), teamLeadId);
		return ResponseEntity.ok(new RestAPIResponse("success", "Successfully fetched executive data", executives));
	}

	@RequestMapping(value = "/companyCheck/{companyid}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getValidDateCompanyGiven(@PathVariable String companyid) {

		boolean executives = iUserService.getValidDateCompanyGiven(companyid);

		if (executives) {
			logger.warn("!! id of given is Narveetech or singular !!", companyid);
			return new ResponseEntity<>(new RestAPIResponse("success", "Company not exist", executives), HttpStatus.OK);
		} else {
			logger.warn("!! id of given is not Narveetech or singular !!", companyid);
			return new ResponseEntity<>(new RestAPIResponse("fail", "Company exist", executives), HttpStatus.OK);
		}
	}

}
