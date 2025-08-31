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
import com.narvee.ats.auth.dto.SortingRequestDTO;
import com.narvee.ats.auth.entity.Qualification;
import com.narvee.ats.auth.service.IQualificationService;

@RestController
@RequestMapping("/qualification")
@CrossOrigin
public class QualificationController {

	private static final Logger logger = LoggerFactory.getLogger(QualificationController.class);

	@Autowired
	private IQualificationService qualificationService;

	@RequestMapping(value = "saveQualification", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> saveCity(@RequestBody Qualification qualification) {
		logger.info("!!! inside class: QualificationController, !! method: saveQualification");
		boolean saveQualification = qualificationService.saveQualification(qualification);
		if (saveQualification) {
			return new ResponseEntity<>(new RestAPIResponse("success", "saved Qualification Entity", "Entity Saved"),
					HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(new RestAPIResponse("duplicate", "Record Already Exists", "Not Saved"),
					HttpStatus.OK);
		}
	}

	@RequestMapping(value = "getQualificationById/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getQualificationById(@PathVariable Long id) {
		logger.info("!!! inside class: QualificationController, !! method: getQualification");

		Qualification qualification = qualificationService.getQualificationById(id);
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Get Qualification", qualification),
				HttpStatus.OK);
	}

	@RequestMapping(value = "all", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getAllQualifications() {
		logger.info("!!! inside class: QualificationController, !! method: getAllQualifications");
		return new ResponseEntity<>(
				new RestAPIResponse("success", "getall Cities", qualificationService.getAllQualifications()),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Qualification>> AllQualifications() {
		logger.info("!!! inside class: QualificationController, !! method: AllQualifications");
		return new ResponseEntity<>(qualificationService.getAllQualifications(), HttpStatus.OK);
	}

	@RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<RestAPIResponse> deleteQualificationByID(@PathVariable long id) {
		logger.info("!!! inside class: QualificationController, !! method: deleteQualificationByID");
		try {
			qualificationService.deleteQualificationById(id);
		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			e.printStackTrace();
			return new ResponseEntity<>(new RestAPIResponse("fail", "Cannot perform operation"), HttpStatus.OK);
		}
		return new ResponseEntity<>(new RestAPIResponse("success", "delete Qualification"), HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/allWithFilterAndSorting", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getAllQualificationsWithSortingAndFiltering(@RequestBody SortingRequestDTO qualificationsortandfilterdto) {
		logger.info("!!! inside class: QualificationController, !! method: getAllQualificationsWithSortingAndFiltering");
		return new ResponseEntity<>(
				new RestAPIResponse("success", "getall Qualification", qualificationService.getAllQualificationWithSortingAndFiltering(qualificationsortandfilterdto)),
				HttpStatus.OK);
	}
}
