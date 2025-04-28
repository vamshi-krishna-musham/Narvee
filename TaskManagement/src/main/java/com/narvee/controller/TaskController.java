package com.narvee.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.narvee.commons.RestAPIResponse;
import com.narvee.dto.DateSearchDTO;
import com.narvee.dto.RequestDTO;
import com.narvee.dto.UpdateTask;
import com.narvee.dto.UserDTO;
import com.narvee.entity.TmsTask;
import com.narvee.feignclient.UserClient;
import com.narvee.repository.TaskRepository;
import com.narvee.service.service.TaskService;

@RestController
public class TaskController {
	private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

	@Autowired
	private UserClient client;

	@Autowired
	private TaskService service;

	@Autowired
	TaskRepository repo;

	@GetMapping("/getAllUsers")
	public List<UserDTO> getuser(@RequestHeader("AUTHORIZATION") String token) {
		logger.info("!!! inside class: TaskController , !! method: getuser");
		return client.getUsers(token);
	}

	@GetMapping("/findByUserId/{userid}")
	public ResponseEntity<RestAPIResponse> findByUserid(@RequestHeader("AUTHORIZATION") String token,
			@PathVariable Long userid) {
		logger.info("!!! inside class: TaskController , !! method: findByUserid");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", "Fetched all tasks successfully", client.FindByUserid(token, userid)),
				HttpStatus.OK);
	}

	@PostMapping("/createTask")
	public ResponseEntity<?> createTask(@RequestBody TmsTask task, @RequestHeader("AUTHORIZATION") String token) {
		logger.info("!!! inside class: TaskController , !! method: createTask");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " task created successfully", service.createTask(task, token)),
				HttpStatus.CREATED);
	}

	@PostMapping("/update")
	public ResponseEntity<?> update(@RequestBody TmsTask task) {
		logger.info("!!! inside class: TaskController , !! method: update");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " task created successfully", service.update(task)), HttpStatus.CREATED);
	}

	@PostMapping("/updateTask")
	public ResponseEntity<RestAPIResponse> updateTask(@RequestBody UpdateTask updateTask) {
		logger.info("!!! inside class: TaskController , !! method: updateTask");
		Boolean flag = service.updateTask(updateTask);
		if (flag == true)
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Updated successfully"),
					HttpStatus.OK);
		else
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("Task fail", "Task Not Found"),
					HttpStatus.OK);
	}

	@GetMapping("/trackByTask/{taskid}")
	public ResponseEntity<RestAPIResponse> getTaskRecord(@PathVariable Long taskid) {
		logger.info("!!! inside class: TaskController , !! method: getTaskRecord");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", "Fetched tasks records successfully", service.ticketTracker(taskid)),
				HttpStatus.OK);
	}

	@GetMapping("/getbyTaskId/{taskid}")
	public ResponseEntity<RestAPIResponse> findBytaskId(@PathVariable Long taskid) {
		logger.info("!!! inside class: TaskController , !! method: findBytaskId");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", "Fetched  task successfully", service.findBytaskId(taskid)),
				HttpStatus.OK);
	}

	@GetMapping("/getByTicketId/{ticketId}")
	public ResponseEntity<RestAPIResponse> getByTicketId(@PathVariable String ticketId) {
		logger.info("!!! inside class: TaskController , !! method: getByTicketId");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", "Fetched  task successfully", service.findByTicketId(ticketId)),
				HttpStatus.OK);
	}

	@GetMapping("/getAllTasks")
	public ResponseEntity<RestAPIResponse> getAllTasks() {
		logger.info("!!! inside class: TaskController , !! method: getAllTasks");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", "Fetched all tasks successfully", service.getAllTasks()), HttpStatus.OK);

	}

	@GetMapping("/taskAssinInfo/{taskid}")
	public ResponseEntity<RestAPIResponse> taskAssinInfo(@PathVariable Long taskid) {
		logger.info("!!! inside class: TaskController , !! method: taskAssinInfo");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", "Fetched  task info successfully", service.taskAssignInfo(taskid)),
				HttpStatus.OK);

	}

	@GetMapping("/trackByUser/{userid}")
	public ResponseEntity<RestAPIResponse> getByAsignUserIdandtaskId(@PathVariable Long userid) {
		logger.info("!!! inside class: TaskController , !! method: getByAsignUserIdandtaskId");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", "Fetched successfully", service.trackerByUser(userid)), HttpStatus.OK);

	}

	@DeleteMapping("/delete/{taskid}")
	public ResponseEntity<RestAPIResponse> deleteTaskById(@PathVariable Long taskid) {
		logger.info("!!! inside class: TaskController , !! method: deleteTaskById");
		try {
			service.deleteTask(taskid);
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "deleted successfully"),
					HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("failed", "Cannot delete this Task because it has associated Sub-Task."),
					HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/getAllTasksRecords", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> allTasksRecords() {
		logger.info("!!! inside class: TaskController , !! method: allTasksRecords");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", "Fetched successfully", service.allTasksRecords()), HttpStatus.OK);

	}

	@RequestMapping(value = "/getTaskReports", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> TaskReports(@RequestBody DateSearchDTO dateSearch) {
		logger.info("!!! inside class: TaskController , !! method: TaskReports");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", "Reports Fetched successfully", service.taskReports(dateSearch)),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/tasksByProjectId", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getTaskbyProjectId(@RequestBody RequestDTO requestResponseDTO) {
		logger.info("!!! inside class: TaskController , !! method: getTaskbyProjectId");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "fetched taskByProjectid",
				service.getTaskByProjectid(requestResponseDTO)), HttpStatus.OK);

	}

	@RequestMapping(value = "/getUsers/{department}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> findBydeparatmentWiseUsers(@PathVariable String department) {
		logger.info("!!! inside class: TaskController , !! method: findBydeparatmentWiseUsers");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success",
				"fetched by deparatmentWise users successfully", service.getUsersByDepartment(department)),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/update/{taskid}/{status}/{updatedby}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> updateTaskStatus(@PathVariable Long taskid, @PathVariable String status,@PathVariable String updatedby) {
		logger.info("!!! inside class: TaskController , !! method: updateTaskStatus");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Updated status  successfully",
				service.updateTaskStatus(taskid, status,updatedby)), HttpStatus.OK);
	}

	@RequestMapping(value = "/findByProjectId", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> findTaskByProjectId(@RequestBody RequestDTO requestResponseDTO) {
		logger.info("!!! inside class: TaskController , !! method: findTaskByProjectId");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", "fetched tasks", service.findTaskByProjectid(requestResponseDTO)),
				HttpStatus.OK);
	}
	
	
	
	@RequestMapping(value = "/getProjectUsers/{projectId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getProjectUsers(@PathVariable String projectId) {
		logger.info("!!! inside class: TaskController , !! method: getProjectUsers");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success",
				"fetched by deparatmentWise users successfully", service.getProjectUsers(projectId)),
				HttpStatus.OK);
	}
	
	//---------------------replicated methods for Tms  added by keerthi ----------------
	
	@PostMapping("/createTmsTask")
	public ResponseEntity<?> createTmsTask(@RequestBody TmsTask task, @RequestHeader("AUTHORIZATION") String token) {
		logger.info("!!! inside class: TaskController , !! method: createTmsTask");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " task created successfully", service.createTmsTask(task, token)),
				HttpStatus.CREATED);
	}
	
	@PostMapping("/updateTmsTask")
	public ResponseEntity<?> updateTmsTask(@RequestBody TmsTask task) {
		logger.info("!!! inside class: TaskController , !! method: updateTmsTask");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " task Updated successfully", service.Tmsupdate(task)), HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/tasksByProjectId-tms", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getTmsTaskbyProjectId(@RequestBody RequestDTO requestResponseDTO) {
		logger.info("!!! inside class: TaskController , !! method: getTaskbyProjectId");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "fetched taskByProjectid",
				service.getTaskByProjectid(requestResponseDTO)), HttpStatus.OK);

	}
	
	@RequestMapping(value = "/findByProjectId-tms", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> findTmsTaskByProjectId(@RequestBody RequestDTO requestResponseDTO) {
		logger.info("!!! inside class: TaskController , !! method: findTaskByProjectId");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", "fetched tasks", service.findTmsTaskByProjectid(requestResponseDTO)),
				HttpStatus.OK);
	}
	
	
	@PostMapping("/updateTask-tms")
	public ResponseEntity<RestAPIResponse> updateTmsTask(@RequestBody UpdateTask updateTask) {
		logger.info("!!! inside class: TaskController , !! method: updateTask");
		Boolean flag = service.updateTmsTask(updateTask);
		if (flag == true)
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Updated successfully"),
					HttpStatus.OK);
		else
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("Task fail", "Task Not Found"),
					HttpStatus.OK);
	}
	
	
	@DeleteMapping("/delete-tms/{taskid}")
	public ResponseEntity<RestAPIResponse> deletetmsTaskById(@PathVariable Long taskid) {
		logger.info("!!! inside class: TaskController , !! method: deletetmsTaskById");
		try {
			service.deleteTask(taskid);
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "deleted successfully"),
					HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("failed", "Cannot delete this Task because it has associated Sub-Task."),
					HttpStatus.OK);
		}

	}
	
}