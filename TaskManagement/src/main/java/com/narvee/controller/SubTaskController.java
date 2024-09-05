package com.narvee.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.narvee.commons.RestAPIResponse;
import com.narvee.dto.RequestDTO;
import com.narvee.entity.TmsSubTask;
import com.narvee.service.service.SubTaskService;

@RestController
@RequestMapping("/subTask")
public class SubTaskController {

	private static final Logger logger = LoggerFactory.getLogger(SubTaskController.class);

	@Autowired
	private SubTaskService subtaskservice;

	@PostMapping("/saveSubTask")
	public ResponseEntity<RestAPIResponse> createSubTask(@RequestBody TmsSubTask subtask) {
		logger.info("!!! inside class: SubTaskController , !! method: findBySubTaskId");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", "SubTask created successfully", subtaskservice.createSubTask(subtask)),
				HttpStatus.CREATED);

	}

	@GetMapping("/getBySubTaskId/{subtaskid}")
	public ResponseEntity<RestAPIResponse> findBySubTaskId(@PathVariable Long subtaskid) {
		logger.info("!!! inside class: SubTaskController , !! method: findBySubTaskId");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched  SubTask successfully",
				subtaskservice.findBySubTaskId(subtaskid)), HttpStatus.OK);
	}

	@GetMapping("/getBySubTaskTicketId/{ticketId}")
	public ResponseEntity<RestAPIResponse> getBySubTaskTicketId(@PathVariable String ticketId) {
		logger.info("!!! inside class: SubTaskController , !! method: getBySubTaskTicketId");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched  SubTask successfully",
				subtaskservice.findBySubTaskTicketId(ticketId)), HttpStatus.OK);
	}

	@DeleteMapping("/deleteSubTask/{subtaskid}")
	public ResponseEntity<RestAPIResponse> deleteSubTaskById(@PathVariable Long subtaskid) {
		logger.info("!!! inside class: SubTaskController , !! method: deleteSubTaskById");
		subtaskservice.deleteSubTask(subtaskid);
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Deleted successfully"),
				HttpStatus.OK);

	}

	@PutMapping("/update")
	public ResponseEntity<RestAPIResponse> updateSubTask(@RequestBody TmsSubTask subtask) {
		logger.info("!!! inside class: SubTaskController , !! method: updateproject");
		Boolean flag = subtaskservice.updateSubTask(subtask);
		if (flag == true) {
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Updated successfully"),
					HttpStatus.OK);
		} else
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("fail", "SubTask not found"), HttpStatus.OK);
	}

	@PostMapping("/getSubTaskUser")
	public ResponseEntity<RestAPIResponse> getSubTaskUser(@RequestBody RequestDTO requestresponsedto) {
		logger.info("!!! inside class: SubTaskController , !! method: getSubTaskUser");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched all projects successfully",
				subtaskservice.getSubTaskUser(requestresponsedto)), HttpStatus.OK);
	}

	@PostMapping("/findAllSubTasks")
	public ResponseEntity<RestAPIResponse> findAllSubTasks(@RequestBody RequestDTO requestresponsedto) {
		logger.info("!!! inside class: SubTaskController , !! method: findAllSubTasks");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched all projects successfully",
				subtaskservice.getAllSubTasks(requestresponsedto)), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateSubTaskStatus/{subTaskid}/{status}/{updatedby}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> updateSubTaskStatus(@PathVariable Long subTaskid,
			@PathVariable String status,@PathVariable Long updatedby) {
		logger.info("!!! inside class: SubTaskController , !! method: updateSubTaskStatus");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Updated status  successfully",
				subtaskservice.updateSubTaskStatus(subTaskid, status, updatedby)), HttpStatus.OK);
	}

}
