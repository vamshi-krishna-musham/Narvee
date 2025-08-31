package com.narvee.ats.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.narvee.ats.auth.commons.RestAPIResponse;
import com.narvee.ats.auth.dto.SortingRequestDTO;
import com.narvee.ats.auth.service.ILoginTrackerService;

@RestController
@RequestMapping("/loginTrack")
//@CrossOrigin
public class LoginTrackController {
	public static final Logger logger = LoggerFactory.getLogger(Logincontroller.class);

	@Autowired
	private ILoginTrackerService iLoginTrackerService;

	@RequestMapping(value = "/getAll", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getAllLoginTrack(@RequestBody SortingRequestDTO sortingRequestDTO) {
		logger.info("!!! inside class: LoginTrackController, !! method: getAllLoginTrack ");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", "successfully Fetched All Logintrack details",
						iLoginTrackerService.getAllLoginTrack(sortingRequestDTO)),
				HttpStatus.OK);

	}

}
