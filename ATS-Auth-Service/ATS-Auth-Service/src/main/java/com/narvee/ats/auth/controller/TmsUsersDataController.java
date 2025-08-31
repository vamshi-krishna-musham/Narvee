package com.narvee.ats.auth.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.narvee.ats.auth.commons.RestAPIResponse;
import com.narvee.ats.auth.service.TmsUsersService;

import com.narvee.ats.auth.tms.dto.AllTmsUsers;
import com.narvee.ats.auth.tms.dto.GetAllUserRequestDTO;
import com.narvee.ats.auth.tms.dto.TmsUsersDropDown;
import com.narvee.ats.auth.tms.dto.TmsUsersInfo;

@RestController
@RequestMapping("/tms")
public class TmsUsersDataController {

	private static final Logger logger = LoggerFactory.getLogger(TmsUsersController.class);

	@Autowired
	private TmsUsersService tmsUsersService;

	@PostMapping("/getAllUsers")
	public ResponseEntity<RestAPIResponse> getAllTmsUsers(@RequestBody GetAllUserRequestDTO allUserRequestDTO) {
		logger.info("!!! inside class: TmsUsersController, !! method: getAllTmsUsers ");

		try {
			Page<AllTmsUsers> info = tmsUsersService.getAllUsersByAdmin(allUserRequestDTO);

			return ResponseEntity.ok(new RestAPIResponse("success", "Resource get successfully", info));
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.OK)
					.body(new RestAPIResponse("fail", "Failed to get Resource Data", e.getMessage()));
		}
	}

	@GetMapping("/getUsersDropDown/{adminId}")
	public ResponseEntity<RestAPIResponse> getTmsUsers(@PathVariable Long adminId) {
		logger.info("!!! inside class: TmsUsersController, !! method: getTmsUsers ");

		try {
			List<TmsUsersDropDown> info = tmsUsersService.getTmsUsersDropDown(adminId);

			return ResponseEntity.ok(new RestAPIResponse("success", "Get Resource dropdown successfully", info));
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.OK)
					.body(new RestAPIResponse("fail", "Failed to get Resource", e.getMessage()));
		}
	}

	@DeleteMapping("/DeleteTeamMember/{Id}")
	public ResponseEntity<RestAPIResponse> deleteTmsUsers(@PathVariable Long Id) {
		logger.info("!!! inside class: TmsUsersController, !! method: deleteTmsUsers ");

		try {
			tmsUsersService.deleteTeamMember(Id);
			return ResponseEntity.ok(new RestAPIResponse("success", "Resource deleted successfully"));
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.OK)
					.body(new RestAPIResponse("fail", "Deletion failed: The resource is currently linked to existing project assignments.", e.getMessage()));
		}
	}

	@PostMapping("/UpdateTmsUser")
	public ResponseEntity<RestAPIResponse> UpdateTmsUsers(@RequestBody TmsUsersInfo tmsUsersInfo) {
		logger.info("!!! inside class: TmsUsersController, !! method: UpdateTmsUsers ");

		try {
			tmsUsersService.updateTeamMember(tmsUsersInfo);
			return ResponseEntity.ok(new RestAPIResponse("success", "updated successfully."));
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.OK)
					.body(new RestAPIResponse("fail", "The information could not be updated", e.getMessage()));
		}
	}

	@GetMapping("/findByid/{userid}")
	public ResponseEntity<RestAPIResponse> findByid(@PathVariable Long userid) {
		logger.info("!!! inside class: TmsUsersController, !! method: findByid ");
		return ResponseEntity.status(HttpStatus.OK)
				.body(new RestAPIResponse("success", "Find user", tmsUsersService.findByUserId(userid)));
	}
	
	@DeleteMapping("/deleleProfilepic/{userid}")
	public ResponseEntity<RestAPIResponse> deleleProfilepic(@PathVariable Long userid) {
		logger.info("!!! inside class: TmsUsersController, !! method: deleleProfilepic ");
		tmsUsersService.deleteProfilePic(userid);
		return ResponseEntity.status(HttpStatus.OK)
				.body(new RestAPIResponse("success", "User profile photo removed successfully"));
	}
	

	

}
