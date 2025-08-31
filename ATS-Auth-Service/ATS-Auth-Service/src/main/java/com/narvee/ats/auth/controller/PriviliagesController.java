package com.narvee.ats.auth.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.narvee.ats.auth.commons.RestAPIResponse;
import com.narvee.ats.auth.entity.Privilege;
import com.narvee.ats.auth.request.PrivilegeVO;
import com.narvee.ats.auth.request.RoleToPrivilegesVO;
import com.narvee.ats.auth.service.IPrivilegeService;

@RestController
@RequestMapping("/priviliges")
@CrossOrigin
public class PriviliagesController {
	private static final Logger logger = LoggerFactory.getLogger(PriviliagesController.class);

	@Autowired
	private IPrivilegeService privilegeService;

	@RequestMapping(value = "/getPrivileges", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getAllRoles() {
		logger.info("!!! inside class: PriviliagesController , !! method: getAllRoles");
		PrivilegeVO privileges = privilegeService.getAllPrivileges();
		return new ResponseEntity<>(new RestAPIResponse("Success", "All Roles Fetched", privileges), HttpStatus.OK);
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> listAllPrev() {
		logger.info("!!! inside class: PriviliagesController , !! method: listAllPrev");
		List<Privilege> privileges = privilegeService.allprev();
		return new ResponseEntity<>(new RestAPIResponse("Success", "All Roles Fetched", privileges), HttpStatus.OK);
	}

	@RequestMapping(value = "/savePrevileges", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> savePrevg(@RequestBody Privilege previleges) {
		logger.info("!!! inside class: PriviliagesController , !! method: savePrevg");
		// System.out.println(previleges);
		privilegeService.savePrevileges(previleges);
		return new ResponseEntity<>(new RestAPIResponse("Success", "Previleges Saved"), HttpStatus.CREATED);
	}

	@RequestMapping(value = "/addprevtorole", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> addprevtorole(@RequestBody RoleToPrivilegesVO previleges) throws Exception {

		logger.info("!!! inside class: PriviliagesController , !! method: addprevtorole");
		privilegeService.addPrivilegeToRole(previleges);
		return new ResponseEntity<>(new RestAPIResponse("Success", "Previleges Saved"), HttpStatus.CREATED);
	}

//	@RequestMapping(value = "/addprevtoroles", method = RequestMethod.POST, produces = "application/json")
//	public ResponseEntity<RestAPIResponse> addprevtoroles(@RequestBody IndividualCompanyPrivilegesDto previleges) {
//		
//		logger.info("!!! inside class: PriviliagesController , !! method: addprevtorole");
//		privilegeService.addPrivilegeToRoles(previleges);
//		return new ResponseEntity<>(new RestAPIResponse("Success", "Previleges Saved"), HttpStatus.CREATED);
//	}

	@RequestMapping(value = "/getPrivilegesById/{roleId}/{companyId}", method = RequestMethod.GET)
	public ResponseEntity<RestAPIResponse> checkPriviliage(@PathVariable long roleId, @PathVariable Long companyId) {
		logger.info("!!! inside class: PriviliagesController , !! method: checkPriviliage");

		PrivilegeVO privileges = privilegeService.getPrivilegesById(roleId, companyId);
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "priviliges feteched", privileges),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getPrivilegesByRoleIdCompany/{roleId}/{company}", method = RequestMethod.GET)
	public ResponseEntity<RestAPIResponse> checkPriviliageByRoleIdAndCompany(@PathVariable long roleId,
			@PathVariable String company) {
		logger.info("!!! inside class: PriviliagesController , !! method: checkPriviliage");
		PrivilegeVO privileges = privilegeService.getPrivilegesByUsingRoleIdAndCompany(roleId, company);
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "priviliges feteched", privileges),
				HttpStatus.OK);
	}

//	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces = "application/json")
//	public ResponseEntity<RestAPIResponse> updatePrivileges(@RequestBody List<CompanyPrivilegesDto> company) {
//		logger.info("!!! inside class: CompanyController, !! method: updateCompany ");
//		Privilege obj = privilegeService.updatePrivileges(company);
//		return new ResponseEntity<>(new RestAPIResponse("success", "update TimeSheet successfully", obj),
//				HttpStatus.OK);
//	}

//	@RequestMapping(value = "/getByCompany/{company}", method = RequestMethod.GET, produces = "application/json")
//	public ResponseEntity<RestAPIResponse> getCardsByGivenCompany(@PathVariable String company) {
//		logger.info("!!! inside class: CompanyController, !! method: updateCompany ");
//
//		Long compnayId = null;
//		try {
//			compnayId = EncryptionUtil.decrypt(company);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		List<PrivilegeCompanyCardProjection> obj = privilegeService.getCardsForGivenCompany(compnayId);
//		return new ResponseEntity<>(new RestAPIResponse("success", "Privs for companywise obtained successfully", obj),
//				HttpStatus.OK);
//	}

//	@RequestMapping(value = "/getAllSelectedCompanies", method = RequestMethod.GET, produces = "application/json")
//	public ResponseEntity<RestAPIResponse> getAllSelectedCompanies() {
//		logger.info("!!! inside class: CompanyController, !! method: updateCompany ");
//
//		List<PrivilegeCompanyCardProjection> obj = privilegeService.fetchAllSelectedCompanies();
//		return new ResponseEntity<>(new RestAPIResponse("success", "Privs for companywise obtained successfully", obj),
//				HttpStatus.OK);
//	}

}
