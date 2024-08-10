package com.narvee.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.narvee.dto.RequestResponseDTO;
import com.narvee.dto.TaskReportsDTO;
import com.narvee.dto.TicketTrackerSortDTO;
import com.narvee.dto.TrackerByUserDTO;
import com.narvee.dto.UpdateTask;
import com.narvee.dto.UserDTO;
import com.narvee.entity.Task;
import com.narvee.feignclient.UserClient;
import com.narvee.repository.TaskRepository;
import com.narvee.service.service.TaskService;

@RestController
@CrossOrigin(origins="*")
public class TaskController {
	private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
	
	 @Autowired private UserClient client;
	

	@Autowired
	private TaskService service;

	@Autowired
	TaskRepository repo;

	@GetMapping("/getAllUsers")
	public List<UserDTO> getuser(@RequestHeader("AUTHORIZATION") String token) {
		logger.info("!!! inside class: TaskController , !! method: getuser");
		return client.getUsers(token);
	}

	@GetMapping("/findByUserid/{userid}")
	public ResponseEntity<RestAPIResponse> findByUserid(@RequestHeader("AUTHORIZATION") String token,
			@PathVariable Long userid) {
		logger.info("!!! inside class: TaskController , !! method: getuser");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", "Fetched all tasks successfully", client.FindByUserid(token, userid)),
				HttpStatus.OK);
	}

	@PostMapping("/createTask")
	public ResponseEntity<?> createTask(@RequestBody Task task, @RequestHeader("AUTHORIZATION") String token) {
		logger.info("!!! inside class: TaskController , !! method: createTask");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " task created successfully", service.createTask(task, token)),
				HttpStatus.CREATED);
	}

	@PostMapping("/updateTask")
	public ResponseEntity<RestAPIResponse> updateTask(@RequestBody UpdateTask updateTask) {
		logger.info("!!! inside class: TaskController , !! method: updateTask");
		Boolean flag = service.updateTask(updateTask);
		if(flag == true)
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
				new RestAPIResponse("success", "Fetched tasks records successfully", repo.ticketTracker(taskid)),
				HttpStatus.OK);
	}

	@GetMapping("/getbyTaskId/{taskid}")
	public ResponseEntity<RestAPIResponse> findBytaskId(@PathVariable Long taskid) {
		logger.info("!!! inside class: TaskController , !! method: findBytaskId");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", "Fetched  task successfully", service.findBytaskId(taskid)),
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
		service.deleteTask(taskid);
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "deleted successfully"),
				HttpStatus.OK);

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

	//PrashanthMadishetti
	@PostMapping("/getAllTasks")
	public ResponseEntity<RestAPIResponse> getAllTasksWithSortingAndPagination(
			@RequestBody RequestResponseDTO requestresponsedto) {
		logger.info("!!! inside class: TaskController , !! method: getAllTasksWithSortingAndPagination");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched all Tasks successfully",
				service.findAllTasks(requestresponsedto)), HttpStatus.OK);
	}

	@PostMapping("/trackByUserWithSortingAndPagination")
	public ResponseEntity<RestAPIResponse> trackerByUserWithSortingAndPagination(
			@RequestBody TrackerByUserDTO trackerbyuserdto) {
		logger.info("!!! inside class: TaskController , !! method: trackerByUserWithSortingAndPagination");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched successfully",
				service.trackerByUserWithSortingAndPagination(trackerbyuserdto)), HttpStatus.OK);

	}

	@PostMapping("/taskAssinInfo")
	public ResponseEntity<RestAPIResponse> taskAssinInfoWithSortingAndPagination(
			@RequestBody TicketTrackerSortDTO tickettrackersortdto) {
		logger.info("!!! inside class: TaskController , !! method: taskAssinInfoWithSortingAndPagination");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched  task info successfully",
				service.taskAssignInfoWithSortingAndPagination(tickettrackersortdto)), HttpStatus.OK);

	}

	@RequestMapping(value = "/getTaskReportsWithSorting", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> TaskReportsWithSortingAndPagination(
			@RequestBody TaskReportsDTO taskreportsdto) {
		logger.info("!!! inside class: TaskController , !! method: TaskReportsWithSortingAndPagination");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Reports Fetched successfully",
				service.taskReportsByDepartmentWithSortingAndPagination(taskreportsdto)), HttpStatus.OK);
	}

	@RequestMapping(value = "/getAllTasksRecordsWithSorting", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> allTasksRecordsWithSortingAndPaginaion(
			@RequestBody RequestResponseDTO requestresponsedto) {
		logger.info("!!! inside class: TaskController , !! method: allTasksRecordsWithSortingAndPaginaion");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched successfully",
				service.allTasksRecordsWithSortingAndPagination(requestresponsedto)), HttpStatus.OK);
	}
	
	@PostMapping("/findAllTasks")
	public ResponseEntity<RestAPIResponse> findAllTasks(
			@RequestBody RequestResponseDTO requestresponsedto) {
		logger.info("!!! inside class: TaskController , !! method: findAllTasks");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched all Tasks successfully",
				service.findAllTasks(requestresponsedto)), HttpStatus.OK);
	}
	
	@RequestMapping(value ="/taskByprojectid",method = RequestMethod.POST,produces = "application/json")
	public ResponseEntity<RestAPIResponse> getTaskbyProjectId(@RequestBody RequestResponseDTO requestResponseDTO ){
		logger.info("!!! inside class: TaskController , !! method: getTaskbyProjectId");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "fetched taskByProjectid", service.getTaskByProjectid(requestResponseDTO)), HttpStatus.OK);
		
	}
	
	@RequestMapping(value ="/user/{department}",method = RequestMethod.GET,produces = "application/json")
	public ResponseEntity<RestAPIResponse> findBydeparatmentWiseUsers(@PathVariable String department){
		logger.info("!!! inside class: TaskController , !! method: findBydeparatmentWiseUsers");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "fetched by deparatmentWise users  successfully", service.getUsersByDepartment(department)), HttpStatus.OK);
		
	}
	
}