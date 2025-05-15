package com.narvee.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.narvee.commons.RestAPIResponse;
import com.narvee.dto.RequestDTO;
import com.narvee.dto.UpdateTask;
import com.narvee.entity.TmsSubTask;
import com.narvee.entity.TmsTask;
import com.narvee.service.service.SubTaskService;

@RestController
@RequestMapping("/subTask")
public class SubTaskController {

	private static final Logger logger = LoggerFactory.getLogger(SubTaskController.class);

	@Autowired
	private SubTaskService subtaskservice;
	
	@Autowired
	private ObjectMapper mapper;

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
			@PathVariable String status, @PathVariable Long updatedby) {
		logger.info("!!! inside class: SubTaskController , !! method: updateSubTaskStatus");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Updated status  successfully",
				subtaskservice.updateSubTaskStatus(subTaskid, status, updatedby)), HttpStatus.OK);
	}

	@RequestMapping(value = "/trackBySubTask/{subTaskid}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> trackBySubTask(@PathVariable Long subTaskid) {
		logger.info("!!! inside class: SubTaskController , !! method: trackBySubTask");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Comments fetched successfully",
				subtaskservice.ticketTrackerBySubTaskId(subTaskid)), HttpStatus.OK);
	}

	@PostMapping("/updateSubTaskTrack")
	public ResponseEntity<RestAPIResponse> updateTask(@RequestBody UpdateTask updateTask) {
		logger.info("!!! inside class: SubTaskController , !! method: updateTask");
		Boolean flag = subtaskservice.updateSubTaskTrack(updateTask);
		if (flag == true)
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Updated successfully"),
					HttpStatus.OK);
		else
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("Task fail", "Task Not Found"),
					HttpStatus.OK);
	}
  //-----------------------all replicated methods for tms by keerthi----------------------
	
	@PostMapping(value = "/saveSubTask-tms",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<RestAPIResponse> createTmsSubTask(@RequestPart("subtask") String tmsSubTask, @RequestPart(value = "files",required =  false) List<MultipartFile> subTaskFile) throws IOException {
		logger.info("!!! inside class: SubTaskController , !! method: findBySubTaskId--tms");
		 TmsSubTask subTask = mapper.readValue(tmsSubTask, TmsSubTask.class);

		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", "SubTask created successfully", subtaskservice.createTmsSubTask(subTask,subTaskFile)),
				HttpStatus.CREATED);

	}
	
	@PutMapping(value = "/updateSubTask-tms",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<RestAPIResponse> updateSubTaskTms(@RequestPart("subtask") String tmsSubTask, @RequestPart(value = "files",required = false ) List<MultipartFile> subTaskFile) throws JsonMappingException, JsonProcessingException {
		logger.info("!!! inside class: SubTaskController , !! method: updateSubTask --tms");
		TmsSubTask subTask = mapper.readValue(tmsSubTask, TmsSubTask.class);	 
		
		try {
			TmsSubTask UpdateTmsSubTask	 = subtaskservice.updateTmsSubTask(subTask,subTaskFile);
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success",  "Updated successfully",UpdateTmsSubTask),
					HttpStatus.OK);
		}catch(Exception exception){
			
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("fail", "SubTask not found",exception), HttpStatus.OK);
	  }
	}
	
	@PostMapping("/findAllSubTasks-tms")
	public ResponseEntity<RestAPIResponse> findAllSubTasksTms(@RequestBody RequestDTO requestresponsedto) {
		logger.info("!!! inside class: SubTaskController , !! method: findAllSubTasksTms--tms");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched all projects successfully",
				subtaskservice.getAllSubTasksTms(requestresponsedto)), HttpStatus.OK);
	}
	
	@DeleteMapping("/deleteSubTask-tms/{subtaskid}")
	public ResponseEntity<RestAPIResponse> deleteSubTaskByIdTms(@PathVariable Long subtaskid) {
		logger.info("!!! inside class: SubTaskController , !! method: deleteSubTaskById--Tms");
		subtaskservice.deleteSubTaskTms(subtaskid);
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Deleted successfully"),
				HttpStatus.OK);

	}
	

	@GetMapping("/getBySubTaskId-tms/{subtaskid}")
	public ResponseEntity<RestAPIResponse> findBySubTaskIdTms(@PathVariable Long subtaskid) {
		logger.info("!!! inside class: SubTaskController , !! method: findBySubTaskIdTms--tms");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched  SubTask successfully",
				subtaskservice.findBySubTaskIdTms(subtaskid)), HttpStatus.OK);
	}
	

	@RequestMapping(value = "/updateSubTaskStatus-tms/{subTaskid}/{status}/{updatedby}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> updateSubTaskStatusTms(@PathVariable Long subTaskid,
			@PathVariable String status, @PathVariable Long updatedby) {
		logger.info("!!! inside class: SubTaskController , !! method: updateSubTaskStatusTms--tms");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Updated status  successfully",
				subtaskservice.updateSubTaskStatusTms(subTaskid, status, updatedby)), HttpStatus.OK);
	}
	
	@PostMapping("/getBySubTaskTicketId")
	public ResponseEntity<RestAPIResponse> getBySubTaskTicketIdTms(@RequestBody  RequestDTO requestresponsedto) {
		logger.info("!!! inside class: SubTaskController , !! method: getBySubTaskTicketId--tms");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched  SubTask successfully",
				subtaskservice.findTmsSubTaskByTicketId(requestresponsedto)), HttpStatus.OK);
	}

	
	@PostMapping("/updateTmsSubTaskTrack")
	public ResponseEntity<RestAPIResponse> updateTmsTask(@RequestBody UpdateTask updateTask) {
		logger.info("!!! inside class: SubTaskController , !! method: updateTmsTask--tms");
		Boolean flag = subtaskservice.updateTmsSubTaskTrack(updateTask);
		if (flag == true)
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Updated successfully"),
					HttpStatus.OK);
		else
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("Task fail", "Task Not Found"),
					HttpStatus.OK);
	}
	
	@RequestMapping(value = "/trackBySubTask-tms/{subTaskid}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> trackByTmsSubTask(@PathVariable Long subTaskid) {
		logger.info("!!! inside class: SubTaskController , !! method: trackByTmsSubTask--tms");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Comments fetched successfully",
				subtaskservice.ticketTrackerByTmsSubTaskId(subTaskid)), HttpStatus.OK);
	}
}
