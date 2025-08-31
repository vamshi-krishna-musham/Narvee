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
import com.narvee.ats.auth.entity.Visa;
import com.narvee.ats.auth.service.IVisaService;

@RestController
@RequestMapping("/visa")
@CrossOrigin
public class VisaController {
	public static final Logger logger = LoggerFactory.getLogger(VisaController.class);

	@Autowired
	public IVisaService service;

	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> saveVisa(@RequestBody Visa visa) {
		logger.info("VisaController.saveVisa()");
		boolean saveStates = service.saveVisa(visa);
		if (saveStates) {
			return new ResponseEntity<>(new RestAPIResponse("success", "Visa Added Successfully", "Entity Saved"),
					HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(new RestAPIResponse("duplicate", "Record Already Exists", "Not Saved"),
					HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<RestAPIResponse> deleteVisaByID(@PathVariable long id) {
		logger.info("VisaController.deleteVisaByID()");
		try {
			boolean flg = service.deleteVisaStatus(id);
		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			e.printStackTrace();
			return new ResponseEntity<>(new RestAPIResponse("fail", "Cannot perform operation"), HttpStatus.OK);
		}
		return new ResponseEntity<>(new RestAPIResponse("success", " Visa Deleted Sucessfully", ""), HttpStatus.OK);
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getAllVisa() {
		logger.info("VisaController.getAllVisa()");
		return new ResponseEntity<>(new RestAPIResponse("success", "Visa Fetched Successfully", service.getAllVisa()),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getbyid/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getVisaByID(@PathVariable long id) {
		logger.info("VisaController.getVisaByID()");
		return new ResponseEntity<>(
				new RestAPIResponse("success", "Visa Fetched Successfully", service.getVisaById(id)), HttpStatus.OK);
	}

	@RequestMapping(value = "/visa", method = RequestMethod.PUT, produces = "application/json")
	public ResponseEntity<RestAPIResponse> updateVisa(@RequestBody Visa visa) {
		logger.info("VisaController.updateVisa()");
		return new ResponseEntity<>(new RestAPIResponse("success", "Update Visa Successfully", service.saveVisa(visa)),
				HttpStatus.ACCEPTED);
	}

//	@RequestMapping(value = "/pagination/{pageNo}", method = RequestMethod.GET, produces = "application/json")
//	public ResponseEntity<RestAPIResponse> findPaginated(@PathVariable("pageNo") int pageNo) {
//		logger.info("VisaController.findPaginated()");
//		int pageSize = 2;
//		Page<Visa> findPaginated = service.findPaginated(pageNo, pageSize);
//		List<Visa> findAlltech = findPaginated.getContent();
//		return new ResponseEntity<>(new RestAPIResponse("success", "fetching Visa By Page No", findAlltech),
//				HttpStatus.OK);
//	}

	@RequestMapping(value = "/visas", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getVisa() {
		logger.info("!!! inside class: CityController, !! method: getAllCities");
		return new ResponseEntity<>(new RestAPIResponse("success", "getall Cities", service.getvisaidname()),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getVisas", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Object[]>> getAllVisas() {
		logger.info("!!! inside class: CityController, !! method: getAllVisas");
		return new ResponseEntity<>(service.getvisaidname(), HttpStatus.OK);
	}

	@RequestMapping(value = "/h1visas", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> geth1Visa() {
		logger.info("!!! inside class: CityController, !! method: getAllCities");
		return new ResponseEntity<>(new RestAPIResponse("success", "getall Cities", service.getH1visa()),
				HttpStatus.OK);
	}
	
	@RequestMapping(value = "/allVisaWithSorting", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getAllVisaWithSortingAndFiltering(@RequestBody SortingRequestDTO visasortandfilterdto) {
		logger.info("VisaController.getAllVisa()");
		return new ResponseEntity<>(new RestAPIResponse("success", "Visa Fetched Successfully", service.getAllVisaWithSortingAndFiltering(visasortandfilterdto)),
				HttpStatus.OK);
	}

	
}
