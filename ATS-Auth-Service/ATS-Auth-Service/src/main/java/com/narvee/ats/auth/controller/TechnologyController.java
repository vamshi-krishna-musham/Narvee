package com.narvee.ats.auth.controller;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.narvee.ats.auth.commons.RestAPIResponse;
import com.narvee.ats.auth.entity.Technologies;
import com.narvee.ats.auth.service.ITechnologyService;

@RestController
@RequestMapping("/technology")
@CrossOrigin
public class TechnologyController {
	public static final Logger logger = LoggerFactory.getLogger(TechnologyController.class);
	@Autowired
	public ITechnologyService service;

	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> saveTechnology(@RequestBody Technologies technologies) {
		logger.info("TechnologyController.saveTechnology()");
		return new ResponseEntity<>(
				new RestAPIResponse("success", "Technology Saved", service.saveTechnologies(technologies)),
				HttpStatus.CREATED);
	}

	@RequestMapping(value = "/technologies", method = RequestMethod.PUT, produces = "application/json")
	public ResponseEntity<RestAPIResponse> updateTechnology(@RequestBody Technologies technologies) {
		logger.info("TechnologyController.updateTechnology()" + technologies);
		return new ResponseEntity<>(
				new RestAPIResponse("success", "Technology Saved", service.saveTechnologies(technologies)),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getall() {
		logger.info("TechnologyController.getall()");
		List<Technologies> allTechnologies = service.getAllTechnologies();
		Comparator<Technologies> comparator = (c1, c2) -> {
			return Long.valueOf(c2.getId()).compareTo(c1.getId());
		};
		Collections.sort(allTechnologies, comparator);
		return new ResponseEntity<>(new RestAPIResponse("success", "Technology Saved", allTechnologies), HttpStatus.OK);
	}

	@RequestMapping(value = "/allTech/{pageNo}/{pageSize}/{field}/{sortField}/{sortOrder}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> fetchAllTechnologies(@PathVariable int pageNo, @PathVariable int pageSize,
			@PathVariable String field,@PathVariable(required = false) String sortField, @PathVariable(required = false) String sortOrder) {
		Page<Technologies> allTechnologies = service.getAllTechnologies(pageNo, pageSize, field,sortField,sortOrder);
		return new ResponseEntity<>(new RestAPIResponse("success", "getall Technologies", allTechnologies),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getbyid/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getTechnologyByID(@PathVariable int id) {
		logger.info("TechnologyController.getTechnologyByID()");
		return new ResponseEntity<>(
				new RestAPIResponse("success", "Get Technology By ID", service.getTechnologyByID(id)), HttpStatus.OK);
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<RestAPIResponse> deleteTechnologyByID(@PathVariable int id) {
		logger.info("TechnologyController.deleteTechnologyByID()");
		String msg = "";
		try {
			boolean deleteTechnologiesById = service.deleteTechnologiesById(id);
			msg = "Success";

			if (deleteTechnologiesById) {
				return new ResponseEntity<>(new RestAPIResponse("success", "deleted Successfully", msg), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new RestAPIResponse("associated",
						"Technology Associated With Consultant, Requirement not deleted"), HttpStatus.OK);
			}

		} catch (SQLIntegrityConstraintViolationException e) {
			msg = "Fail";
			e.printStackTrace();
			return new ResponseEntity<>(new RestAPIResponse("Fail", "deleted Successfully", msg),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/status", method = RequestMethod.PATCH, produces = "application/json")
	public ResponseEntity<RestAPIResponse> changeStatus(@RequestBody Technologies technologies) {
		logger.info("TechnologyController.changeStatus()");
		long id = technologies.getId();
		String status = "Active";
		String remarks = technologies.getRemarks();

		int changestat = 0;
		String result;
		if (status.equals("Active"))
			result = "InActive";
		else
			result = "Active";
		changestat = service.changeStatus(result, id, remarks);
		if (changestat != 0) {
			logger.info("TechnologyController.changeStatus() => status changed");
		} else {
			logger.info("TechnologyController.changeStatus() => status changed");
		}
		// service.changeStatus(result, id, remarks);
		return new ResponseEntity<>(new RestAPIResponse("success", "Status Change Successfully", "Done"),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/tech", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getCities() {
		logger.info("!!! inside class: TechnologyController, !! method: getAllTech");
		return new ResponseEntity<>(new RestAPIResponse("success", "getall Techhnologies", service.gettechnologies()),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/technologies", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Object[]>> getTechnologies() {
		logger.info("!!! inside class: TechnologyController, !! method: getTechnologies");
		return new ResponseEntity<List<Object[]>>(service.gettechnologies(), HttpStatus.OK);
	}

	@RequestMapping(value = "/getskillsbyid/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getTechnologySkillsByID(@PathVariable int id) {
		logger.info("TechnologyController.getTechnologyByID()");
		return new ResponseEntity<>(
				new RestAPIResponse("success", "Get Technology By ID", service.getTechnologySkillsByID(id)),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/skillsById/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<String> getTechSkillsByID(@PathVariable int id) {
		logger.info("TechnologyController.getTechSkillsByID()");
		return new ResponseEntity<>(service.getTechnologySkillsByID(id), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/duplicatecheck/{technologyarea}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> checkDuplicateTechnologeArea(@PathVariable String technologyarea) {
	    boolean isAvailable = service.isTechnologyAreaAvailable(technologyarea);
	    if (!isAvailable) {
	        return new ResponseEntity<>(
	            new RestAPIResponse("failed", "Technology area already exists", false),
	            HttpStatus.OK
	        );
	    }
	    return new ResponseEntity<>(
	        new RestAPIResponse("success", "Technology area is available", true),
	        HttpStatus.OK
	    );
	}

}
