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
import com.narvee.ats.auth.dto.GetRoles;
import com.narvee.ats.auth.entity.Roles;
import com.narvee.ats.auth.service.IRoleService;
import com.narvee.ats.auth.util.EncryptionUtil;

@RestController
@RequestMapping("/roles")
@CrossOrigin
public class RolesController {
	public static final Logger logger = LoggerFactory.getLogger(RolesController.class);

	@Autowired
	private IRoleService Service;

	@RequestMapping(value = "/save/{companyId}", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> AddRoles(@RequestBody Roles roles, @PathVariable String companyId) throws Exception {
		logger.info("inside RolesController.AddRoles()");
		Long Id=EncryptionUtil.decrypt(companyId);
		roles.setCompanyid(Id);
		
		boolean flg = Service.saveRole(roles);			
		if (flg) {
			logger.info("Role saved after checking duplicate records available or not");
			return new ResponseEntity<>(new RestAPIResponse("success", "Role Saved", "Success"), HttpStatus.CREATED);
		} else {
			logger.info("Role not saved => Role Already Exist");
			return new ResponseEntity<>(new RestAPIResponse("Fail", "Role Already Exist", "Data not Saved"),
					HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getAllRoles() {
		logger.info("inside RolesController.getAllRoles()=> fetching all roles");
		List<Roles> saveroles = Service.getAllRoles();
		return new ResponseEntity<>(new RestAPIResponse("success", "All Roles Fetched", saveroles), HttpStatus.OK);
	}

	@RequestMapping(value = "/allRoles/{companyId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getAllRolesByCompanyId(@PathVariable String companyId) {
		Long id = null;
		try {
			id = EncryptionUtil.decrypt(companyId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("inside RolesController.getAllRoles()=> fetching all roles");
		List<Roles> saveroles = Service.getAllRolesByCompany(id);
		return new ResponseEntity<>(new RestAPIResponse("success", "All Roles Fetched", saveroles), HttpStatus.OK);
	}

	@RequestMapping(value = "/all/{company}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getAllRolesCompanyWise(@PathVariable String company) throws Exception {
		logger.info("inside RolesController.getAllRoles()=> fetching all roles");
		List<Roles> saveroles = Service.getAllRolesCompanyWise(company);
		return new ResponseEntity<>(new RestAPIResponse("success", "All Roles Fetched", saveroles), HttpStatus.OK);
	}

	@RequestMapping(value = "/getrole/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getRole(@PathVariable Long id) {
		logger.info("inside RolesController.getRole()=> fetching single role by id");
		Roles saveroles = Service.getRole(id);
		return new ResponseEntity<>(new RestAPIResponse("success", "Role Feteched By ID", saveroles), HttpStatus.OK);
	}

	@RequestMapping(value = "/updaterole", method = RequestMethod.PUT, produces = "application/json")
	public ResponseEntity<RestAPIResponse> UpdateRoles(@RequestBody Roles roles) {
		logger.info("inside RolesController.UpdateRoles()");
		boolean flg = Service.updateRole(roles);
		if (flg) {
			logger.info("Role Updated after checking duplicate records available or not");
			return new ResponseEntity<>(new RestAPIResponse("success", "Role Updated", "Updated"), HttpStatus.CREATED);
		} else {
			logger.info("Role not Updated => Role Already Exist");
			return new ResponseEntity<>(new RestAPIResponse("Fail", "Role Already Exist", "Data not Saved"),
					HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<RestAPIResponse> deleteRoleCompanyWise(@PathVariable Long id) {
		logger.info("inside RolesController.deleteRole()=> delete single role by id");

		boolean val = Service.deleteRoleCompanyWise(id);
		if (val == true) {
			return new ResponseEntity<>(new RestAPIResponse("success", "Role Deleted", ""), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new RestAPIResponse("fail", "Role Assigned to User, Role Not Deleted", ""),
					HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/status", method = RequestMethod.PATCH, produces = "application/json")
	public ResponseEntity<RestAPIResponse> changeStatus(@RequestBody Roles roles) {
		logger.info("inside RolesController.changeStatus()");
		Long id = roles.getRoleid();
		String status = roles.getStatus();
		String remarks = roles.getRemarks();

		int changestat = 0;
		String result;

		if (status.equals("Active") || status == null) {
			result = "InActive";
		} else {
			result = "Active";
		}

		changestat = Service.changeStatus(result, id, remarks);
		if (changestat != 0) {
			logger.info("inside RolesController.changeStatus() => Status changed successfully");
		} else {
			logger.info("inside RolesController.changeStatus()=> Status not changed ");
		}
		Service.changeStatus(result, id, remarks);
		return new ResponseEntity<>(new RestAPIResponse("success", "Status Change Successfully", "Done"),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getroles/{companyId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getAllRolesDrop(@PathVariable String companyId) throws Exception {
		logger.info("UsersController.getAllRoles()");
		
		Long cid=EncryptionUtil.checkCidType(companyId);
		List<GetRoles> getrole = Service.getRoles(cid);
		return new ResponseEntity<>(new RestAPIResponse("success", "All Roles Fetched", getrole), HttpStatus.OK);

	}
}
