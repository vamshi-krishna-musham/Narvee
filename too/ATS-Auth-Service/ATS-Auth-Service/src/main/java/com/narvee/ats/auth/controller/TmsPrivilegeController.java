package com.narvee.ats.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.narvee.ats.auth.commons.RestAPIResponse;
import com.narvee.ats.auth.entity.TmsPrivilege;
import com.narvee.ats.auth.request.RoleToPrivilegesVO;
import com.narvee.ats.auth.request.TmsPrivilegeVO;
import com.narvee.ats.auth.service.TmsPrivilegeService;

@RestController
@RequestMapping("/tmsPrivilege")
public class TmsPrivilegeController {
	public static final Logger logger = LoggerFactory.getLogger(TmsPrivilegeController.class);

	@Autowired
	private TmsPrivilegeService privilegeService;

	@RequestMapping(value = "/getPrivileges", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getAllRoles() {
		logger.info("!!! inside class: TmsPrivilegeController , !! method: getAllRoles");
		TmsPrivilegeVO privileges = privilegeService.getAllPrivileges();
		return new ResponseEntity<>(new RestAPIResponse("success", "Successfully fetched all Privileges", privileges),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getPrivilegesById/{roleId}", method = RequestMethod.GET)
	public ResponseEntity<RestAPIResponse> checkPriviliage(@PathVariable long roleId) {
		logger.info("!!! inside class: TmsPrivilegeController , !! method: checkPriviliage");
		TmsPrivilegeVO privileges = privilegeService.getPrivilegesById(roleId);
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", "Succesfully feteched priviliges by role", privileges), HttpStatus.OK);
	}

	@RequestMapping(value = "/savePrevileges", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> savePrevg(@RequestBody TmsPrivilege previleges) {
		logger.info("!!! inside class: TmsPrivilegeController , !! method: savePrevg");
		privilegeService.savePrevileges(previleges);
		return new ResponseEntity<>(new RestAPIResponse("Success", "Previleges Saved"), HttpStatus.CREATED);
	}

	@RequestMapping(value = "/addprevtorole", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> addPrevtoRole(@RequestBody RoleToPrivilegesVO previleges) throws Exception {
		logger.info("!!! inside class: TmsPrivilegeController , !! method: addPrevtoRole");
		privilegeService.addPrivilegeToRole(previleges);
		return new ResponseEntity<>(new RestAPIResponse("Success", "Previleges Saved successfully"),
				HttpStatus.CREATED);
	}

}
