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

import com.narvee.ats.auth.clientdto.FilterTechAndSupportDTO;
import com.narvee.ats.auth.commons.RestAPIResponse;
import com.narvee.ats.auth.dto.TechAndSupportDTO;
import com.narvee.ats.auth.entity.TechAndSupport;
import com.narvee.ats.auth.service.ITechSupportService;

@RestController
@RequestMapping(value = "/techsupp")
public class TechSupportController {
	public static final Logger logger = LoggerFactory.getLogger(TechSupportController.class);

	@Autowired
	private ITechSupportService Service;

	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> AddTechSupport(@RequestBody TechAndSupport techandsupport) {
		logger.info("TechSupportController.AddTechSupport()");
		boolean flg = Service.saveTechSupp(techandsupport);
		if (flg == true) {
			return new ResponseEntity<>(new RestAPIResponse("success", "Record Saved Successfully"),
					HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(new RestAPIResponse("fail", "Record already Exists"), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> updateTechSupport(@RequestBody TechAndSupport techandsupport) {
		logger.info("TechSupportController.updateTechSupport()");
		boolean flg = Service.updateTechSupp(techandsupport);
		if (flg == true) {
			return new ResponseEntity<>(new RestAPIResponse("success", "Record 	Updated Successfully"),
					HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(new RestAPIResponse("fail", "Record already Exists"), HttpStatus.OK);
		}
	}

//	@RequestMapping(value = "/search/{keyword}", method = RequestMethod.GET, produces = "application/json")
//	public ResponseEntity<RestAPIResponse> getAll(@PathVariable String keyword) {
//		logger.info("TechSupportController.getAll() with key search");
//		List<TechAndSupportDTO> all = Service.getAll(keyword);
//		return new ResponseEntity<>(new RestAPIResponse("success", "Fetch TechSupport with search", all),
//				HttpStatus.OK);
//	}

	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> all() {
		logger.info("TechSupportController.all()");
		List<TechAndSupportDTO> all = Service.all();
		return new ResponseEntity<>(new RestAPIResponse("success", "Fetch TechSupport ", all), HttpStatus.OK);

	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<RestAPIResponse> deleteByID(@PathVariable long id) {
		logger.info("TechSupportController.deleteByID()");
		return new ResponseEntity<>(
				new RestAPIResponse("success", "Record Deleted Successfully", Service.deleteSupp(id)), HttpStatus.OK);
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getTechsupoort(@PathVariable long id) {
		logger.info("TechSupportController.getTechsupoort() by id");
		return new ResponseEntity<>(
				new RestAPIResponse("success", "Record fetched Successfully", Service.getTechSupp(id)), HttpStatus.OK);
	}

	@RequestMapping(value = "/status", method = RequestMethod.PATCH, produces = "application/json")
	public ResponseEntity<RestAPIResponse> changeStatus(@RequestBody TechAndSupport techandsupport) {
		logger.info("TechSupportController.changeStatus()");
		Long id = techandsupport.getId();
		String status = "Active";
		String remarks = techandsupport.getRemarks();

		int changestat = 0;
		String result;
		if (status.equals("Active"))
			result = "InActive";
		else
			result = "Active";
		changestat = Service.changeStatus(result, id, remarks);
		if (changestat != 0) {
			logger.info("TechSupportController.changeStatus() => Status Chnaged ");
		} else {
			logger.info("TechSupportController.changeStatus() => Status not Chnaged ");
		}
		Service.changeStatus(result, id, remarks);
		return new ResponseEntity<>(new RestAPIResponse("success", "Status Change Successfully", "Done"),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/allTechWithSorting", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> allWithSortingWithfilters(
			@RequestBody FilterTechAndSupportDTO filtertechandsupportdto) {
		logger.info("TechSupportController.all()");
		Page<TechAndSupportDTO> all = Service.allWithSortingWithFiltering(filtertechandsupportdto);
		return new ResponseEntity<>(new RestAPIResponse("success", "Fetch TechSupport ", all), HttpStatus.OK);

	}

}
