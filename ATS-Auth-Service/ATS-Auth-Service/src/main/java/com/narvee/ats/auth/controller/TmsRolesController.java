package com.narvee.ats.auth.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.narvee.ats.auth.commons.RestAPIResponse;
import com.narvee.ats.auth.dto.RequestDto;
import com.narvee.ats.auth.dto.RolesResponseDto;
import com.narvee.ats.auth.entity.TmsRoles;
import com.narvee.ats.auth.service.TmsRolesSerivice;

@RequestMapping("/tmsRoles")
@RestController
public class TmsRolesController {
	public static final Logger logger = LoggerFactory.getLogger(TmsRolesController.class);

	@Autowired
	private TmsRolesSerivice rolesSerivice;

	@RequestMapping(value = "/all", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getAllRoles(@RequestBody RequestDto requestDto) throws Exception {
		logger.info("!!! inside class: TmsRolesController, !! method: getAllRoles()");
		Page<RolesResponseDto> roles = rolesSerivice.getAllRole(requestDto);
		return new ResponseEntity<>(new RestAPIResponse("success", "Roles fetched Successfully", roles), HttpStatus.OK);
	}

	@RequestMapping(value = "/all/{AdminId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getAllRoles(@PathVariable Long AdminId) throws Exception {
		logger.info("!!! inside class: TmsRolesController, !! method: getAllRoles()");
		List<TmsRoles> roles = rolesSerivice.getAllRoleByAdmin(AdminId);
		return new ResponseEntity<>(new RestAPIResponse("success", "Roles fetched successfully", roles), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateRole", method = RequestMethod.PUT, produces = "application/json")
	public ResponseEntity<RestAPIResponse> UpdateRoles(@RequestBody TmsRoles roles) {
		logger.info("!!! inside class: TmsRolesController, !! method: UpdateRoles()");
		boolean flg = rolesSerivice.updateRole(roles);

		if (flg) {
			logger.info("Role Updated after checking duplicate records available or not");
			return new ResponseEntity<>(new RestAPIResponse("success", "Role updated successfully", "Updated"), HttpStatus.CREATED);
		} else {
			logger.info("Role not Updated => Role Already Exist");
			return new ResponseEntity<>(new RestAPIResponse("Fail", "Role already exist", "Data not Saved"),
					HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<RestAPIResponse> deleteRole(@PathVariable Long id) {
		logger.info("!!! inside class: TmsRolesController, !! method: deleteRole()");

		boolean val = rolesSerivice.deleteRole(id);
		if (val == true) {
			return new ResponseEntity<>(new RestAPIResponse("success", "Role deleted successfully", ""), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new RestAPIResponse("fail", "Role is assigned to a user and cannot be deleted.", ""),
					HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> AddRoles(@RequestBody TmsRoles roles) throws Exception {
		logger.info("inside RolesController.AddRoles()");

		boolean flg = rolesSerivice.saveRole(roles);
		if (flg) {
			logger.info("Role saved after checking duplicate records available or not");
			return new ResponseEntity<>(new RestAPIResponse("success", "Role added successfully", "Success"), HttpStatus.CREATED);
		} else {
			logger.info("Role not saved => Role Already Exist");
			return new ResponseEntity<>(new RestAPIResponse("Fail", "Role already exist", "Data not Saved"),
					HttpStatus.OK);
		}
	}

}
